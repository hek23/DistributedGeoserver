package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cl.diinf.usach.DistributedGeoserverAPI.Utilities.JsonUtil.toJson;

//@ConfigurationProperties(prefix = "db")
public class Datastore implements Dao{
    static JsonParser parser = new JsonParser();
    String name;
    static String host = "localhost";
    static String port = "25434";
    static String dbname = "postgres";
    static String user = "docker";
    static String pass = "docker";

    public Datastore (String name){
        this.name = name;
    }

    public static int create(String name, String wname){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        ArrayList<Object> entry =  new ArrayList();
        entry.add(generate("host", host));
        entry.add(generate("port", port));
        entry.add(generate("database", dbname));
        entry.add(generate("user", user));
        entry.add(generate("passwd", pass));
        entry.add(generate("dbtype", "postgis"));
        entry.add(generate("create database", true));
        entry.add(generate("Batch insert size", 100));

        Map<String, Object> dataStore = new HashMap<>();
        dataStore.put("name",name);
        dataStore.put("connectionParameters", entry);
        Map<String, Object> servObj = new HashMap<>();
        servObj.put("dataStore", dataStore);

        JsonObject body = parser.parse(toJson(servObj)).getAsJsonObject();

        RestResponse response = RestBridge.sendRest("/workspaces/"+wname+"/datastores", body,"POST");
        //RestResponse response = RestBridge.sendPost("/workspaces/"+wname+"/datastores", ob, headers);
        return response.getStatus();

    }

    public static int check (String name, String wsname){
        RestResponse response = RestBridge.sendRest("/workspaces/"+wsname+"/datastores/"+name, null, "GET");
        return response.getStatus();
    }

    public static Map<String, Object> generate(String key, Object value){
        Map<String, Object> data = new HashMap<>();
        data.put("@key", key);
        data.put("$", value);
        return data;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public int create(String body) {
        //Procesar el cuerpo

        //Revisar si tiene el nombre
        return 0;

    }

    @Override
    public void update(Object o, String[] params) {

    }

    @Override
    public void delete(Object o) {

    }
}
