package cl.diinf.usach.DistributedGeoserverAPI.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class RestResponse {
    private int status;
    private JsonNode response;

    public RestResponse(ResponseEntity<String> response){
        this.status = response.getStatusCodeValue();
        try {
            this.response = new ObjectMapper().readTree(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getStatus() {
        return this.status;
    }

    public JsonNode getResponse() {
        return this.response;
    }
}
