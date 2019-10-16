import json
from shapely.geometry import shape


def getWKTQuery(geojsonObject, radius=0):
   o = {
      "coordinates": [[[23.314208, 37.768469], [24.039306, 37.768469], [24.039306, 38.214372], [23.314208, 38.214372], [23.314208, 37.768469]]],
      "type": "MultiLineString"
   }

   if(geojsonObject == {} ):
      return "ST_Transform(the_geom,3857)"

   figure = "ST_GeomFromText('"+getPolygon(geojsonObject,radius).wkt + "',3857)"
   
   return figure


def getPolygon(geojsonObject,radius=0):

   if (geojsonObject['type'].lower() == "circle"):
      geojsonObject['type'] = "Point"
      geojsonObject['coordinates'] = geojsonObject['coordinates'][0][0]

   g2 = shape(geojsonObject)

   if (radius != 0):
      g2 = g2.buffer(float(radius))
   
   return g2


