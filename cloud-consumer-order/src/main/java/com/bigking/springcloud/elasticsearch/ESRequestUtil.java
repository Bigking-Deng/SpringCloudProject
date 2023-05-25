package com.bigking.springcloud.elasticsearch;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ESRequestUtil {

    public static EsRequest buildWithCallId(String callId, String startTime, String endTime) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startDate = sdf1.parse(startTime);
        Date endDate = sdf1.parse(endTime);

        EsRequest esRequest = new EsRequest();
        EsQueryRange esQueryRange = new EsQueryRange().setRange("@timestamp", new RangeCompareEntity().setGte(sdf2.format(startDate)).setLte(sdf2.format(endDate)));

        EsQueryBool esQueryBool = new EsQueryBool();
                esQueryBool.addFilter(new EsQueryTerm().setTerm("callId.keyword", callId), esQueryRange);
        EsQuery esQuery = new EsQuery();
        esQuery.setBool(esQueryBool);
        esRequest.addQueryBool(esQuery);
        return esRequest;

    }


}
