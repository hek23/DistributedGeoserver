package cl.diinf.usach.DistributedGeoserverAPI;

import cl.diinf.usach.Model.ImportInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@RestController
class SHPImporter {

    //@Autowired
    //private UsernamePasswordCredentials geoserverAuth;

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    public Object importSHPFiles(@RequestBody ImportInfo resource) throws IOException, AuthenticationException {
        //Create JsonMapper
        ObjectMapper mapper = new ObjectMapper();
        //Create base node
        ObjectNode importnode = mapper.createObjectNode();
        //Init Import object
        //Init Datastore
        ObjectNode datastore = mapper.createObjectNode();
        datastore.put("name", resource.storeName);
        ObjectNode targetStore = mapper.createObjectNode();
        targetStore.set("dataStore", datastore);
        //Init Workspace
        ObjectNode workspace = mapper.createObjectNode();
        workspace.put("name", resource.workspaceName);
        ObjectNode targetWorkspace = mapper.createObjectNode();
        targetWorkspace.set("workspace", workspace);
        //Now, root!
        importnode.set("targetStore", targetStore);
        importnode.set("targetWorkspace", targetWorkspace);
        ObjectNode importdefinition = (ObjectNode) mapper.createObjectNode().set("import", importnode);
        ArrayList<String[]> headers = new ArrayList<String[]>();
        //headers.add(new String[]{"Accept", "application/json"});
        headers.add(new String[]{"Content-type", "application/json"});
        sendRest("", importdefinition,headers);
        return importdefinition;
    }
/*
    public void sendDefinition(ObjectNode definition) throws IOException, AuthenticationException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("http://localhost:8600/geoserver/rest/imports");
        StringEntity entity = new StringEntity(definition.toString());
        request.setEntity(entity);

        request.addHeader(new BasicScheme().authenticate(creds, request, null));
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = client.execute(request);
        System.out.println(response);
        client.close();
        //return response;
    }
    */
    public Object sendRest(String endpoint, ObjectNode body, ArrayList<String[]> headers){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpPost request = new HttpPost("http://localhost:8600/geoserver/rest/imports" + endpoint);
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

        try {

            response = client.execute(request);
            String objeto = handler.handleResponse(response);
            System.out.println(objeto);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
