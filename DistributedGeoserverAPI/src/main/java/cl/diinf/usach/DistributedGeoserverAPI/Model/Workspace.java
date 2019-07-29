package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
        JsonNode jn = entity.getResponse().get("workspace");
        this.name = jn.get("name").asText();
        this.dataStores = jn.get("dataStores").asText();
        this.coverageStores=jn.get("coverageStores").asText();
        this.wmsStores=jn.get("wmsStores").asText();
    }

    public static List<Workspace> getAll(){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        RestResponse response = RestBridge.sendRest("workspaces", null, "GET");
        ObjectMapper mapper = new ObjectMapper();
        List<Workspace> workspaceList = new ArrayList<Workspace>();
        response.getResponse().get("workspaces").get("workspace").forEach(wspace -> workspaceList.add(new Workspace(wspace.get("name").asText())));
        return workspaceList;
    }

    public static int create(String name){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge


        MultiValueMap<String, Object> ws = new LinkedMultiValueMap<>();
        ws.add("name",name);
        MultiValueMap<String, Object> wsBody = new LinkedMultiValueMap<>();
        wsBody.add("workspace", ws);
        RestResponse response = RestBridge.sendRest("workspaces",wsBody, "POST");
        System.out.println(response.getStatus());
        return response.getStatus();
    }

    public static Workspace getByName(String name){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        RestResponse response = RestBridge.sendRest("/workspaces/"+name,null,"GET");
        return new Workspace(response);
    }

    public static int check (String name){
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        RestResponse response = RestBridge.sendRest("/workspaces/"+name,null, "GET");
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
