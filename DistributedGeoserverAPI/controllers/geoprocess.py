
from flask import request, current_app
from helpers.geoformsGenerator import getWKTQuery, getPolygon
from helpers.intersector import processIntersection
from helpers.formatter import resultFormatter


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

    polygon = getWKTQuery(request.json['geometry'], request.json['radius'])

    return resultFormatter(processIntersection(layersName,polygon))
