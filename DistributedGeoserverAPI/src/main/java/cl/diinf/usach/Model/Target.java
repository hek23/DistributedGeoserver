package cl.diinf.usach.Model;

import com.fasterxml.jackson.annotation.JsonRootName;

public class Target {
    public Store store;

    Target(String name){
        this.store = new Store(name);
    }
}
