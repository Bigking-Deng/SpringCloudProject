package com.bigking.springcloud.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DateUtils {
    public static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_REGEX = "^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))$";
    public static final String YYYY_MM_DD_HH_MM_SS_REGEX = "^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]|[0-9][1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    private static final Log LOG = LogFactory.getLog(DateUtils.class);

    public static Date getCurrentUTCDateTime() {
        return getCurrentUTCDateTime(YYYY_MM_DD_HH_MM_SS_FORMAT);
    }

    public static Date getCurrentUTCDateTime(String dateTimeFormat){
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        if (dateTimeFormat == null) {
            dateTimeFormat = YYYY_MM_DD_HH_MM_SS_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String curUtcDateTimeStr = format.format(cal.getTime());

        try {
            return format.parse(curUtcDateTimeStr);
        } catch (Exception ex) {

        }
        return null;
    }

    public static Date convertToUTCDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String curUtcDateTimeStr = format.format(cal.getTime());

        try {
            return format.parse(curUtcDateTimeStr);
        } catch (Exception ex) {

        }
        return null;
    }

    public static Date convertStringToDateTime(String dateTimeStr) {
        if (dateTimeStr == null) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_FORMAT);
            return format.parse(dateTimeStr);
        } catch (Exception ex) {

        }
        return null;
    }

    public static Date convertStrToUTCDateTime(String dateStr) {
        Date date = convertStringToDateTime(dateStr);
        return convertToUTCDateTime(date);
    }

    public static Date convertTzString2Date(String dateStr) {
        if (StringUtils.isNotEmpty(dateStr) && dateStr.trim().length() >= 19) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                return format.parse(dateStr);
            } catch (Exception ex) {

            }
        }
        return null;
    }

    //just support YYYY_MM_DD_FORMAT, YYYY_MM_DD_HH_MM_SS_FORMAT currently.
    public static boolean isValidDate(String dateStr, String formatStr) {
        LOG.info(String.format("Start to isValidDate, dateStr=%s, formatStr=%s.", dateStr, formatStr));
        if (StringUtils.isBlank(dateStr) || StringUtils.isBlank(formatStr)) {
            LOG.warn(String.format("isValidDate, dateStr=%s, formatStr=%s, Required dateStr or formatStr is empty.", dateStr, formatStr));
            return false;
        }
        try {
            boolean isMatchRegex = false;
            switch (formatStr) {
                case YYYY_MM_DD_FORMAT:
                    isMatchRegex = Pattern.matches(YYYY_MM_DD_REGEX, dateStr);
                    break;
                case YYYY_MM_DD_HH_MM_SS_FORMAT:
                    isMatchRegex = Pattern.matches(YYYY_MM_DD_HH_MM_SS_REGEX, dateStr);
                    break;
                default:
                    LOG.warn(String.format("isValidDate, dateStr=%s, formatStr=%s, Unknown formatStr.", dateStr, formatStr));
                    break;
            }
            if (!isMatchRegex) {
                LOG.warn(String.format("isValidDate, dateStr=%s, formatStr=%s, Don't matched format dateStr.", dateStr, formatStr));
                return false;
            }
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            format.setLenient(false);
            Date date = format.parse(dateStr);
            LOG.info(String.format("Succeed to isValidDate, dateStr=%s, formatStr=%s, date=%s.", dateStr, formatStr, date));
            return true;
        } catch (Exception e) {//java.text.ParseException or NullPointerException
            LOG.warn(String.format("Error to isValidDate, dateStr=%s, formatStr=%s, error=%s.", dateStr, formatStr, e.getMessage()), e);
            return false;
        }
    }

    public static String getISO8601FormatTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
        return df.format(date);
    }

    public static Date getPastNDays(Integer pastNumber) {
        // return date of pastNumber days ago
        try {
            final Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat format = new SimpleDateFormat(YYYY_MM_DD_FORMAT);
            final SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS_FORMAT);
            calendar.add(Calendar.DATE, -1 * pastNumber);
            String dateStr = format.format(calendar.getTime());
            return dateFormat.parse(dateStr + " 00:00:00");
        } catch (Exception e) {

        }
        return null;
    }

    public static boolean isDateVail(String date) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime.parse(date, dtf);
            return true;
        } catch (Exception e) {
            LOG.warn("Failed to isDateVail, error: ", e);
            return false;
        }
    }
}