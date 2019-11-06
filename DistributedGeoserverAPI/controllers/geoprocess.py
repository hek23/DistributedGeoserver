import geojson
from flask import Flask, request, current_app, g
from flask import jsonify
import time
import json
import os
from datetime import datetime
import concurrent
from shapely.geometry import shape
from shapely.ops import triangulate
from shapely.geometry import mapping

from helpers import psqlConnector
from helpers.geoformsGenerator import getWKTQuery, getPolygon
from copy import deepcopy
from concurrent.futures import ThreadPoolExecutor, wait
from root_register import app
import copy

THREADS = current_app.config.get("THREADS")

#{layers: [], geometry: GEOJSON (geometry part), radius: OPTIONAL float, bufferRadius: float}

@current_app.route('/geoprocessing/intersection', methods=['POST'])
def intersection():
    #Here are needed 2 table names
    # {layers: [], geometry: GEOJSON (geometry part), radius: OPTIONAL float, bufferRadius: float}
    # Type: Figure (Polygon, Line, Point, Circle)
    # Radius: If it's Circle => Radius from first point in points. Ommited in other terms
    # bufferRadius: Radius to buffer

    # Check all params
    if not ("layers" in request.json and "geometry" in request.json ): #and "bufferRadius" in request.json
        # FAIL
        return "BAD PARAMETERS"
    if(request.json['geometry'].keys()):
        if (request.json['geometry']['type'].lower == "circle"):
            if ("radius" in request.json):
               if isinstance(request.json['radius'], (int, float)):
                   request.json['radius'] = float(request.json['radius'])
               else:
                   return "Radius must be int or float"
            else:
                return "RADIUS IS NEEDED"

        else:
            # Use radius to generate a circle, with the first point
            request.json['radius'] = 0
    else:
        # Use radius to generate a circle, with the first point
        request.json['radius'] = 0

    layersName = request.json['layers']
    if len(layersName)<1:
        return "Need Layers"
    #Could be ST_WITHIN(the_geom::geometry, {1}::geometry)

    ####################### MULTIPLE LAYERS
    #Attributes must be layername.attr

    #MultiIntersection. Must be (A inter B inter C), etc. A is polygon


    polygon = getWKTQuery(request.json['geometry'], request.json['radius'])

    ## OPTION 1: LAYER BY PROCESS (Each exec goes to one thread). Threads are defined by layers number
    polygons = []
    attributes = []

    #MULTITHREAD START
    start = time.time()

    executor = ThreadPoolExecutor(max_workers=THREADS)
    print("THREADING!")
    processedLayers = []
    #Use minor number between Threads or Layersqty
    for layer in layersName:
        processedResult = executor.submit(threadCall, layer,polygon)
        processedLayers.append(processedResult)
    result = [res.result() for res in wait(processedLayers).done]


    end = time.time()
    print(end-start)
    shapeFinal = merge(result)
    end = time.time()
    print("TIME ELAPSED " +str(end - start))
    return jsonify(shapeFinal)

    #MULTITHREAD END


    ## OPTION 2: POLYGON DISTRIBUTED
    

    #if(len(shapeFinal[])==0): #No response
    #    print(shapeFinal)
    #    return jsonify({"error":"No data suitable to query"}), 404
    #else:
    #    #return jsonify({"polygons": polygons, "attributes": attributes}), 200
    #    return jsonify(result),200    




    ##########################################

