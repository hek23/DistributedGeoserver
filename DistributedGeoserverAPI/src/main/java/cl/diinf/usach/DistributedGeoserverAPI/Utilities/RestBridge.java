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

    public static RestResponse sendRest(String endpoint, MultiValueMap entity, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "geoserver");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(entity, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> reqResponse;
        RestResponse responseFormatted = null;
        //endpoint = "http://geoservertester:8080/geoserver/rest/" + endpoint;
        endpoint= "http://localhost:8600/geoserver/ows?service=WPS&version=1.0.0&request=GetCapabilities";
        switch (type) {
            case "GET":
                responseFormatted = new RestResponse(restTemplate.getForEntity(endpoint, String.class));
                System.out.println(responseFormatted);
                break;
            case "POST":
                responseFormatted = new RestResponse(restTemplate.postForEntity(endpoint, requestEntity, String.class));
                break;
            case "PUT":
                restTemplate.put(endpoint, requestEntity);
                break;
            case "DELETE":
                restTemplate.delete(endpoint, requestEntity);
            default:
                //Form
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                requestEntity = new HttpEntity<>(entity, headers);
                responseFormatted = new RestResponse(restTemplate.postForEntity(endpoint, requestEntity, String.class));
                break;
        }
        return responseFormatted;
    }
}