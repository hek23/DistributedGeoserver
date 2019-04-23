package cl.diinf.usach.Model;

import com.fasterxml.jackson.annotation.JsonRootName;

public class TargetStore {
    public DataStore dataStore;

    TargetStore(String name){
        this.dataStore = new DataStore(name);
    }
}
