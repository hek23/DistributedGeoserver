package cl.diinf.usach.Model;

import com.fasterxml.jackson.annotation.JsonRootName;

public class TargetWorkspace {
    public Workspace workspace;

    TargetWorkspace(String name){
        this.workspace = new Workspace(name);
    }
}