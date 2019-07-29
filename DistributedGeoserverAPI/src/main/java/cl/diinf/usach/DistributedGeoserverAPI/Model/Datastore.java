package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

//@ConfigurationProperties(prefix = "db")
public class Datastore {
    String name;
    static String host = "mainPostgres";
    static String port = "5432";
    static String dbname = "postgres";
    static String user = "docker";
    static String pass = "docker";

    public Datastore (String name){
        this.name = name;
    }

    public static ArrayList<Datastore> getAll(){
        return null;
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
        MultiValueMap<String, Object> dataStore = new LinkedMultiValueMap<>();
        dataStore.add("name",name);
        dataStore.add("connectionParameters", entry);
        MultiValueMap<String, Object> servObj = new LinkedMultiValueMap<>();
        servObj.add("dataStore", dataStore);

        RestResponse response = RestBridge.sendRest("/workspaces/"+wname+"/datastores",servObj,"POST");
        //RestResponse response = RestBridge.sendPost("/workspaces/"+wname+"/datastores", ob, headers);
        return response.getStatus();
    }

    public static int check (String name, String wsname){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        //RestResponse response = RestBridge.sendGet("/workspaces/"+wsname+"/datastores/"+name,headers);
        RestResponse response = RestBridge.sendRest("/workspaces/"+wsname+"/datastores/"+name, null, "GET");
        return response.getStatus();
    }

    public static MultiValueMap generate(String key, Object value){
        //{"@key":"host","$":"localhost"}
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("@key", key);
        data.add("$", value);
        return data;
    }

}
