package com.bigking.springcloud.pojo;

import java.io.Serializable;

/**
 * @author devwang
 * HttpClientUtils Common Response POJO
 */
public class HttpResponsePojo implements Serializable {
    private static final long serialVersionUID = -5002484256687048879L;

    private int statusCode;
    private String reasonPhrase;
    private String body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpResponsePojo{" +
                "statusCode=" + statusCode +
                ", reasonPhrase='" + reasonPhrase + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
