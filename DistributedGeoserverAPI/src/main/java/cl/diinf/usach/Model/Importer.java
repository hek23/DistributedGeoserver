package cl.diinf.usach.Model;


import com.fasterxml.jackson.annotation.JsonRootName;

/*
{
  "import": {
    "targetStore": {
      "dataStore": {
        "name": "postgis"
      }
    },
    "targetWorkspace": {
      "workspace": {
        "name": "cite"
      }
    }
  }
}
 */
public class Importer {
    public TargetStore targetStore;
    public TargetWorkspace targetWorkspace;

    public Importer(String storeName, String workspaceName){
        this.targetStore = new TargetStore(storeName);
        this.targetWorkspace = new TargetWorkspace(workspaceName);
    }
}
