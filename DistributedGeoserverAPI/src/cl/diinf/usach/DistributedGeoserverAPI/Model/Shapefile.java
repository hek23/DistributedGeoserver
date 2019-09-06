package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Configuration.Parameters;
import cl.diinf.usach.DistributedGeoserverAPI.Utilities.Dataloader;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Parameter;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

//OUT
public class Shapefile {


    public static FeatureCollection<SimpleFeatureType, SimpleFeature> convertShapeToFeature(File shapeFile) {
        try {
            //Se abre el fichero para acceder a su información con la estructura de geotools
            DataStore inputDataStore = DataStoreFinder.getDataStore(Collections.singletonMap("url", shapeFile.toURI().toURL()));

            //Nombre del fichero limpio (sin path del fichero)
            String inputFileName = inputDataStore.getTypeNames()[0];

            //Se obtiene el esquema del fichero y la información (tuplas) del fichero
            SimpleFeatureType inputType = inputDataStore.getSchema(inputFileName);

            FeatureSource<SimpleFeatureType, SimpleFeature> source = inputDataStore.getFeatureSource(inputFileName);
            return source.getFeatures();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Feature a BD
    public static byte storeFeatureToBD(FeatureCollection<SimpleFeatureType, SimpleFeature> toSave, String schema) {

        Properties params = Parameters.dbparams();
        Dataloader writer = null;

        try {
            writer = new Dataloader();
            writer.establishConnection(params, schema);

            System.out.println(toSave.getSchema());

            if (!writer.createSchema(toSave.getSchema())) {
                //log.info("Escribiendo capa "+fileName+" en BD");


                writer.writeFeatures(toSave);
                return 1;
                //En cualquier otro caso la tabla ya existe en la BD
            } else {
                //log.warn("Tabla ya existe en BD con nombre: " + schemaName);
                System.out.println("Tabla ya existe en BD con nombre: " + schema);
            }
            return 0;
        } catch (/*SchemaException | */IOException e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            //Se finaliza la conexión con la BD
            if (writer != null) {
                writer.endConnection();
            }
        }
        return -1;
    }

    public static byte saveSHP(File shp, String schema) {
        if (schema.isEmpty() || shp == null){
            return -2;
        }
        return storeFeatureToBD(convertShapeToFeature(shp), schema);
    }
}
