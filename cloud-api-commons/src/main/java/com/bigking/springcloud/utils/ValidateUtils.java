package com.bigking.springcloud.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class ValidateUtils
{
	public static void validateNotNull(Object target) {
		Validate.notNull(target, "the 'target' must not be null");
	}

	public static String validateNotEmptyStr(String str) {
		str = StringUtils.trim(str);
		Validate.isTrue(StringUtils.isNotEmpty(str), "the 'str' must not be empty and not be null");
		return str;
	}
	
	public static void validateNotNullCollection(Object[] objects) {
		Validate.notNull(objects, "the 'collection' must not be null");
		Validate.noNullElements(objects, "the element must not be null");
		Validate.notEmpty(objects, "the 'collection' must not be empty");
	}
}