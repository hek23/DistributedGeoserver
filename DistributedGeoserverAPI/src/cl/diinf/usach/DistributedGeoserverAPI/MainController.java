package cl.diinf.usach.DistributedGeoserverAPI;

import cl.diinf.usach.DistributedGeoserverAPI.Model.GSDatastore;
import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import cl.diinf.usach.DistributedGeoserverAPI.Model.GSWorkspace;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Shapefile;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;
import static cl.diinf.usach.DistributedGeoserverAPI.Utilities.JsonUtil.*;

public class MainController {

    public MainController(){
        GSWorkspace gsWorkspace = new GSWorkspace();
        GSDatastore gsDatastore = new GSDatastore();

        // Port Definition
        port(8080);

        // Permitir CORS
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }
                    return "OK";
                });
        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        path("/workspaces", () -> {

            get("/", (req, res) -> {
                System.out.println("[GET] Req: " + req.toString() + ", Res: " + res.toString());
                List<GSWorkspace> lw = gsWorkspace.getAll();
                if(lw == null){
                    res.body(toJson(new RestResponse(503, "Geoserver doesn't answer or is down")));
                    res.status(503);
                }
                else{
                    res.body(toJson(lw));
                    res.status(200);
                }
                return res;
            });

            post("/", (req, res) -> {
                System.out.println("[POST] Req: " + req.toString() + ", Res: " + res.toString());
                int status = gsWorkspace.create(req.body());
                String message = "";
                switch (status) {
                    case 201: {
                        message = "Workspace Created";
                        break;
                    }
                    case 400: {
                        message = "Malformed JSON. See docs";
                        break;
                    }
                    case 401:{
                        message = "Workspace Already Exists";
                        break;
                    }
                    default:{
                        message = "ERROR";
                    }

                }
                res.body(toJson(new RestResponse(status, message)));
                return res;
            });

            get("/:wsName", (req, res)->{
                System.out.println("[GET] Req: " + req.toString() + ", Res: " + res.toString());
                return gsWorkspace.exists(req.params(":wsName")) == 200;
            });
        });

        path("/datastores", () -> {
            post("/", (req, res) -> {
                System.out.println("[POST] Req: " + req.toString() + ", Res: " + res.toString());
                int status = gsDatastore.create(req.body());
                String message = "";
                switch (status) {
                    case 201: {
                        message = "Datastore Created";
                        break;
                    }
                    case 400: {
                        message = "Malformed JSON. See docs";
                        break;
                    }
                    case 401:{
                        message = "Datastore Already Exists";
                        break;
                    }
                    default:{
                        message = "ERROR";
                    }

                }
                res.body(toJson(new RestResponse(status, message)));
                return res;
            });
        });

        path("/geoprocessing", ()-> {
            post("/upload", (request, response) -> {
                request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
                JsonObject body = fromJson(request.body());
                File file = null;
                String schema = "";
                try (InputStream is = request.raw().getPart("file").getInputStream()) {
                    // Use the input stream to create a file
                    file = new File("./tmp/" + body.get("workspace").getAsString() + body.get("datastore").getAsString() + String.valueOf(System.currentTimeMillis()));
                    schema = request.raw().getPart("file").getSubmittedFileName();
                    // commons-io
                    FileUtils.copyInputStreamToFile(is, file);
                }

                byte state = Shapefile.saveSHP(file,schema);
                Map<String, String> result = new HashMap<String, String>();
                switch (state){
                    case -2:
                        result.put("State", "File or Schema are empty");
                        response.status(400);
                        break;
                    case -1:
                        result.put("State", "An unexpected error occured");
                        response.status(500);
                        break;
                    case 0:
                        result.put("State", "Layer already exists");
                        response.status(409);
                        break;
                    case 1:
                        result.put("State", "Layer uploaded succesfully");
                        response.status(201);
                        break;
                    default:
                        result.put("State", "Error");
                        response.status(500);
                }
                if (response.status()==201){
                    //publish on geoserver

                }
                response.body(toJson(result));
                return response;
            });
        });







/*
        path("/api", () -> {
            path("/workspaces", () -> {
                //post("/create",    workspace.create);
                //put("/update",     workspace.update);
                //delete("/remove",  EmailApi.deleteEmail);
                get("/all", workspace.getAll);
            });
            path("/username", () -> {
                post("/add",       UserApi.addUsername);
                put("/change",     UserApi.changeUsername);
                delete("/remove",  UserApi.deleteUsername);
            });
        });
*/

        after((req, res) -> {
            res.type("application/json");
        });
    }
}
