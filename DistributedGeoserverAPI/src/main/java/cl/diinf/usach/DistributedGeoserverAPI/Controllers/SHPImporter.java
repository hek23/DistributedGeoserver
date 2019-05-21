package cl.diinf.usach.DistributedGeoserverAPI.Controllers;

import cl.diinf.usach.DistributedGeoserverAPI.FileStorageService;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Datastore;
import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Workspace;
import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@RestController
class SHPImporter {

    @Autowired
    private FileStorageService fileStorageService;
    //@Autowired
    //private UsernamePasswordCredentials geoserverAuth;

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public Object importSHPFiles(@RequestParam("filename") String name,
                                 @RequestParam("coordinateSystem") String coordSystem,
                                 @RequestParam("workspace") String workspaceName,
                                 @RequestParam("file") MultipartFile file) {
        //First of all, we need to check the workspace
        if(Workspace.check(workspaceName)==404){
            //Doesn't Exist. So, create
            Workspace.create(workspaceName);
        }
        //Then check Datastore
        if(Datastore.check(name,workspaceName) == 404){
            Datastore.create(name, workspaceName);
        }
        //Create JsonMapper
        ObjectMapper mapper = new ObjectMapper();
        //Create base node
        ObjectNode importnode = mapper.createObjectNode();
        //Init Import object
        //Init Datastore
        ObjectNode datastore = mapper.createObjectNode();
        datastore.put("name", name);
        ObjectNode targetStore = mapper.createObjectNode();
        targetStore.set("dataStore", datastore);
        //Init Workspace
        ObjectNode workspace = mapper.createObjectNode();
        workspace.put("name", workspaceName);
        ObjectNode targetWorkspace = mapper.createObjectNode();
        targetWorkspace.set("workspace", workspace);
        //Now, root!
        importnode.set("targetStore", targetStore);
        importnode.set("targetWorkspace", targetWorkspace);
        ObjectNode importdefinition = (ObjectNode) mapper.createObjectNode().set("import", importnode);
        ArrayList<String[]> headers = new ArrayList<String[]>();
        headers.add(new String[]{"Content-type", "application/json"});

        //Now, that importdefinition is done, send to Geoserver
        RestResponse rr = RestBridge.sendPost("imports", importdefinition,headers);
        //Now we hace importID. Son we must send the file to create Task
        String importID = rr.getResponseDeserialized().get("import").get("id").asText();


        //File treatment
        //Save file
        fileStorageService.storeFile(file);

        //Use to send

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("filedata", fileStorageService.loadFileAsResource(name+".zip"));
        body.add("name",file.getName());

        RestResponse formResponse = RestBridge.sendForm("imports/"+importID+"/tasks",body);
        //Clean file
        try {

            Files.deleteIfExists(fileStorageService.getFileStorageLocation().resolve(name+".zip"));
        } catch (IOException e) {
            System.out.println("fallo");
        }

        //Reset to Postgis

        MultiValueMap<String, Object> pgReset = new LinkedMultiValueMap<>();
        pgReset.add("dataStore", "{\"name\":\"postgis\"}");
        try {
            System.out.println("imports/"+importID+"/tasks/"+ mapper.readTree(formResponse.getResponseSerialized()).get("task").get("id").asText()+"/target");
            RestBridge.sendPut("imports/"+importID+"/tasks/"+ mapper.readTree(formResponse.getResponseSerialized()).get("task").get("id").asText()+"/target",pgReset);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Activate Processing
        RestBridge.sendPost2("imports/"+importID, null);
        /*try {
            //mapper.readTree("{\"dataStore\": }");
            MultiValueMap<String, Object> resetPg = new LinkedMultiValueMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return importdefinition;
    }


}
