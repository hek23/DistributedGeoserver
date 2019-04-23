package cl.diinf.usach.DistributedGeoserverAPI;

import cl.diinf.usach.Model.ImportInfo;
import cl.diinf.usach.Model.Importer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
class SHPImporter {

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public ObjectNode importSHPFiles(@RequestBody ImportInfo resource) throws JsonProcessingException {
        Importer imp = new Importer(resource.storeName, resource.workspaceName);
        ObjectMapper mapper = new ObjectMapper();
        //mapper.writeValue(System.out, imp);ma

        ObjectNode on = mapper.createObjectNode();
        on.set("import", mapper.valueToTree(imp));
        //String jsonString = mapper.writeValueAsString("import",imp);
        return on;

    }
}
