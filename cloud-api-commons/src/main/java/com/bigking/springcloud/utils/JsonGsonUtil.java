package com.bigking.springcloud.utils;

import com.bigking.springcloud.pojo.Response;
import com.bigking.springcloud.pojo.TimestampTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Map;

public class JsonGsonUtil<T> {
	
	private static final Log LOG = LogFactory.getLog(JsonGsonUtil.class);
	
	private Type typeOfT;
	private static Gson gson = createGson();
	private static synchronized Gson createGson() {
		try {
			if (gson == null) {
				gson = new GsonBuilder().setPrettyPrinting().create();
			}
		} catch (Exception e) {
			LOG.error("Create Gson Exception. ", e);
		}
		return gson;
	}

	public JsonGsonUtil(Type typeOfT) {
		this.typeOfT = typeOfT;
	}

	public String toJson(Response<T> response) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Timestamp.class,
						new TimestampTypeAdapter())
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

		return gson.toJson(response, typeOfT);
	}

	public Response<T> fromJson(String jsonString) {
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Timestamp.class,
						new TimestampTypeAdapter())
				.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

		return gson.fromJson(jsonString, typeOfT);
	}

	public Response<T> toResponse(T object) {
		Response<T> response = new Response<T>(object);

		String json = this.toJson(response);

		response.setJsonString(json);

		LOG.debug("toResponse, json=" + json);

		return response;
	}

	/*
	 * change 'xxx' to xxx
	 */
	public static String formatName(String name) {
		if (name != null && !"".equals(name.trim())) {
			name = name.trim();
			if (name.startsWith("'")) {
				name = name.replace("'", "");
			}
			if (name.endsWith("'")) {
				name = name.substring(0, name.length()-1);
			}
		}
		return name;
	}

	public static String getJsonObjectString(JsonObject jo, String memberName){
		if (jo != null && StringUtils.isNotEmpty(memberName)){
			if (jo.has(memberName) && jo.get(memberName).getAsJsonPrimitive().isString()){
				return jo.get(memberName).getAsString();
			}
		}
		return "";
	}

	public static Long getJsonObjectNumber(JsonObject jo, String memberName){
		if (jo != null && StringUtils.isNotEmpty(memberName)){
			if (jo.has(memberName) && jo.get(memberName).getAsJsonPrimitive().isNumber()){
				return jo.get(memberName).getAsLong();
			}
		}
		return -1L;
	}

	public static Boolean getJsonObjectBoolean(JsonObject jo, String memberName){
		if (jo != null && StringUtils.isNotEmpty(memberName)){
			if (jo.has(memberName) && jo.get(memberName).getAsJsonPrimitive().isBoolean()){
				return jo.get(memberName).getAsBoolean();
			}
		}
		return null;
	}
	
	public static String convertMapToJson(Map<String, String> map) {
		Gson gson = new Gson();
		String json = gson.toJson(map);
		return json;
	}

	public static <T> T fromGson(String json, Class<T> clazz) {
		T ret = null;
		if (gson == null) {
			gson = createGson();
		}
		try {
			ret = gson.fromJson(json, clazz);
		} catch (Exception e) {
			LOG.error("Gson fromJson Exception. ", e);
		}

		return ret;
	}
}
