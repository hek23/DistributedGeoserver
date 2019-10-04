import geojson
from flask import Flask, request, current_app, g
from flask import jsonify

import json
import os
from datetime import datetime

from shapely.geometry import shape
from shapely.ops import triangulate

from helpers import psqlConnector
from helpers.geoformsGenerator import getWKTQuery


def prepareArea(geometry):
    s = json.dumps(geometry)
    g1 = geojson.loads(s)
    g2 = shape(g1)
    areas = triangulate(g2)

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

    columns_names = "SELECT c.column_name as cName FROM information_schema.columns as c WHERE table_name = '{0}' and column_name != 'the_geom' and column_name != 'fid';"
    #intersection = "(SELECT {2} ST_AsGeojson(ST_Transform(ST_Intersection(the_geom::geometry, {1}::geometry)," \
    #               "4326)) FROM {0} where ST_Intersects(the_geom::geometry, {1}::geometry))"

    intersection = "(SELECT {2} ST_AsGeojson(ST_Intersection(the_geom::geometry, {1}::geometry))" \
                   "FROM {0} where ST_Intersects(the_geom::geometry, {1}::geometry))"

    #Could be ST_WITHIN(the_geom::geometry, {1}::geometry)

    ####################### MULTIPLE LAYERS
    #
    #cols = [columns_names.format(shape_name.split[1]) for shape_name in layersName]

    



    ##########################################



    # Generate queries
    attributes_layer = columns_names.format(layersName[0].split(".")[1])
    # get new cursor
    cursor = psqlConnector.getCursor()
    # Get attibutes
    cursor.execute(attributes_layer)
    # Attributes from layer (names)
    information_layer = cursor.fetchall()
    layer = ""
    for attr in information_layer:
        layer = layer + attr[0] + ", "

    intersection = intersection.format(layersName[0], getWKTQuery(request.json['geometry'], request.json['radius']), layer)
    print(intersection)
    cursor.execute(intersection)
    #Result will be saved as a new layer, also returned as geom WKT
    #First, save new layer
    #fig = "CREATE TABLE {0}_{1} AS ".format(layersName[0],datetime.now().strftime("%d_%m_%Y_%H_%M_%S")) + intersection
    #cursor.execute(fig)
    result = cursor.fetchall()
    #psqlConnector.get_db().commit()
    cursor.close()
    polygons = []
    attributes = []
    for row in result:
        polygons.append(json.loads(row[-1])) # polygon
        attr = {}
        for attri in range(len(information_layer)):
            print(row[attri])
            attr.update({information_layer[attri][0]: row[attri]})# attr
        attributes.append(attr)
    if(len(result)==0): #No response
        return jsonify({"error":"No data suitable to query"}), 404
    else:
        return jsonify({"polygons": polygons, "attributes": attributes}), 200


#@current_app.route('/geoprocessing/')