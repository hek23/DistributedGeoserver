import json
import geojson
from shapely.geometry import shape

#https://gist.github.com/drmalex07/5a54fc4f1db06a66679e
def getWKTQuery(geojsonObject, radius=0):
   o = {
      "coordinates": [[[23.314208, 37.768469], [24.039306, 37.768469], [24.039306, 38.214372], [23.314208, 38.214372], [23.314208, 37.768469]]],
      "type": "MultiLineString"
   }

   if(geojsonObject == {} ):
      return "the_geom"

   if (geojsonObject['type'].lower() == "circle"):
      geojsonObject['type'] = "Point"
      geojsonObject['coordinates'][0] = [geojsonObject['coordinates'][0][0]]

   s = json.dumps(geojsonObject)

   # Convert to geojson.geometry.Polygon
   g1 = geojson.loads(s)

   # Feed to shape() to convert to shapely.geometry.polygon.Polygon
   # This will invoke its __geo_interface__ (https://gist.github.com/sgillies/2217756)
   g2 = shape(g1)
   figure = "ST_GeomFromText('"+g2.wkt + "',ST_SRID(the_geom))"
   # Now check if radius is on there
   if (radius != 0):
      #As radius is not 0, means that is a circle
      figure = "ST_Buffer(" + figure + ", " +str(radius) +");"
   return figure



