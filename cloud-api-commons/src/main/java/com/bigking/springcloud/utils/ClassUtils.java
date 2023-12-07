package com.bigking.springcloud.utils;



import com.bigking.springcloud.Exception.BingException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author haicheng
 */

public class ClassUtils
{
	public static Object createDefaultValue(Class<?> clazz) {
		ValidateUtils.validateNotNull(clazz);
		if (clazz.isAssignableFrom(Byte.class)) {
			return new Byte((byte) 0);
		}
		if (clazz.isAssignableFrom(Short.class)) {
			return new Short((short)0);
		}
		if (clazz.isAssignableFrom(Integer.class)) {
			return new Integer(0);
		}
		if (clazz.isAssignableFrom(Long.class)) {
			return new Long(0L);
		}
		if (clazz.isAssignableFrom(Float.class)) {
			return new Float(0.0F);
		}
		if (clazz.isAssignableFrom(Double.class)) {
			return new Double(0.0D);
		}
		if (clazz.isAssignableFrom(Boolean.class)) {
			return new Boolean(false);
		}
		if(clazz.isArray()) {
			return new Object[0];
		}
		if(Collection.class.isAssignableFrom(clazz)) {
			return new ArrayList<String>();
		}
		return "";
	}

	public static <T> T cast(Class<T> clazz, Object target) {
		ValidateUtils.validateNotNull(clazz);
		if(target == null) {
			return null;
		}
		if (clazz.isAssignableFrom(Byte.class)) {
			return clazz.cast(Byte.valueOf(String.valueOf(target)));
		} 
		if (clazz.isAssignableFrom(Integer.class)) {
			return clazz.cast(Integer.valueOf(String.valueOf(target)));
		}
		if (clazz.isAssignableFrom(Long.class)) {
			return clazz.cast(Long.valueOf(String.valueOf(target)));
		}
		if (clazz.isAssignableFrom(Float.class)) {
			return clazz.cast(Float.valueOf(String.valueOf(target)));
		}
		if (clazz.isAssignableFrom(Double.class)) {
			return clazz.cast(Double.valueOf(String.valueOf(target)));
		}
		if (clazz.isAssignableFrom(Boolean.class)) {
			return clazz.cast(Boolean.valueOf(String.valueOf(target)));
		}
		if (clazz.isAssignableFrom(String.class)) {
			return clazz.cast(String.valueOf(target));
		}
		throw new BingException("Cannot cast to the target class [" + clazz.getName() + "]");
	}

}