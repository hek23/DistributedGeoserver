package cl.diinf.usach.DistributedGeoserverAPI.Controllers;

import cl.diinf.usach.DistributedGeoserverAPI.FileStorageService;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Datastore;
import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Workspace;
import cl.diinf.usach.DistributedGeoserverAPI.Utilities.RestBridge;
import cl.diinf.usach.DistributedGeoserverAPI.Utilities.SHPManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

@RestController
class SHPImporter {

    private SHPManager shpManager;

    SHPImporter() {
        this.shpManager = new SHPManager();
    }

    @PostMapping("/import")
    @ResponseBody
    public ResponseEntity importSHPFiles(@RequestParam("datastore") String dsname,
                                 @RequestParam("coordinateSystem") String coordSystem,
                                 @RequestParam("workspace") String workspaceName,
                                 @RequestParam("file") MultipartFile file) {

        shpManager.importSHPFiles(dsname, coordSystem, workspaceName, file);
        return new ResponseEntity(HttpStatus.CREATED);
    }
    @GetMapping("/cap")
    @ResponseBody
    public void getCap(){
        RestBridge.sendRest(null, null, "GET");
    }

}
