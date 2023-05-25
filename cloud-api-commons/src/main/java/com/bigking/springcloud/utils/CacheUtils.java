package com.bigking.springcloud.utils;




import java.util.concurrent.ConcurrentHashMap;


public class CacheUtils {
    public static ConcurrentHashMap<String, Object> cacheMap;
    public static ConcurrentHashMap<String, Long> expireTimeMap;
    public static final Integer cacheThreshold = 3000;
    static {
        cacheMap = new ConcurrentHashMap<>();
        expireTimeMap = new ConcurrentHashMap<>();
    }
    public static void setCache(String key, Object value, Integer minutes){
        try {
            System.out.println(cacheMap.size());
            clearAll();
            cacheMap.put(key, value);
            expireTimeMap.put(key, System.currentTimeMillis()+minutes*60*1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getCache(String key){
        try {
            if(cacheMap.containsKey(key) && expireTimeMap.containsKey(key) && expireTimeMap.get(key) > System.currentTimeMillis()){
                Object res = cacheMap.get(key);
                return res;
            }else{
                removeCache(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void removeCache(String key){
        try {
            if(cacheMap.containsKey(key)){
                cacheMap.remove(key);
            }
            if(expireTimeMap.containsKey(key)){
                expireTimeMap.remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearAll(){
        try {
            if(cacheMap.size() > cacheThreshold){
                for(String key: cacheMap.keySet()){
                    if(expireTimeMap.containsKey(key) && expireTimeMap.get(key) < System.currentTimeMillis()){
                        expireTimeMap.remove(key);
                        cacheMap.remove(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