def intersectionLayer(layerName, polygon):
    # Generate queries
    columns_names = "SELECT c.column_name as cName FROM information_schema.columns as c WHERE table_name = '{0}' and column_name != 'the_geom' and column_name != 'fid';"
    #intersection = "(SELECT {2} ST_AsGeojson(ST_Transform(ST_Intersection(the_geom::geometry, {1}::geometry)," \
    #               "4326)) FROM {0} where ST_Intersects(the_geom::geometry, {1}::geometry))"


    tableName = layerName.split(".")[-1]
    if(tableName[0].isupper()):
        layerName = layerName.replace(tableName , "\"" + tableName + "\"")
        tableName = "\"" + tableName + "\""
    attributes_layer = columns_names.format(tableName)
    print(layerName)
    
    with app.app_context():
        # get new cursor
        cursor = psqlConnector.getCursor()
        # Get attibutes
        cursor.execute(attributes_layer)
        # Attributes from layer (names)
        information_layer = cursor.fetchall()
        layer = ""
        for attr in information_layer:
            if attr[0][0].isupper():
                layer = layer + "\"" + attr[0] + "\", "
            else:
                layer = layer + attr[0] + ", "

        intersection = "(SELECT {2} ST_AsGeojson(ST_Intersection(ST_Transform(the_geom,3857), {1}))" \
                    "FROM {0} where ST_Intersects(ST_Transform(the_geom,3857), {1}))"

        #intersection = "(SELECT {2} ST_AsGeojson(ST_Transform(ST_Intersection(the_geom::geometry, {1}::geometry),3857))" \
        #               "FROM {0} where ST_Intersects(the_geom::geometry, {1}::geometry))"

        cursor.execute(intersection.format(layerName, polygon, layer))
        #Result will be saved as a new layer, also returned as geom WKT
        #First, save new layer
        #fig = "CREATE TABLE {0}_{1} AS ".format(layersName[0],datetime.now().strftime("%d_%m_%Y_%H_%M_%S")) + intersection
        #cursor.execute(fig)
        result = cursor.fetchall()
        #psqlConnector.get_db().commit()
        cursor.close()
    return information_layer, result

#Polygon in a geoJSON
def dividePolygon(polygon, radius=0):
    minx, miny, maxx, maxy = polygon.bounds
    #remainder goes to last div. Division left to right
    intervalx = (maxx - minx)/THREADS
    intervaly = (maxy - miny)/THREADS
    lines = []
    for i in range(THREADS):
        line = LineString([(minx + i*intervalx, miny), (minx + i*intervalx, maxy)])
        lines.append(line)
    lines.append(getPolygon().boundary)
    merged = linemerge(lines)
    borders = unary_union(merged)
    polygons = polygonize(borders)
    return list(polygons)

#Intersect polygon with layer, and format it as an object
def threadCall(layer, polygon):
    with app.app_context():
        attributes = []
        polygons = []
        information_layer, result = intersectionLayer(layer, polygon)
        layer = layer.split(".")[-1]
        for row in result:
            polygons.append(json.loads(row[-1])) # polygon
            attr = {}
            for attri in range(len(information_layer)):
                attr.update({layer +"." +information_layer[attri][0]: row[attri]})# attr
            attributes.append(attr)
        return {"polygons": polygons, "attributes": attributes}

#With a list of Polygons as objects (threadCall), join them in a format that can be used by Visualization
def merge(polygonResultList):
    #Keep intersected polygon, but need intersect and transform from dict to objects
    #Shapes is List of Objects, which contains Polygons and attributes (lists)

    #shapes = [[shape(polygon) for polygon in results['polygons']] for results in polygonResultList]
    
    #Select shapes[0] as pivot (first to compare) //polygonResultList

    pivot = polygonResultList.pop(0) #  OBJECT!!!!!!
    
    for shp in polygonResultList:
        attributes = []
        polygons = []
        #Each polygon in each shape
        #for polygon in shp['polygons']:
        for polygon in shp['polygons']:
            #Each polygon in pivot
            for pivot_polygon in pivot['polygons']:
                r1 = shape(polygon)
                r2 = shape(pivot_polygon)
                result = r1.intersection(r2)
                if(not(result.is_empty)):
                    piv_idx = pivot['polygons'].index(pivot_polygon)
                    shp_idx = shp['polygons'].index(polygon)
                    attributes.append({**pivot['attributes'][piv_idx], **shp['attributes'][shp_idx]})
                    polygons.append(mapping(result))
        #Now, result is the new pivot!
        result = {"attributes": attributes, "polygons": polygons}
        pivot = copy.deepcopy(result)
    #return results
    return pivot          
        

#@current_app.route('/geoprocessing/')