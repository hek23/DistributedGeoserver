
from flask import Flask, request, current_app, g
from flask import jsonify

import time
import json
import os
import geojson
from datetime import datetime
from copy import deepcopy

from concurrent.futures import ThreadPoolExecutor, wait, as_completed
from multiprocessing import Process, Queue, Pool

from shapely.geometry import shape
from shapely.ops import triangulate
from shapely.geometry import mapping

from helpers import psqlConnector
from helpers.geoformsGenerator import getWKTQuery, getPolygon
from helpers.parallelGenerator import getProcessorPool, getThreads
from root_register import app

from flask_cors import CORS

from numpy import asarray, array_split, array, ndarray
from math import floor


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
###################################################################################################################################
    
    return resultFormatter(intersection(layersName,polygon))
###########################################    #MULTITHREAD END #####################################################################


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
        #print("Return from db")
    return information_layer, result

# Return array of objects {"attributes": {}, "polygons": {}}
def formatShape(layer,polygon):
    with app.app_context():
        results = []
        information_layer, result = intersectionLayer(layer, polygon)
        layer = layer.split(".")[-1]
        for row in result:
            #Each result will be one object
            attr = {}
            for attri in range(len(information_layer)):
                attr.update({layer +"." +information_layer[attri][0]: row[attri]}) # attr
            results.append({"polygons": json.loads(row[-1]), "attributes": attr})
        return results

#Intersect two shapes or one and a list of them formatted.
#Is recursive
#If shape2 is empty list, returns
def intersectShapes(shapeList):
    if(len(shapeList) == 1):
        #Recursive finish
        return shapeList[0]
    #Before parallelize, order shapes, using polygon quantity
    #shapeList = [shape1,shape2]
    shapeList.sort(key = len)
    #The smallest shape will be used as a constant for comparison
    #splitting the other between threads
    smallShape = shapeList.pop(0)
    polygonList = array(shapeList.pop(0))
    num = min(len(os.sched_getaffinity(0)), current_app.config["NODES"])

    polygonList = array_split(polygonList,num)

    #MULTITHREAD START
    executor = getThreads()
    processedLayers = []
    for polygonSet in polygonList:
        processedResult = executor.submit(intersect, polygonSet, smallShape)
        processedLayers.append(processedResult)
    #At processedLayers will be a list of objects like 
    # {attributes: [], polygons:[]} that need to be joined
    results = []
    for res in as_completed(processedLayers):
        results.extend(res.result())

    shapeList.append(results)
    return intersectShapes(shapeList)

#Shape1 and Shape2 are list of {attributes: [], polygons:[]} Return same format
def intersect(shape1, shape2):
    res = []
    #Each polygon in each shape
    for polygon1 in shape1:    
        for polygon2 in shape2:
            #Transform to object
            r1 = shape(polygon1['polygons'])
            r2 = shape(polygon2['polygons'])
            #Calculate intersection
            result = r1.intersection(r2)
            #If is not empty, add to result
            if(not(result.is_empty)):
                res.append({"attributes": {**polygon1['attributes'], **polygon2['attributes']}, "polygons": mapping(result)})
    return res      

def intersection(layersName, polygon):
    layerSet = []
    #Get layers info (uses Threads for paralell query)
    executor = getThreads(len(layersName))
    for layer in layersName:
        formattedLayer = executor.submit(formatShape, layer,polygon)
        layerSet.append(formattedLayer)
    results = []
    for res in as_completed(layerSet):
        results.append(res.result())
    #Call till layerSet is empty
    polygons = intersectShapes(results)
    return polygons
    
#Reformats to {attributes:[], polygons:[]}. Must be corresponding
def resultFormatter(objectList):
    #As indexes must match, a controlled and limited access to
    #final data containers are done
    #Distribute the objectList on "even" parts
    threadCount = min(len(os.sched_getaffinity(0)), current_app.config["NODES"])
    #Transform list to array, for array_split use
    objectList = array_split(array(objectList),threadCount)
    futureRes = []
    executor = getThreads()
    #Now, each element from objectList is ready to work parallel
    for partialList in objectList:
        splitted = executor.submit(splitter, partialList)
        futureRes.append(splitted)
    #We care order, so add to results, only if finished
    polygons = []
    attributes = []
    for res in as_completed(futureRes):
        attributes.extend(res.result()[0])
        polygons.extend(res.result()[1])
    return jsonify({"attributes":attributes, "polygons": polygons})

def splitter(partialList):
    return [[obj["attributes"] for obj in partialList],[obj["polygons"] for obj in partialList]]