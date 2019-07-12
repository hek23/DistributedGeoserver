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
        MultiValueMap<String, Object> dataStore = new LinkedMultiValueMap<>();
        dataStore.add("name",name);


        ObjectNode connectionParameters = mapper.createObjectNode();
        ArrayNode entry = mapper.createArrayNode();
        //Create internal entry
        ObjectNode keyBind = mapper.createObjectNode();
        keyBind.put("@key","host");
        keyBind.put("$", host);
        //add to array
        entry.add(keyBind);
        //Do again
        keyBind = mapper.createObjectNode();
        keyBind.put("@key","port");
        keyBind.put("$",port);
        entry.add(keyBind);
        keyBind = mapper.createObjectNode();
        //Do again
        keyBind.put("@key","database");
        keyBind.put("$","geoserverDBTest");
        entry.add(keyBind);
        keyBind = mapper.createObjectNode();
        //Do again
        keyBind.put("@key","user");
        keyBind.put("$",user);
        entry.add(keyBind);
        keyBind = mapper.createObjectNode();
        //Do again
        keyBind.put("@key","passwd");
        keyBind.put("$",pass);
        entry.add(keyBind);
        keyBind = mapper.createObjectNode();
        keyBind.put("@key","dbtype");
        keyBind.put("$","postgis");

        entry.add(keyBind);
        keyBind = mapper.createObjectNode();
        keyBind.put("@key","Batch insert size");
        keyBind.put("$",100);
        entry.add(keyBind);
        keyBind = mapper.createObjectNode();
        keyBind.put("@key","create database");
        keyBind.put("$",true);
        entry.add(keyBind);

        System.out.println(entry);
        connectionParameters.set("entry",entry);
        dataStore.set("connectionParameters", connectionParameters);
        ObjectNode ob = (ObjectNode) mapper.createObjectNode().set("dataStore", dataStore);

        RestResponse response = RestBridge.sendPost("/workspaces/"+wname+"/datastores", ob, headers);
        return response.getStatus();
    }

    public static int check (String name, String wsname){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        List<String[]> headers = new ArrayList<String[]>();
        headers.add(new String[]{"Content-type", "application/json"});
        RestResponse response = RestBridge.sendGet("/workspaces/"+wsname+"/datastores/"+name,headers);
        return response.getStatus();
    }
}
