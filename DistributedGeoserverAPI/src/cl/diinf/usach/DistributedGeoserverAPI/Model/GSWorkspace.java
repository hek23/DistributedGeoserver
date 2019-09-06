package cl.diinf.usach.DistributedGeoserverAPI.Model;

import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.json.Json;
import java.util.ArrayList;
import java.util.List;

public class GSWorkspace extends Element{
    static JsonParser parser = new JsonParser();
    private String name;
    private String dataStores;
    private String coverageStores;
    private String wmsStores;
    private transient String endpoint = "workspaces";

    public GSWorkspace(){

    }
    public GSWorkspace(String name){
        this.name = name;
    }

    @Override
    public List<GSWorkspace> getAll(){
        JsonElement wsObject;
        List<GSWorkspace> GSWorkspaceList;
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
            System.out.println("WS " + wsObject.toString());
            if(wsObject.isJsonObject()){
                if (wsObject.getAsJsonObject().has("workspace")) {
                    wsObject = wsObject.getAsJsonObject().get("workspace");
                    if (wsObject.isJsonArray()) {
                        GSWorkspaceList = new ArrayList<GSWorkspace>();
                        wsObject.getAsJsonArray().forEach(wspace ->{
                                GSWorkspaceList.add(new GSWorkspace(wspace.getAsJsonObject().get("name").getAsString()));
                        });
                        return GSWorkspaceList;
                    }
                }
            }
            else{
                GSWorkspaceList = new ArrayList<GSWorkspace>();
                return GSWorkspaceList;
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
            System.out.println("param " + param);
            if(param.has("workspace")){
                //param = param.get("workspace").getAsJsonObject();
            //
                if(param.get("workspace").getAsJsonObject().has("name")){
                    if(param.get("workspace").getAsJsonObject().get("name").getAsString()!=""){
                        System.out.println(param);
                        return RestBridge.sendRest(endpoint, param, "POST").getStatus();
                    }
                }
            }
            return 400;
        }
        else{
            //WS

            JsonObject ws = new JsonObject();
            param.addProperty("name",name);
            ws.add("workspace", param);
            System.out.println("CREATE WS FROM DS");
            System.out.println(param);
        }
        return RestBridge.sendRest(endpoint, param, "POST").getStatus();
    }


    @Override
    public void update(Object o, String[] params) {

    }

    @Override
    public void delete(Object o) {

    }

    @Override
    public int exists(String name) {
        return RestBridge.sendRest(endpoint+"/"+name, null, "GET").getStatus();
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
