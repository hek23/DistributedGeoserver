package cl.diinf.usach.DistributedGeoserverAPI.Utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Dataloader {

    private final static Logger log = LogManager.getLogger(Dataloader.class);
    private DataStore dataStore;

    public void establishConnection(Properties params) throws IOException {
        //Genera la conexión con la BD
        this.dataStore = DataStoreFinder.getDataStore(params);
    }

    //Método que establece conexión con la BD para un esquema en particular
    //  El string schema es agregado a la configuración de conexión para sólo acceder a dicho esquema en la BD
    public void establishConnection(Properties params, String schema) throws IOException {
        //Establece el esquema a utilizar
        params.put("schema",schema);
        //Genera la conexión con la BD
        this.dataStore = DataStoreFinder.getDataStore(params);
    }

    //Método que finaliza la conexión con la BD
    public void endConnection() {
        this.dataStore.dispose();
    }

    //Método que crea un esquema dado en la BD
    public boolean createSchema(SimpleFeatureType schema) throws IOException {
        //Primero se verifica si la tabla ya existe en la BD (indiferente del esquema)
        String[] typeNames = dataStore.getTypeNames();
        boolean exists = false;
        String schemaName = schema.getName().getLocalPart();
        for (String name : typeNames) {

            if (schemaName.equalsIgnoreCase(name)) {
                exists = true;
            }
        }
        //Si la tabla no existe se crea
        if (!exists) {
            log.info("Esquema "+schemaName+" creado");
            dataStore.createSchema(schema);
        }
        return exists;
    }

    //Método que borra un esquema dado en la BD
    public void deleteSchema(SimpleFeatureType schema) throws IOException {
        dataStore.removeSchema(schema.getTypeName());
    }

    //Método que escribe en la BD.
    //Retorna verdadero si logra escribir en la BD. Caso contrario retorna falso.
    //  Features contiene los datos a almacenar en la BD acorde a estructura de geotools.
    public boolean writeFeatures(FeatureCollection<SimpleFeatureType, SimpleFeature> features) {
        /** En la función se habla de DATO y TABLA
         *    DATO hace referencia al file (shape en teoría) que se ingresa a la BD
         *    TABLA hace referencia a la tabla que se encuentra en la BD
         */
        boolean match = false;
        if (this.dataStore == null) {
            throw new IllegalStateException(
                    "El Datastore (BD) no puede ser nulo");
        }
        //Se obtiene el esquema del DATO
        //  Esquema es, básicamente, el nombre de las columnas
        SimpleFeatureType schemaDato = features.getSchema();
        //Se obtiene el nombre del DATO
        //  Nombre general de la estructura (debe ser el mismo en la estructura DATO y en la BD
        String nombreDato = schemaDato.getName().getLocalPart();
        try {
            //Nombres de las TABLAS en la BD
            String[] typeNames = this.dataStore.getTypeNames();
            //Se compara que el nombre de la TABLA y el DATO sean iguales
            for (String name : typeNames) {
                if (nombreDato.equalsIgnoreCase(name)) {
                    match = true;
                }
            }
            //Se entrega el error en caso de ocurrir
            if (!match) {
                throw new LinkageError("No existe la TABLA "+nombreDato+" en la BD");
            }

            //Se compara que el esquema de la TABLA y el DATO sean iguales
            Map schemaDatoMap = new HashMap();
            Map schemaDataStore = new HashMap();
            for(int i=0;i<schemaDato.getTypes().size();i++)
            {
                if(schemaDato.getTypes().get(i).getBinding().getCanonicalName().toString().equals("org.locationtech.jts.geom.MultiPolygon")) {
                    schemaDatoMap.put(schemaDato.getTypes().get(i).getBinding(),"the_geom");
                } else {
                    schemaDatoMap.put(schemaDato.getTypes().get(i).getBinding(),schemaDato.getTypes().get(i).getName());
                }
                schemaDataStore.put(this.dataStore.getSchema(nombreDato).getTypes().get(i).getBinding(),this.dataStore.getSchema(nombreDato).getTypes().get(i).getName());
            }
            if(schemaDataStore.size()!=schemaDatoMap.size()) {
                throw new LinkageError("El esquema de la TABLA y el DATO no coinciden");
            }

            if(schemaDatoMap.toString().equals(schemaDataStore.toString())) {
                //Si el esquema de la TABLA y el DATO son iguales se procede a escribir en la BD
                Transaction transaction = new DefaultTransaction("create");
                SimpleFeatureSource featureSource = this.dataStore.getFeatureSource(nombreDato);

                //Proceso de escritura en la BD
                if (featureSource instanceof SimpleFeatureStore) {
                    SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

                    featureStore.setTransaction(transaction);
                    try {
                        List<FeatureId> ids = featureStore.addFeatures(features);
                        transaction.commit();
                    } catch (Exception problem) {
                        problem.printStackTrace();
                        transaction.rollback();
                    } finally {
                        transaction.close();
                    }
                    return true;
                } else {
                    log.error("No se logra escribir en la BD");
                    return false;
                }
            }
            //Se entrega el error en caso de ocurrir
            else {
                throw new LinkageError("El esquema de la TABLA y el DATO no coinciden");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Si llega hasta esta linea implica que no logra escribir en la BD y por tanto retorna false
        log.error("No se logra escribir en la BD");
        return false;
    }
}
