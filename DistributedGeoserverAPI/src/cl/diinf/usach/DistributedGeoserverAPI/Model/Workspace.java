package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.entity.StringEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Workspace extends Element{
    static JsonParser parser = new JsonParser();
    private String name;
    private String dataStores;
    private String coverageStores;
    private String wmsStores;
    private String endpoint = "workspaces";

    public Workspace(){

    }
    public Workspace (String name){
        this.name = name;
    }

    @Override
    public List<Workspace> getAll(){
        JsonElement wsObject;
        List<Workspace> workspaceList;
        //Se envía solicitud al servicio para obtención de Workspace, con RestBridge
        RestResponse response = RestBridge.sendRest(endpoint, null, "GET");
        //revisar si se pudo hacer la solicitud
        if(response.getResponse() == null){
            return null;
        }

        //Chequear, llega Arreglo en el JsonObject
        if(response.getResponse().has("workspaces")) {
            //Respuesta ok!. Ahora recorrer cada fase
            wsObject = response.getResponse().get("workspaces");
            if(!wsObject.getAsString().isEmpty()){
                if (wsObject.getAsJsonObject().has("workspace")) {
                    wsObject = wsObject.getAsJsonObject().get("workspace");
                    if (wsObject.isJsonArray()) {
                        workspaceList = new ArrayList<Workspace>();
                        wsObject.getAsJsonArray().forEach(wspace -> workspaceList.add(new Workspace(wspace.getAsString())));
                        return workspaceList;
                    }
                }
            }
            else{
                workspaceList = new ArrayList<Workspace>();
                return workspaceList;
            }

        }
        System.out.println("Retorno");
        return null;
    }

    @Override
    public int create(String name) {
        //Revisar si viene de rest o de Datastore
        JsonObject param = new JsonObject();
        if(parser.parse(name).isJsonObject()) {
            //REST
            param = parser.parse(name).getAsJsonObject();
            //
            if(param.has("workspace")){
                param = param.get("workspace").getAsJsonObject();
            //
                if(param.has("name")){
                    if(param.get("name").getAsString()!=""){
                        return RestBridge.sendRest(endpoint, param, "POST").getStatus();
                    }
                }
            }
            return 400;
        }
        else{
            //WS
            param.addProperty("name",name);
        }
        return RestBridge.sendRest(endpoint, param, "POST").getStatus();
    }


    @Override
    public void update(Object o, String[] params) {

    }

    @Override
    public void delete(Object o) {

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
