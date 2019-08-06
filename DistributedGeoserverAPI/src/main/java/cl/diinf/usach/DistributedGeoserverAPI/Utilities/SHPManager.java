package cl.diinf.usach.DistributedGeoserverAPI.Utilities;

import cl.diinf.usach.DistributedGeoserverAPI.FileStorageService;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Datastore;
import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

public class SHPManager {

    @Autowired
    public FileStorageService fileStorageService;

    public ResponseEntity importSHPFiles(String dsname,
                                         String coordSystem,
                                         String workspaceName,
                                         MultipartFile file) {

        //Create JsonMapper
        //ObjectMapper mapper = new ObjectMapper();
        //First of all, we need to check the workspace
        if(Workspace.check(workspaceName)==404){
            //Doesn't Exist. So, create
            Workspace.create(workspaceName);
        }
        //Then check Datastore. Get out if exist!
        if(Datastore.check(dsname,workspaceName) == 404){
            Datastore.create(dsname, workspaceName);
        }
        MultiValueMap<String, Object> importNode = new LinkedMultiValueMap<>();
        //Datastore
        MultiValueMap<String, Object> store = new LinkedMultiValueMap<>();
        store.add("name", dsname);
        MultiValueMap<String, Object> target = new LinkedMultiValueMap<>();
        target.set("dataStore", store);
        //Add datastore to root
        importNode.add("targetStore",target);
        //now workspace
        store.set("name",workspaceName);
        target.clear();
        target.set("workspace",store);
        importNode.add("targetWorkspace", target);
        MultiValueMap<String, Object> importer =  new LinkedMultiValueMap<>();
        importer.set("import", importNode);
        //Now, that importdefinition is done, send to Geoserver
        RestResponse rr = RestBridge.sendRest("imports", importer, "POST");
        //Now we get importID. So we must send the file to create Task
        String importID = rr.getResponse().get("import").get("id").asText();
        //File treatment
        //Save file

        String filename = fileStorageService.storeFile(file);

        //Use to send

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("filedata", fileStorageService.loadFileAsResource(filename));
        body.add("name",filename);

        RestResponse formResponse = RestBridge.sendRest("imports/"+importID+"/tasks", body, "FORM");
        //Clean file
        try {

            Files.deleteIfExists(fileStorageService.getFileStorageLocation().resolve(filename));
        } catch (IOException e) {

        }

        //Reset to Postgis

        MultiValueMap<String, Object> pgReset = new LinkedMultiValueMap<>();
        pgReset.add("dataStore", "{\"name\":\"postgis\"}");
        //Se quitó "target" al final del endpoint
        RestBridge.sendRest("imports/"+importID+"/tasks/"+ formResponse.getResponse().get("task").get("id").asText(),pgReset, "POST");

        //Activate Processing

        RestBridge.sendRest("imports/"+importID+"/?async=true", null, "POST");

        //Links para consulta de estado e info
        MultiValueMap<String, Object> links = new LinkedMultiValueMap<>();
        links.add("state", "/import/"+"id"+"/task/" + "id");
        links.add("layerInfo", "/import/"+"id");

        return new ResponseEntity(links, HttpStatus.CREATED);
    }
}
