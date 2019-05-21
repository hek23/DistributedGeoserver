package cl.diinf.usach.DistributedGeoserverAPI.Utilities;

import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
//import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public final class RestBridge {

    //Body puede ser HTTPEntity...
    public static RestResponse sendPost(String endpoint, ObjectNode body, List<String[]> headers) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://geoservertester:8080/geoserver/rest/" + endpoint);
        try {
            request.setEntity(new StringEntity(body.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.forEach(header -> request.setHeader(header[0],header[1]));
        try {

            request.setHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("admin", "geoserver"),request,null));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        CloseableHttpResponse response = null;

        RestResponse rr=null;
        try {
            response = client.execute(request);
            rr = new RestResponse(response);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rr;
    }

    public static RestResponse sendGet(String endpoint, List<String[]> headers){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpGet request = new HttpGet("http://geoservertester:8080/geoserver/rest/" + endpoint);

        headers.forEach(header -> request.setHeader(header[0],header[1]));
        try {

            request.setHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials("admin", "geoserver"),request,null));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        CloseableHttpResponse response = null;

        RestResponse rr = null;
        try {
            response = client.execute(request);
            rr = new RestResponse(response);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rr;
    }
    public static RestResponse sendForm(String endpoint, MultiValueMap entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBasicAuth("admin","geoserver");
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(entity, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .postForEntity("http://geoservertester:8080/geoserver/rest/"+endpoint, requestEntity, String.class);

        return new RestResponse(response.getStatusCodeValue(), response.getBody());
    }

    public static void sendPut(String endpoint, MultiValueMap entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin","geoserver");
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(entity, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put("http://geoservertester:8080/geoserver/rest/"+endpoint, requestEntity);

        //System.out.println(response.toString());
    }

    public static void sendPost2(String endpoint, MultiValueMap entity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin","geoserver");
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(entity, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("http://geoservertester:8080/geoserver/rest/"+endpoint, requestEntity, String.class);

        System.out.println(response.toString());
    }
}
