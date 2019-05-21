package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
    private String name;
    private String dataStores;
    private String coverageStores;
    private String wmsStores;

    public Workspace (String name){
        this.name = name;
    }

    public Workspace (RestResponse entity){
        JsonNode jn = entity.getResponseDeserialized().get("workspace");
        this.name = jn.get("name").asText();
        this.dataStores = jn.get("dataStores").asText();
        this.coverageStores=jn.get("coverageStores").asText();
        this.wmsStores=jn.get("wmsStores").asText();
    }

    public static List<Workspace> getAll(){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        List<String[]> headers = new ArrayList<String[]>();
        headers.add(new String[]{"Content-type", "application/json"});
        RestResponse response = RestBridge.sendGet("workspaces", headers);
        ObjectMapper mapper = new ObjectMapper();
        List<Workspace> workspaceList = new ArrayList<Workspace>();
        response.getResponseDeserialized().get("workspaces").get("workspace").forEach(wspace -> workspaceList.add(new Workspace(wspace.get("name").asText())));
        return workspaceList;
    }

    public static int create(String name){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        List<String[]> headers = new ArrayList<String[]>();
        headers.add(new String[]{"Content-type", "application/json"});
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode ob = mapper.createObjectNode();
        ob.put("name", name);
        ObjectNode node = mapper.createObjectNode();
        node.set("workspace",ob);
        RestResponse response = RestBridge.sendPost("workspaces",node, headers);
        System.out.println(response.getStatus());
        return response.getStatus();
    }

    public static Workspace getByName(String name){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        List<String[]> headers = new ArrayList<String[]>();
        headers.add(new String[]{"Content-type", "application/json"});
        RestResponse response = RestBridge.sendGet("/workspaces/"+name,headers);
        return new Workspace(response);
    }

    public static int check (String name){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        List<String[]> headers = new ArrayList<String[]>();
        headers.add(new String[]{"Content-type", "application/json"});
        RestResponse response = RestBridge.sendGet("/workspaces/"+name,headers);
        return response.getStatus();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataStores() {
        return dataStores;
    }

    public void setDataStores(String dataStores) {
        this.dataStores = dataStores;
    }

    public String getCoverageStores() {
        return coverageStores;
    }

    public void setCoverageStores(String coverageStores) {
        this.coverageStores = coverageStores;
    }

    public String getWmsStores() {
        return wmsStores;
    }

    public void setWmsStores(String wmsStores) {
        this.wmsStores = wmsStores;
    }
}
