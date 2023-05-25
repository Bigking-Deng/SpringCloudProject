package com.bigking.springcloud.elasticsearch;

import com.bigking.springcloud.utils.GsonUtil;
import com.google.gson.JsonObject;

import java.util.Arrays;

public class test {
    public void test(){

//        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();

        EsRequest esRequest = new EsRequest();


        EsQueryBool esQueryBool = new EsQueryBool();
        esQueryBool.addMust(new EsQueryTerms().setTerms("callId", Arrays.asList("1242121", "43213123")), new EsQueryTerm().setTerm("country", "USA"));
        esQueryBool.addFilter(new EsQueryRange().setRange("startTime", new RangeCompareEntity().setGt(100).setLte(200)));

        esRequest.addSort("dateTime", "asc");

        EsAggTerms esAggTerms = new EsAggTerms().setTerms("site");
        esAggTerms.setAggs("aggs2", new EsAggAvg().setTerm("color"));


        esRequest.addAggs("agg1", esAggTerms);

        EsQuery esQuery = new EsQuery();
        esQuery.setBool(esQueryBool);

        esRequest.addQueryBool(esQuery);


        String ss = GsonUtil.toJsonString(esRequest);

        JsonObject js = GsonUtil.getJsonElement(ss).getAsJsonObject();

//        String bodyStr3 = JSON.toJSONString(esRequest);
//        JSONObject js3 = JSON.parseObject(bodyStr3);

        System.out.println("----");




    }

    public static void main(String[] args) {

        new test().test();
    }
}
