package cl.diinf.usach.DistributedGeoserverAPI;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class Workspace {
    String name;

    public Workspace (String name){
        this.name = name;
    }

    public static ArrayList<Workspace> getAll(){
        return null;
    }

    public static Workspace get(String name){
        return null;
    }

    /*public boolean create(){
        ObjectMapper mapper = new ObjectMapper();
        (ObjectNode) mapper.createObjectNode().put("name",this.name);
        return true;
    }*/

}
