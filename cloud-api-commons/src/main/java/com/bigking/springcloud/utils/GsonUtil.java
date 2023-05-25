package com.bigking.springcloud.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class GsonUtil {
    static final Gson gson;
    static final JsonParser jsonParser;
    static {
        gson = new Gson();
        jsonParser = new JsonParser();
    }

    public static String toJsonString(Object object){
        return gson.toJson(object);
    }

    public static JsonElement getJsonElement(String jsonStr){
        return jsonParser.parse(jsonStr);
    }
}
