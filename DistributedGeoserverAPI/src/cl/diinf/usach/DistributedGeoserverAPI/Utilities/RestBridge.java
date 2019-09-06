package cl.diinf.usach.DistributedGeoserverAPI.Utilities;

import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static cl.diinf.usach.DistributedGeoserverAPI.Utilities.JsonUtil.toJson;

public final class RestBridge {

    public static String user = "admin";
    public static String pass = "geoserver";
    public static String authHeader = "Basic " + new String(Base64.encodeBase64(
            (user + ":" + pass).getBytes(StandardCharsets.ISO_8859_1)));

    public static RestResponse sendRest(String endpoint, JsonObject entity, String type) {
        endpoint = "http://localhost:8600/geoserver/rest/" + endpoint;
        //HttpRequest request = null;
        Request request;
        switch (type) {
            case "GET":
                request = Request.Get(endpoint);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                break;
            case "POST":
                request = Request.Post(endpoint);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                break;
            case "PUT":
                request = Request.Put(endpoint);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                break;
            case "DELETE":
                request = Request.Delete(endpoint);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                break;
            default:
                //Form
                request = Request.Post(endpoint);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
                break;
        }

        request.addHeader(HttpHeaders.ACCEPT,"application/json");
        //Añadir parámetros
        try {
            StringEntity s = new StringEntity(toJson(entity));
            System.out.println(type);
            if(!(type == "GET" || type == "DELETE")){
                request.body(s);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Ahora se aplica la autentificacion
        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        HttpResponse response = null;
        try {

            response = request.execute().returnResponse();
        } catch (IOException e) {
            return new RestResponse();

        }
        System.out.println(response.getEntity());
        return new RestResponse(response);
    }
}