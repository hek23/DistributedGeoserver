package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

    public static int check (String name, String wsname){
        RestResponse response = RestBridge.sendRest("/workspaces/"+wsname+"/datastores/"+name, null, "GET");
        return response.getStatus();
    }

    public JsonObject generate(String key, Object value){
        JsonObject data = new JsonObject();
        data.addProperty("@key", key);
        switch (value.getClass().getSimpleName().toLowerCase()){
            case "string":
                data.addProperty("$", (String) value);
                break;
            case "boolean":
                data.addProperty("$", (boolean) value);
                break;
            case "integer":
                data.addProperty("$", (Number) value);
                break;
            default:
                System.out.println(value.getClass().getSimpleName().toLowerCase());
                break;
        }
        return data;
    }

    @Override
    public List getAll() {
        return null;
    }

    @Override
    public int create(String body) {
        //Procesar el cuerpo
        JsonObject param = parser.parse(body).getAsJsonObject();
        //revisar si tiene los nombres
        if (param.has("workspace")) {
            if (param.get("workspace").getAsString() != "") {
                if (param.has("datastore")) {
                    if (param.get("datastore").getAsString() != "") {
                        //crear objeto para crear Datastore
                        //Verificar si esta el workspace
                        if (RestBridge.sendRest("workspaces", null, "GET").getStatus() != 200) {
                            //Workspace noexiste
                            //Crear Workspace
                            if(new Workspace().create(param.get("workspace").getAsString()) !=201){
                                //No pudo crearse
                                return 400;
                            }
                        }
                        //Crear Datastore
                        JsonObject dsRoot = new JsonObject();
                        JsonArray dsArray = new JsonArray();
                        JsonObject entry = new JsonObject();
                        //Generar entradas
                        dsArray.add(generate("port", port));
                        dsArray.add(generate("host", host));
                        dsArray.add(generate("database", dbname));
                        dsArray.add(generate("user", user));
                        dsArray.add(generate("passwd", pass));
                        dsArray.add(generate("dbtype", "postgis"));
                        dsArray.add(generate("create database", true));
                        dsArray.add(generate("Batch insert size", 100));
                        //Generar objeto
                        dsRoot.addProperty("name",param.get("datastore").getAsString());
                        entry.add("entry",dsArray);
                        dsRoot.add("connectionParameters",entry);
                        JsonObject dsBody = new JsonObject();
                        dsBody.add("dataStore", dsRoot);
                        return RestBridge.sendRest("workspaces/"+param.get("workspace").getAsString()+"/datastores",dsBody, "POST").getStatus();

                    }

                }
            }
        }
        return 400; //(return directo?)
    }

    @Override
    public void update(Object o, String[] params) {

    }

    @Override
    public void delete(Object o) {

    }
}
