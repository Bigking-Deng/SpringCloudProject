package com.bigking.springcloud.utils;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class GsonUtils {

    public static final String CN_DATE = "yyyy-MM-dd";
    public static final String US_DATE = "MM/dd/yyyy";

    public static final String US_MONTH_DAY = "MM/dd";

    public static final String CN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String US_DATE_TIME = "MM/dd/yyyy HH:mm:ss";

    public static final String DAY_BEGIN_TIME = "00:00:00";
    public static final String DAY_END_TIME = "23:59:59";

    public static final String CN_DATE_TIME_TZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final Logger log = LoggerFactory.getLogger(GsonUtils.class);
    private static JsonParser jsonParser = new JsonParser();
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    private static final ThreadLocal<Map<String, SimpleDateFormat>> LOCAL_FORMAT = ThreadLocal.withInitial(() -> new HashMap<>(5));

    private GsonUtils() {
    }

    public static JsonElement parse(String jsonString) {
        return jsonParser.parse(jsonString);
    }

//    public static <T> T parse(JsonObject jsonObject, Class<T> clz) {
//
//        T t = null;
//        try {
//            t = clz.getDeclaredConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            log.error(e.getMessage());
//            return null;
//        }
//        return update(jsonObject, t);
//    }

//    public static <T> T update(JsonObject jsonObject, T t) {
//        try {
//            if (jsonObject == null || jsonObject.isJsonNull()) {
//                return t;
//            }
//            Class clz = t.getClass();
//            BeanInfo beanInfo = Introspector.getBeanInfo(clz);
//            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
//                String key = descriptor.getName();
//                Method setter = descriptor.getWriteMethod();
//                Object data = null;
//                boolean updateNull = false;
//                if (!"class".equals(key) && setter != null) {
//                    Field field = clz.getDeclaredField(descriptor.getName());
//                    SerializeName serializeName = field.getAnnotation(SerializeName.class);
//                    if (serializeName == null) {
//                        JsonElement element = jsonObject.get(field.getName());
//                        data = fetchType(element, field.getType());
//                    } else {
//                        String[] paths = serializeName.name();
//                        String formatter = serializeName.formatter();
//                        updateNull = serializeName.updateNull();
//                        for (String path : paths) {
//                            JsonElement element = targetJsonElement(jsonObject, path);
//                            data = fetchType(element, field.getType(), formatter);
//                            if (data != null) {
//                                break;
//                            }
//                        }
//                    }
//                    if (data != null || updateNull) {
//                        setter.invoke(t, data);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return t;
//    }

    private static JsonElement targetJsonElement(JsonObject jsonObject, String path) {
        String[] pathNodes = path.split("\\.");
        JsonElement pathElement = null;
        for (int i = 0; i < pathNodes.length - 1; i++) {
            pathElement = jsonObject.get(pathNodes[i]);
            if (pathElement == null || !pathElement.isJsonObject()) {
                return null;
            }
            jsonObject = pathElement.getAsJsonObject();
        }
        return jsonObject.get(pathNodes[pathNodes.length - 1]);
    }

    public static Date getDateByPath(JsonObject jsonObject, String jsonPath, String formatter) {
        try {
            if (jsonObject == null || jsonObject.isJsonNull()) {
                return null;
            }
            JsonElement element = targetJsonElement(jsonObject, jsonPath);
            return fetchType(element, Date.class, formatter);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    public static <T> T getDataByPath(JsonObject jsonObject, String jsonPath, Class<T> type) {
        try {
            if (jsonObject == null || jsonObject.isJsonNull()) {
                return null;
            }
            JsonElement element = targetJsonElement(jsonObject, jsonPath);
            return fetchType(element, type);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T fetchType(JsonElement element, Class<T> type) throws Exception {
        return fetchType(element, type, US_DATE_TIME);
    }


    public static <T> T fetchType(JsonElement element, Class<T> type, String formatter) throws Exception {
        if (element == null || element.isJsonNull()) {
            return null;
        } else if (String.class.isAssignableFrom(type)) {
            if (!element.isJsonPrimitive()) {
                return type.cast(element.toString());
            } else {
                return type.cast(element.getAsString().trim());
            }
        }//prevent some type cast issue
        else if (Integer.class.isAssignableFrom(type)) {
            String numString = element.getAsString().replace(",", "");
            if (!NumberUtils.isParsable(numString)) {
                return null;
            }
            return type.cast((int) Math.ceil(Float.parseFloat(numString)));
        } else if (Long.class.isAssignableFrom(type)) {
            return type.cast((long) Math.ceil(element.getAsDouble()));
        } else if (Float.class.isAssignableFrom(type)) {
            return type.cast(element.getAsFloat());
        } else if (Double.class.isAssignableFrom(type)) {
            return type.cast(element.getAsDouble());
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return type.cast(element.getAsBigDecimal());
        } else if (Boolean.class.isAssignableFrom(type)) {
            return type.cast(element.getAsBoolean());
        } else if (Date.class.isAssignableFrom(type)) {
            if (StringUtils.isBlank(element.getAsString())) {
                return null;
            }
            return type.cast(parse(element.getAsString(), formatter));
        } else if (JsonObject.class.isAssignableFrom(type)) {
            return type.cast(element.getAsJsonObject());
        } else if (JsonArray.class.isAssignableFrom(type)) {
            return type.cast(element.getAsJsonArray());
        } else {
            return null;
        }
    }

    public static Date parse(String date, String formatter) {
        return parse(date, formatter,null);
    }
    public static Date parse(String date, String formatter, TimeZone timeZone) {
        try {
            SimpleDateFormat sdf = getSdf(formatter);
            if (timeZone != null){
                sdf.setTimeZone(timeZone);
            }
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private static SimpleDateFormat getSdf(String pattern) {
        Map<String, SimpleDateFormat> map = LOCAL_FORMAT.get();
        SimpleDateFormat sdf = map.get(pattern);
        if (sdf == null) {
            sdf = new SimpleDateFormat(pattern);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            map.put(pattern, sdf);
        }
        return sdf;
    }

}
