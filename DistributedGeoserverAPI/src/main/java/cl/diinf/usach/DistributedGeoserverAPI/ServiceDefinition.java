package cl.diinf.usach.DistributedGeoserverAPI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ServiceDefinition {
    public ServiceDefinition(String filename) {
        /*try {
            FileInputStream propFile = new FileInputStream(filename);
            loadProperties(propFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        MainController mc = new MainController();
    }
}
