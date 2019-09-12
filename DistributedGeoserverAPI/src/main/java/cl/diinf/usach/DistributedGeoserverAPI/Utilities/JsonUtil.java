package cl.diinf.usach.DistributedGeoserverAPI.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.ResponseTransformer;

import java.text.NumberFormat;

public class JsonUtil {
    private static JsonParser parser = new JsonParser();

    public static JsonObject fromJson(String base){
        return parser.parse(base).getAsJsonObject();
    }

    public static Object fromJson(String base, Class c){
        return new Gson().fromJson(base, c);
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }
}
