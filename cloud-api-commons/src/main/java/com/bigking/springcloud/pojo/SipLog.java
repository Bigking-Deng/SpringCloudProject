package com.bigking.springcloud.pojo;

import com.google.gson.JsonObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class SipLog implements Serializable {
    public String callId;
    public String timestamp;
    public String agent;
    public String wxcServerType;
    public String wxcServerStatus;
    public JsonObject host;
}
