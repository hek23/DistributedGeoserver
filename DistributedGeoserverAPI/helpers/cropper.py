from helpers import psqlConnector
from root_register import app as globalApp

def cropByPolygon(layerName, polygon):
    # Generate queries
    columns_names = "SELECT c.column_name as cName FROM information_schema.columns as c WHERE table_name = '{0}' and column_name != 'the_geom' and column_name != 'fid';"

    tableName = layerName.split(".")[-1]
    if(tableName[0].isupper()):
        layerName = layerName.replace(tableName , "\"" + tableName + "\"")
        #tableName = "\"" + tableName + "\""
    attributes_layer = columns_names.format(tableName)
    #print(attributes_layer)
    
    with globalApp.app_context():
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