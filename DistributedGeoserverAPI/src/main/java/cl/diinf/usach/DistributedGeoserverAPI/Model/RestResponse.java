package cl.diinf.usach.DistributedGeoserverAPI.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RestResponse {
    private int status;
    private String responseSerialized;

    public RestResponse(CloseableHttpResponse response) throws IOException {
        this.status = response.getStatusLine().getStatusCode();
        this.responseSerialized = EntityUtils.toString(response.getEntity());
    }

    public RestResponse(int status, String response){
        this.status = status;
        this.responseSerialized = response;
    }

    public JsonNode getResponseDeserialized() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jn = null;
        try {
            jn = mapper.readTree(this.responseSerialized);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jn;
    }

    public int getStatus() {
        return status;
    }

    public String getResponseSerialized() {
        return responseSerialized;
    }
}
