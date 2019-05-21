package cl.diinf.usach.DistributedGeoserverAPI.Controllers;

import cl.diinf.usach.DistributedGeoserverAPI.Model.Workspace;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
public class WorkspaceController {

    public static Workspace get(String name){
        return null;
    }


    @GetMapping("/workspaces")
    public List<String> getAll() {
        List<String> workspaceList = new ArrayList<String>();
        Workspace.getAll().forEach(wspace -> workspaceList.add(wspace.getName()));
        return workspaceList;
    }

    @PostMapping("/workspaces")
    public int create(@RequestBody String name){
        return Workspace.create(name);
    }

    @GetMapping("/workspaces/{name}")
    public Workspace getByName(@PathVariable String name){
        return Workspace.getByName(name);
    }

}
