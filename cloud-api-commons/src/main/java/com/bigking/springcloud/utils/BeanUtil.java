package com.bigking.springcloud.utils;

import com.alibaba.fastjson.JSONObject;

import com.bigking.springcloud.annotations.DateFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class BeanUtil
{
	private final static Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);
    /**
     * @author haicheng
     */
    public static void constructFromRow(Object target, List<String> columns, List<String> row) {

		final List<Field> fieldsRet = new LinkedList<Field>();
		ReflectionUtils.doWithFields(target.getClass(), field -> fieldsRet.add(field), ReflectionUtils.COPYABLE_FIELDS);

		for(int i1 = 0; i1 < columns.size(); ++i1) {

			Field targetField = ReflectionUtils.findField(target.getClass(), columns.get(i1));
			if(targetField == null) {
				continue;
			}

			processEachField(target, targetField, row.get(i1));
		}
    }

	public static void constructFromRow(Object target, JSONObject row) {

		final List<Field> fieldsRet = new LinkedList<Field>();
		ReflectionUtils.doWithFields(target.getClass(), field -> fieldsRet.add(field), ReflectionUtils.COPYABLE_FIELDS);

		for(int i1 = 0; i1 < fieldsRet.size(); ++i1) {

			Field targetField = fieldsRet.get(i1);

			JSONObject targetObject = row.getJSONObject(targetField.getName());

			String targetValue = targetObject == null ? null : targetObject.getString("value");

			processEachField(target, targetField, targetValue);
		}
	}
    
    private static void setFieldValue(Object target, Field field, Object value) {
    	ReflectionUtils.makeAccessible(field);
    	try {
    		field.set(target, value);
    	}catch(Exception ex) {
			LOGGER.error("Method setFieldValue() error msg:{}",ex);
		}
    }
    
    public static void processEachField(Object target, Field field, String value) {
    	try {
    		if (field.getType().isAssignableFrom(Byte.class)) {
				setFieldValue(target, field, Float.valueOf(value).byteValue());
			} else if (field.getType().isAssignableFrom(Integer.class)) {
				setFieldValue(target, field, Float.valueOf(value).intValue());
			} else if (field.getType().isAssignableFrom(Long.class)) {
				setFieldValue(target, field, Float.valueOf(value).longValue());
			} else if (field.getType().isAssignableFrom(Float.class)) {
				setFieldValue(target, field, Float.valueOf(value));
			} else if (field.getType().isAssignableFrom(Double.class)) {
				setFieldValue(target, field, Double.valueOf(value));
			} else if (field.getType().isAssignableFrom(Boolean.class)) {
				setFieldValue(target, field, Boolean.valueOf(value));
			} else if (field.getType().isAssignableFrom(Timestamp.class)) {
				String format = null;
				DateFormat valueFormat = field.getAnnotation(DateFormat.class);
				if (valueFormat != null) {
					format = valueFormat.value();
				}
				format = (StringUtils.isEmpty(format)) ? GsonUtils.CN_DATE_TIME : format;
				setFieldValue(target, field, new Timestamp(new SimpleDateFormat(format).parse(value).getTime()));
			} else if(StringUtils.isNotEmpty(value)) {
				value=value.replaceAll("\r|\n|\t", "");
				setFieldValue(target, field, value);
			}
    	} catch(Exception ex) {
    		LOGGER.error("Method processEachField() error msg:{}",ex);
    	}
    }

	public static void processEachFieldWithDefaultValue(Object target, Field field) {
		try {
			if (field.getType().isAssignableFrom(Byte.class)) {
				setFieldValue(target, field, 0);
			} else if (field.getType().isAssignableFrom(Integer.class)) {
				setFieldValue(target, field, 0);
			} else if (field.getType().isAssignableFrom(Long.class)) {
				setFieldValue(target, field, 0L);
			} else if (field.getType().isAssignableFrom(Float.class)) {
				setFieldValue(target, field, 0.0f);
			} else if (field.getType().isAssignableFrom(Double.class)) {
				setFieldValue(target, field, 0.0d);
			} else if (field.getType().isAssignableFrom(Boolean.class)) {
				setFieldValue(target, field, false);
			} else if (field.getType().isAssignableFrom(String.class)) {
				setFieldValue(target, field, "");
			}
		} catch(Exception ex) {
			LOGGER.error("Method processEachFieldWithDefaultValue() error msg:{}",ex);
		}
	}
}