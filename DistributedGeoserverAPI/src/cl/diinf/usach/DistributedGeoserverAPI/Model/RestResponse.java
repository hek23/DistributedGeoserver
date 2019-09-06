package cl.diinf.usach.DistributedGeoserverAPI.Model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;


import javax.swing.text.html.parser.Entity;
import java.io.IOException;

public class RestResponse {
    private int status;
    private JsonObject response;
    private String message;

    public JsonObject getResponse() {
        return response;
    }

    public RestResponse(){

    }
    public RestResponse(HttpResponse _response){
        this.status = _response.getStatusLine().getStatusCode();
        JsonParser parser = new JsonParser();
        String body = "";
        try {
            body = EntityUtils.toString(_response.getEntity());
            this.response = parser.parse(body).getAsJsonObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (java.lang.IllegalStateException j){
            //Respuesta de Geoserver. Solo importa el status
            this.response = new JsonObject();
            response.addProperty("result", parser.parse(body).getAsString());
        }
    }

    public RestResponse(int _status,String _message){
        this.status = _status;
        //this.response = new JsonObject();
        //this.response.addProperty("message", _message);
        this.message = _message;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setResponse(JsonObject response) {
        this.response = response;
    }
}
