package com.bigking.springcloud.pojo;

import org.apache.axis2.java.security.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author devwang
 */
public class HttpsClientTrustManager extends X509ExtendedTrustManager {

    private static final Log LOG = LogFactory.getLog(HttpsClientTrustManager.class);

    public static void setHttpsClient() {
        SSLContext sslContext = null;
        HttpsClientTrustManager secureRestClientTrustManager = new HttpsClientTrustManager();
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{secureRestClientTrustManager}, null);
        } catch (Exception e) {
            LOG.error(e, e);
        }
        Protocol.registerProtocol("https",
                new Protocol("https", (ProtocolSocketFactory) new SSLProtocolSocketFactory(sslContext), 443));
    }

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {

        return new X509Certificate[0];
    }

    public boolean isClientTrusted(X509Certificate[] arg0) {

        return true;
    }

    public boolean isServerTrusted(X509Certificate[] arg0) {

        return true;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {

    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {

    }
}
