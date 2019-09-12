package cl.diinf.usach.DistributedGeoserverAPI.Model;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;

import static cl.diinf.usach.DistributedGeoserverAPI.Utilities.JsonUtil.fromJson;

import java.io.IOException;

public class RestResponse {
    private int status;
    private JsonObject response;

    public RestResponse(ResponseEntity<String> response){
        this.status = response.getStatusCodeValue();
        this.response = fromJson(response.getBody());
    }

    public int getStatus() {
        return this.status;
    }

    public JsonObject getResponse() {
        return this.response;
    }

    public RestResponse(HttpResponse _response){
        this.status = _response.getStatusLine().getStatusCode();
        try {
            JsonParser parser = new JsonParser();
            String body = EntityUtils.toString(_response.getEntity());
            this.response = parser.parse(body).getAsJsonObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RestResponse(int _status,String _message){
        this.status = _status;
        this.response = new JsonObject();
        this.response.addProperty("message", _message);
    }
}
