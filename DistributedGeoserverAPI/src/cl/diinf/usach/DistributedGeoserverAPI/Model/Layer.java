package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Layer {
    static JsonParser parser = new JsonParser();


    public boolean publishLayer(String wsName, String dsName, String layerName){
        //Layer ya en DB. Falta indicar a GS que está.

        //Crear Datastore
        JsonObject layerRoot = new JsonObject();
        JsonObject name = new JsonObject();
        name.addProperty("name", layerName);
        layerRoot.add("featureType", name);
        int layerStatus = RestBridge.sendRest("workspaces/"+wsName+"/datastores/"+dsName+"/featureTypes", layerRoot, "POST").getStatus();
        if(layerStatus == 201){
            return true;
        }
        return false;
    }

    public boolean checkLayer(){
        return true;
    }
}
