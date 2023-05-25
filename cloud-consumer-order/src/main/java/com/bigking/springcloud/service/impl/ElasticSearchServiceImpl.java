package com.bigking.springcloud.service.impl;

import com.bigking.springcloud.elasticsearch.ESRequestUtil;
import com.bigking.springcloud.elasticsearch.EsRequest;
import com.bigking.springcloud.pojo.SipLog;
import com.bigking.springcloud.service.ElasticSearchService;
import com.bigking.springcloud.utils.CacheUtils;
import com.bigking.springcloud.utils.GsonUtil;
import com.bigking.springcloud.utils.OkHttpUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

//    private static final String esCookieIndex = "https://es-api.wx-calling.o.webex.com/wxcalling:logs*,wxcallingeuc1:logs*/_search";
    private static final String esCallingIndex = "https://es-api.wx-calling.aiadlogprd-agg1.prod.infra.webex.com/logstash-wxcalling-*/_search";
    private static final String userName = "mats_lmaes_prod.gen@cisco.com";
    private static final String passWord = "M@t5_202209";
    private static final String cookieKey = "prefix_calling_cookie";
    private static OkHttpClient client = OkHttpUtil.getClientInstance();
    private static OkHttpClient clientWithNoRedirects = OkHttpUtil.getClientInstancewithNoRedirects();

    private String patternStr = "<form id=\"login-form\" method=\"post\" name=\"login-form\" action=\"(.+?)\">";

    private String getSipLogsStr(String callId, String startTime, String endTime){
        String body = null;
        String resStr = "";
        try {
            EsRequest esRequest = ESRequestUtil.buildWithCallId(callId, startTime, endTime);
            body = GsonUtil.toJsonString(esRequest);
        } catch (ParseException e) {
           log.error("build elasticSearch request body failed!");
        }
        String cookie = getCookies();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);
        Headers headers = new Headers.Builder()
                .add("Cookie", cookie)
                .add("content-type", "application/json")
                .build();
        ResponseBody res = null;
        try {
            res = OkHttpUtil.post(esCallingIndex, requestBody, headers, client).body();
            resStr =  res.string();
        } catch (IOException e) {
           log.error("get LMA responseBody failed!");
        }finally {
            res.close();
        }
        return resStr;
    }



    private String getCookies(){
        String cacheCookie =(String) CacheUtils.getCache(cookieKey);
        if(StringUtils.isNotEmpty(cacheCookie) && isValidCookie(cacheCookie)){
            return cacheCookie;
        }
        String finalCookies = "";
        Response firstResponse = null;
        Response secondResponse = null;
        Response response = null;
        try {
            Headers headers1 = new Headers.Builder()
                    .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .add("Accept-Language", "en-US,en;q=0.9")
                    .add("content-type", "text/html")
                    .build();
            firstResponse = OkHttpUtil.get(esCallingIndex, headers1, client);
            Response priorResponse = firstResponse.priorResponse();
            String matchStr = firstResponse.body().string();


            Pattern pattern = Pattern.compile(patternStr);
            Matcher m = pattern.matcher(matchStr);
            String post_url = "";
            while(m.find()){
                post_url = m.group(1);
            }


            List<String> firstCookieList = firstResponse.headers("Set-Cookie");
            List<String> priorCookieList = priorResponse.headers("Set-Cookie");

            StringBuilder sb = new StringBuilder();
            for(String ck: firstCookieList){
                sb.append(ck).append(";");
            }
            for(String ck: priorCookieList){
                sb.append(ck).append(";");
            }

            Headers headers2 = new Headers.Builder()
                    .add("Cookie", sb.toString())
                    .add("Content-Type", "application/x-www-form-urlencoded")
                    .add("Origin", "https://cloudsso.cisco.com")
                    .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .add("Accept-Language", "en-US,en;q=0.9")
                    .build();

            RequestBody requestBody = new FormBody.Builder()
                    .add("pf.username", userName)
                    .add("pf.pass", passWord)
                    .add("pf.userType", "cco")
                    .add("pf.TargetResource", "?")
                    .build();


            secondResponse = OkHttpUtil.post(post_url, requestBody, headers2, clientWithNoRedirects);
            String location = secondResponse.header("Location");
            List<String> secondCookieList = secondResponse.headers("Set-Cookie");
            StringBuilder sb2 = new StringBuilder();
            for(String ck: secondCookieList){
                sb2.append(ck).append(";");
            }
            sb2.append(sb);
            Headers headers3 = new Headers.Builder()
                    .add("Cookie", sb2.toString())
                    .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .add("Accept-Language", "en-US,en;q=0.9")
                    .add("content-type", "text/html")
                    .build();

            response = OkHttpUtil.get(location, headers3, clientWithNoRedirects);
            List<String> cookies = response.headers("Set-Cookie");
            StringBuilder sbb = new StringBuilder();
            for(String cookie: cookies){
                sbb.append(cookie).append(";");
            }
            finalCookies =  sbb.toString();
            CacheUtils.setCache(cookieKey, finalCookies, 20);
        } catch (IOException e) {
            log.error("get cookie via LMA failed!");
        }finally {
            firstResponse.body().close();
            secondResponse.body().close();
            response.body().close();
        }
        return finalCookies;
    }

    public boolean isValidCookie(String cookie){
        ResponseBody res = null;
        try {
            String body = GsonUtil.toJsonString(new EsRequest().addSize(1));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body);
            Headers headers = new Headers.Builder()
                    .add("Cookie", cookie)
                    .add("content-type", "application/json")
                    .build();
            Response response = OkHttpUtil.post(esCallingIndex, requestBody, headers, client);
            res = response.body();
            String str = res.string();
            if(StringUtils.isNotEmpty(str) && response.code()==200){
                return true;
            }
        } catch (IOException e) {
           log.error("connect to LMA with cached cookie failed!");
        }finally {
            res.close();
        }
        return false;
    }

    public List<SipLog> getSipLogs(JsonObject jsonObject){
        List<SipLog> res = new ArrayList<>();
        if(jsonObject==null) return res;
        try {
            JsonArray jsonArray = jsonObject.getAsJsonObject("hits").getAsJsonArray("hits");
            if(jsonArray.size()>0){
                for(int i = 0; i < jsonArray.size(); i++){
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    JsonObject source = object.getAsJsonObject("_source");
                    SipLog sipLog = new SipLog();
                    sipLog.setCallId(source.get("callId").getAsString());
                    sipLog.setTimestamp(source.get("@timestamp").getAsString());
                    sipLog.setAgent(source.getAsJsonObject("agent").toString());
                    sipLog.setWxcServerType(source.getAsJsonObject("wxc").getAsJsonObject("server").get("type").getAsString());
                    sipLog.setWxcServerStatus(source.getAsJsonObject("wxc").getAsJsonObject("server").get("status").getAsString());
                    sipLog.setHost(source.getAsJsonObject("host"));
                    res.add(sipLog);

                    res.add(sipLog);
                }
            }
        } catch (Exception e) {
            log.error("Parse SipLogs result failed!");
        }
        return res;
    }


    @Override
    public Map<String, List<SipLog>> getSipLogs(List<String> callIds, String startTime, String endTime) {
        Map<String, List<SipLog>> result = new HashMap<>();
        for(String callId: callIds){
            String sipLogStr = getSipLogsStr(callId, startTime, endTime);
            JsonObject sipLogObj = GsonUtil.getJsonElement(sipLogStr).getAsJsonObject();
            List<SipLog> sipLogList = getSipLogs(sipLogObj);
            result.put(callId, sipLogList);
        }
        return result;
    }
}
