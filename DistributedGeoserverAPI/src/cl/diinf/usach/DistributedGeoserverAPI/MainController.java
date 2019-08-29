package cl.diinf.usach.DistributedGeoserverAPI;

import cl.diinf.usach.DistributedGeoserverAPI.Model.RestResponse;
import cl.diinf.usach.DistributedGeoserverAPI.Model.Workspace;

import java.util.List;
import java.util.Optional;

import static spark.Spark.*;
import static cl.diinf.usach.DistributedGeoserverAPI.Utilities.JsonUtil.*;

public class MainController {

    public MainController(){
        Workspace workspace = new Workspace();


        //logger.writeLog("RecommenderController - Starting Service");
        //core.setLogger(logger);

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
                List<Workspace> lw = workspace.getAll();
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
                int status = workspace.create(req.body());
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
                return "OK";
            });
        });

        path("/datastores", () -> {
            get("/", (req, res) -> {
                System.out.println("[GET] Req: " + req.toString() + ", Res: " + res.toString());
                List<Workspace> lw = workspace.getAll();
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
                int status = workspace.create(req.body());
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
                return "OK";
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
