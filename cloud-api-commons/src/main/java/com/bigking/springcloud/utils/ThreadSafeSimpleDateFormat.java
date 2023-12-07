
package com.bigking.springcloud.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThreadSafeSimpleDateFormat extends ThreadLocal<SimpleDateFormat> {
	String format;
	Locale local;

	public ThreadSafeSimpleDateFormat(String format) {
		this.format = format;
		this.local = Locale.getDefault();
	}

	public ThreadSafeSimpleDateFormat(String format, Locale local) {
		this.format = format;
		this.local = local;
	}

	@Override
	protected SimpleDateFormat initialValue() {
		return new SimpleDateFormat(format, local);
	}

	public Date parse(String source) throws ParseException {
		return get().parse(source);
	}

	public final String format(Date date) {
		return get().format(date);
	}
}