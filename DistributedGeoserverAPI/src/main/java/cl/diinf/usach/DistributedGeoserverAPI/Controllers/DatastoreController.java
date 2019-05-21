package cl.diinf.usach.DistributedGeoserverAPI.Controllers;

import cl.diinf.usach.DistributedGeoserverAPI.Model.Datastore;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

@RestController
public class DatastoreController {

    @PostMapping("/workspaces/{wname}/datastores/")
    public int create(@RequestBody JsonNode params, @PathVariable String wname){
        return Datastore.create(params.get("name").asText(),wname);
    }
}
