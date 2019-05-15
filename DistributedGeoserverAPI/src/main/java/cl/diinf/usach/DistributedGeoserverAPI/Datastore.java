package cl.diinf.usach.DistributedGeoserverAPI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.lang.reflect.Array;
import java.util.ArrayList;

/*
{
  "dataStore": {
    "name": "nyc",
    "connectionParameters": {
      "entry": [
        {"@key":"host","$":"localhost"},
        {"@key":"port","$":"5432"},
        {"@key":"database","$":"nyc"},
        {"@key":"user","$":"bob"},
        {"@key":"passwd","$":"postgres"},
        {"@key":"dbtype","$":"postgis"}
      ]
    }
  }
}

 */
public class Datastore {
    String name;
    ArrayNode entry;

    public Datastore (String name){
        this.name = name;
    }

    public static ArrayList<Datastore> getAll(){
        return null;
    }

    public static Datastore get(String name){
        return null;
    }

    public boolean create(){
        return true;
    }
}
