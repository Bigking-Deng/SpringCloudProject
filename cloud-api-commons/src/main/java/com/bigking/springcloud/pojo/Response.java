package com.bigking.springcloud.pojo;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author devwang
 *
 * @param <T>
 */
public class Response<T> {
	private static final Log LOG = LogFactory.getLog(Response.class);
	private String SSeq;

	private List<T> data;

	private String jsonString;

	public Response() {

	}

	public Response(T object) {
		if (object instanceof List){
			data = (List<T>) object;
}
		else {
			data = new ArrayList<T>();
			data.add(object);
		}
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public void setData(T data) {
		List<T> list = new ArrayList<T>();
		list.add(data);
		this.data = list;
	}

	public String getSSeq() {
		return SSeq;
	}

	public void setSSeq(String sSeq) {
		SSeq = sSeq;
	}

	public String getJsonString() {
		LOG.debug("[OUT] getJsonString, jsonString=" + jsonString);

		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public static Type setClassType(Class clazz) {
		Type typeOfT = TypeToken.get(clazz).getType();
		return typeOfT;
	}

	@Override
	public String toString() {
		return "Container [SSeq=" + SSeq + ", data=" + data + "]";
	}
}