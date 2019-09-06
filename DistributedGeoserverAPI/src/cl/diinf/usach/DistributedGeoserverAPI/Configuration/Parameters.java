package cl.diinf.usach.DistributedGeoserverAPI.Configuration;

import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Parameters {
    static Properties properties = new Properties();
    static {
        try {
            InputStream stream = Parameters.class.getResourceAsStream("/db.properties");
            if (stream == null) {
                throw new ExceptionInInitializerError("Failed to open properties stream.");
            }
            Parameters.properties.load(stream);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Properties dbParams = dbparams();
    public static Properties geoParams = geoparams();

    public final static String DB_PORT = properties.getProperty("DB.port");
    public final static String DB_NAME = properties.getProperty("DB.name");
    public final static String DB_HOST = properties.getProperty("DB.host");
    public final static String DB_TYPE = properties.getProperty("DB.type");
    public final static String DB_USER = properties.getProperty("DB.user");
    public final static String DB_PASS = properties.getProperty("DB.pass");
    public final static String GEOSERVER_URL = properties.getProperty("GEO.url");
    public final static String GEOSERVER_USER = properties.getProperty("GEO.user");
    public final static String GEOSERVER_PASS = properties.getProperty("GEO.pass");

    public static Properties dbparams(){
        Properties params = new Properties();
        params.put("user", DB_USER);
        params.put("passwd",DB_PASS);
        params.put("port", DB_PORT);
        params.put("host", DB_HOST);
        params.put("database", DB_NAME);
        params.put("dbtype", DB_TYPE);
        return params;
    }
     public static Properties geoparams(){
        Properties params = new Properties();
        params.put("url", GEOSERVER_URL);
        params.put("user", GEOSERVER_USER);
        params.put("pass", GEOSERVER_PASS);
        return params;
    }

}
