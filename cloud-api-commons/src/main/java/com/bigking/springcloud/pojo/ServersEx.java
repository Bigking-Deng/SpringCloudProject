package com.bigking.springcloud.pojo;

import com.alibaba.fastjson.JSONObject;
import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author devwang
 */
public class ServersEx {
    private static ServersEx ourInstance = new ServersEx();

    private ServersEx() {
    }

    public static ServersEx getInstance() {
        return ourInstance;
    }

    public VendorExtension<?> servers() {
        List<JSONObject> srvs = new ArrayList<>();
        JSONObject prod = new JSONObject();
        prod.fluentPut("url", "https://antifraud.webex.com/fas").put("description", "PROD Server");
        JSONObject bts = new JSONObject();
        bts.fluentPut("url", "https://antifraudbts.webex.com/fas").put("description", "BTS Server");
        JSONObject ats = new JSONObject();
        ats.fluentPut("url", "https://mtats1fra-vip.webex.com/fas").put("description", "ATS Server");
        JSONObject personal = new JSONObject();
        personal.fluentPut("url", "http://127.0.0.1:9997/fas").put("description", "Personal Server");
        srvs.add(prod);
        srvs.add(bts);
        srvs.add(ats);
        srvs.add(personal);
        return new ListVendorExtension<>("servers", srvs);
    }
}