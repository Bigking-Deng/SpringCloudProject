package com.bigking.springcloud.utils;



import com.bigking.springcloud.Exception.ESQueryException;
import com.bigking.springcloud.pojo.HttpDeleteWithBody;
import com.bigking.springcloud.pojo.HttpResponsePojo;
import com.bigking.springcloud.pojo.HttpsClientTrustManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.fluent.*;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author devwang
 */
public class HttpClientUtils {
    private static final Log LOG = LogFactory.getLog(HttpClientUtils.class);
    private static final int timeout = 15000;
    private static final String TLS = "TLS";
    private static final String SSL = "SSL";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int CONNECTION_REQ_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 1000;
    private static final int DEFAULT_CONNECTION_REQ_TIMEOUT = 1000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 20000;
    private static final int MAX_PER_ROUTE = 200;
    private static final int MAX_TOTAL = 400;
    private static final long KEEP_ALIVE_SECONDS = 5L;
    private static final X509HostnameVerifier HOSTNAME_VERIFIER = new X509HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

        @Override
        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            //Do nothing
        }

        @Override
        public void verify(String host, X509Certificate cert) throws SSLException {
            //Do nothing
        }

        @Override
        public void verify(String host, SSLSocket ssl) throws IOException {
            //Do nothing
        }
    };
    private static final ConnectionKeepAliveStrategy KEEP_ALIVE_STRATEGY = new ConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    try {
                        return Long.parseLong(value) * 1000;
                    } catch (NumberFormatException ignore) {
                    }
                }
            }

            return KEEP_ALIVE_SECONDS * 1000;
        }
    };
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER = CONNECTION_MANAGER(TLS);
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setCircularRedirectsAllowed(true)
            .setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQ_TIMEOUT)
            .setSocketTimeout(SOCKET_TIMEOUT).setStaleConnectionCheckEnabled(true).build();
    private static final HttpClient HTTP_CLIENT = CLIENT();

    private HttpClientUtils() {
        //Do nothing
    }

    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER(String protocol) {
        HttpsClientTrustManager secureRestClientTrustManager = new HttpsClientTrustManager();
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance(protocol);
            sslcontext.init(null, new TrustManager[]{secureRestClientTrustManager}, null);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e, e);
        } catch (KeyManagementException e) {
            LOG.error(e, e);
        }

        SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(
                sslcontext == null ? SSLContexts.createDefault() : sslcontext, HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", ssl).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        cm.setMaxTotal(MAX_TOTAL);
        return cm;
    }

    private static final HttpEntity ENTITY(String requestBody, Map<String, String> parameters) {
        EntityBuilder entityBuilder = EntityBuilder.create().setText(requestBody == null ? "" : requestBody);
        if (parameters != null && !parameters.isEmpty()) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            Set<String> parametersKeySet = parameters.keySet();
            for (String key : parametersKeySet) {
                nameValuePairs.add(new BasicNameValuePair(key, parameters.get(key)));
            }
            entityBuilder.setParameters(nameValuePairs);
        }
        return entityBuilder.build();
    }

    private static final Request GET(String uri, Integer connectTimeout) {
        Request request = Request.Get(uri);
        if (null != connectTimeout) {
            request.connectTimeout(connectTimeout);
        }
        return request;
    }

    private static final Request GET(String uri, Integer connectTimeout,Integer socketTimeout) {
        Request request = Request.Get(uri);
        if (null != connectTimeout) {
            request.connectTimeout(connectTimeout);
        }
        if (null != socketTimeout) {
            request.socketTimeout(socketTimeout);
        }
        return request;
    }

    private static final Request POST(String uri, Integer connectTimeout, String requestBody, Map<String, String> parameters) {
        Request request = Request.Post(uri).body(ENTITY(requestBody, parameters));
        if (null != connectTimeout) {
            request.connectTimeout(connectTimeout);
        }
        return request;
    }


    private static final Request POST(String uri, Integer connectTimeout, Integer socketTimeout, String requestBody, Map<String, String> parameters) {
        Request request = Request.Post(uri).body(ENTITY(requestBody, parameters));
        if (null != connectTimeout) {
            request.connectTimeout(connectTimeout);
        }
        if (null != socketTimeout) {
            request.socketTimeout(socketTimeout);
        }
        return request;
    }

    private static final Request PUT(String uri, String requestBody, Map<String, String> parameters) {
        return Request.Put(uri).body(ENTITY(requestBody, parameters));
    }

    private static final Request DELETE(String uri) {
        return Request.Delete(uri);
    }

    private static final void SET_HEADERS(Request request, Header[] headers) {
        if (headers != null && headers.length > 0) {
            request.setHeaders(headers);
        }
    }

    private static final void SET_PATCH_HEADERS(HttpPatch httpPatch, Header[] headers) {
        if (headers != null && headers.length > 0) {
            for (Header header : headers) {
                httpPatch.setHeader(header);
            }
        }
    }

    private static final HttpClient CLIENT() {
        HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(REQUEST_CONFIG)
                .setConnectionManager(CONNECTION_MANAGER).setKeepAliveStrategy(KEEP_ALIVE_STRATEGY).build();

        new IdleConnectionMonitorThread(CONNECTION_MANAGER).start();
        return httpClient;
    }

    private static final Executor EXECUTOR() {
        return Executor.newInstance(HTTP_CLIENT);
    }

    private static Header[] wrapHeader(Map<String, String> headers) {
        if (null != headers) {
            Header[] retPar = new Header[headers.size()];
            int m = 0;
            for (String key : headers.keySet()) {
                retPar[m] = new BasicHeader(key, headers.get(key));
                m++;
            }
            return retPar;
        }
        return null;
    }

    //getSync
    public static HttpResponsePojo getSync(String uri, Header... headers) {
        return getSync(uri, null, headers);
    }

    public static HttpResponsePojo getSync(String uri, int connectTimeout, Map<String, String> headers) {
        return getSync(uri, connectTimeout, wrapHeader(headers));
    }

    public static HttpResponsePojo getSync(String uri, Integer connectTimeout, Header... headers) {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            Request request = GET(uri, connectTimeout);
            SET_HEADERS(request, headers);
            LOG.info(String.format("Executing get request: %s", request.toString()));
            Response response = EXECUTOR().execute(request);
            HttpResponse httpResponse = response.returnResponse();
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing get response: %s", httpResponsePojo.toString()));
        } catch (Exception e) {
            LOG.error(uri, e);
            processErrorResponse(httpResponsePojo);
        }
        return httpResponsePojo;
    }

    public static HttpResponsePojo getSync(String uri, Integer connectTimeout, Integer socketTimeout, Header... headers) {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            Request request = GET(uri, connectTimeout, socketTimeout);
            SET_HEADERS(request, headers);
            LOG.info(String.format("Executing get request: %s", request.toString()));
            Response response = EXECUTOR().execute(request);
            HttpResponse httpResponse = response.returnResponse();
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing get response: %s", httpResponsePojo.toString()));
        } catch (Exception e) {
            LOG.warn(uri, e);
            processErrorResponse(httpResponsePojo);
        }
        return httpResponsePojo;
    }

    //postSync
    public static HttpResponsePojo postSync(String uri, String requestBody, Header... headers) {
        return postSync(uri, null, null, null, requestBody, headers);
    }

    public static HttpResponsePojo postSync(String uri, String requestBody, HashMap<String, String> headers) {
        return postSync(uri, null, null, null, requestBody, wrapHeader(headers));
    }

    public static HttpResponsePojo postSync(String uri, Map<String, String> parameters, String requestBody, Header... headers) {
        return postSync(uri, null, null, parameters, requestBody, headers);
    }

    public static HttpResponsePojo postSync(String uri, Integer connectTimeout, Map<String, String> parameters, String requestBody, Header... headers) {
        return postSync(uri, connectTimeout, null, parameters, requestBody, headers);
    }

    //timeout in millisecond
    public static HttpResponsePojo postSync(String uri, Integer connectTimeout, Integer socketTimeout, Map<String, String> parameters, String requestBody, Header... headers) {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            Request request = POST(uri, connectTimeout, socketTimeout, requestBody, parameters);
            SET_HEADERS(request, headers);
            LOG.info(String.format("Executing post request: %s", request.toString()));
            Response response = EXECUTOR().execute(request);
            HttpResponse httpResponse = response.returnResponse();
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing post response: %s", httpResponsePojo.toString()));
        } catch (Exception e) {
            LOG.warn(String.format("Executing post warn, uri=%s, connectTimeout=%s ms, socketTimeout=%s ms, error=%s.", uri, connectTimeout, socketTimeout, e.getMessage()), e);
            processErrorResponse(httpResponsePojo);
        }
        return httpResponsePojo;
    }

    //timeout in millisecond
    public static HttpResponsePojo postSyncForES(String uri, String requestBody, HashMap<String, String> headers, Integer socketTimeout) throws ESQueryException {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            Request request = POST(uri, CONNECT_TIMEOUT * 2, socketTimeout, requestBody, null);
            SET_HEADERS(request, wrapHeader(headers));
            LOG.info(String.format("Executing post request: %s", request.toString()));
            Response response = EXECUTOR().execute(request);
            HttpResponse httpResponse = response.returnResponse();
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing post request: %s, response code: %s", request.toString(), httpResponsePojo.getStatusCode()));
        } catch (Exception e) {
            LOG.warn("Executing post request exception info,uri=" + uri + ",body=" + requestBody);
            LOG.warn(uri, e);
            processErrorResponse(httpResponsePojo);
            throw new ESQueryException(e.getMessage(), e);
        }
        return httpResponsePojo;
    }

    //patch
    public static HttpResponsePojo patch(String uri, String requestBody, Header... headers) {
        LOG.info(String.format("Executing patch request: %s", uri));
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            HttpClient httpClient = HTTP_CLIENT;
            HttpPatch httpPatch = new HttpPatch(uri);
            SET_PATCH_HEADERS(httpPatch, headers);
            StringEntity entity = new StringEntity(requestBody);
            httpPatch.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(httpPatch);
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing patch response: %s", httpResponsePojo.toString()));
        } catch (Exception e) {
            LOG.error(uri, e);
            processErrorResponse(httpResponsePojo);
        }
        return httpResponsePojo;
    }

    //putSync
    public static HttpResponsePojo putSync(String uri, Map<String, String> parameters, String requestBody,
                                           Header... headers) {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            Request request = PUT(uri, requestBody, parameters);
            SET_HEADERS(request, headers);
            LOG.info(String.format("Executing put request: %s", request.toString()));
            Response response = EXECUTOR().execute(request);
            HttpResponse httpResponse = response.returnResponse();
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing put response: %s", httpResponsePojo.toString()));
        } catch (Exception e) {
            LOG.error(uri, e);
            processErrorResponse(httpResponsePojo);
        }
        return httpResponsePojo;
    }

    //deleteSync
    public static HttpResponsePojo deleteSync(String uri, Header... headers) {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        try {
            Request request = DELETE(uri);
            SET_HEADERS(request, headers);
            LOG.info(String.format("Executing delete request: %s", request.toString()));
            Response response = EXECUTOR().execute(request);
            HttpResponse httpResponse = response.returnResponse();
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing delete response: %s", httpResponsePojo.toString()));
        } catch (Exception e) {
            LOG.error(uri, e);
            processErrorResponse(httpResponsePojo);
        }
        return httpResponsePojo;
    }

    //deleteSync
    public static HttpResponsePojo deleteSyncWithBody(String uri, String requestBody, Header... headers) {
        HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).
                setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build();
        try(CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build()) {
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(uri);
            StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
            httpDelete.setHeaders(headers);
            httpDelete.setEntity(stringEntity);
            HttpResponse httpResponse = httpclient.execute(httpDelete);
            processResponse(httpResponsePojo, httpResponse);
            LOG.info(String.format("Executing delete with body response: %s", httpResponsePojo.toString()));
        } catch (ConnectTimeoutException ce) {
            processErrorResponse(httpResponsePojo);
            httpResponsePojo.setReasonPhrase(ConnectTimeoutException.class.getSimpleName());
        } catch (SocketTimeoutException se) {
            processErrorResponse(httpResponsePojo);
            httpResponsePojo.setReasonPhrase(SocketTimeoutException.class.getSimpleName());
        } catch (Exception e) {
            LOG.error(uri, e);
            processErrorResponse(httpResponsePojo);
        }

        return httpResponsePojo;
    }

    //getAsync
    public static String getAsync(String uri, ResponseHandler<String> responseHandler, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getAsync(uri, responseHandler, CONNECT_TIMEOUT, headers);
    }

    public static String getAsync(String uri, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getAsync(uri, CONNECT_TIMEOUT, headers);
    }

    public static String getAsync(String uri, ResponseHandler<String> responseHandler, long timeout, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        Request request = GET(uri, null);
        SET_HEADERS(request, headers);
        LOG.info(String.format("Executing getAsync, request: %s", request.toString()));
        Future<String> future = Async.newInstance().use(EXECUTOR()).execute(request, responseHandler);
        return future.get(timeout, TimeUnit.SECONDS);
    }

    public static String getAsync(String uri, long timeout, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getAsync(uri, new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws IOException {
                LOG.info(String.format("Executing getAsync, response: %s", response.getStatusLine().toString()));
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return result;
            }
        }, timeout, headers);
    }

    public static String getAsync(String uri, long timeout, Map<String, String> headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        return getAsync(uri, timeout, wrapHeader(headers));
    }

    public static Content getAsync(String uri, FutureCallback<Content> callback, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        Request request = GET(uri, null);
        SET_HEADERS(request, headers);
        LOG.info(String.format("Executing getAsync, request: %s", request.toString()));
        return Async.newInstance().use(EXECUTOR()).execute(request, callback).get(CONNECT_TIMEOUT, TimeUnit.SECONDS);
    }

    //postAsync
    public static Content postAsync(String uri, Map<String, String> parameters, String requestBody,
                                    FutureCallback<Content> callback, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        Request request = POST(uri, null, requestBody, parameters);
        SET_HEADERS(request, headers);
        LOG.info(String.format("Executing postAsync, request: %s", request.toString()));
        return Async.newInstance().use(EXECUTOR()).execute(request, callback).get(CONNECT_TIMEOUT, TimeUnit.SECONDS);
    }

    public static String postAsync(String uri, Map<String, String> parameters, String requestBody,
                                   ResponseHandler<String> responseHandler, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        Request request = POST(uri, null, requestBody, parameters);
        SET_HEADERS(request, headers);
        LOG.info(String.format("Executing postAsync, request: %s", request.toString()));
        Future<String> future = Async.newInstance().use(EXECUTOR()).execute(request, responseHandler);
        return future.get(CONNECT_TIMEOUT, TimeUnit.SECONDS);
    }

    public static String postAsync(String uri, Map<String, String> parameters, String requestBody, Header... headers)
            throws InterruptedException, ExecutionException, TimeoutException {
        return postAsync(uri, parameters, requestBody, new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws IOException {
                LOG.info(String.format("Executing postAsync, response: %s", response.getStatusLine().toString()));
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new ClientProtocolException(
                            "Unexpected http response status. " + response.getStatusLine().toString());
                }
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                return result;
            }
        }, headers);
    }

    public static HttpResponsePojo getResponse4GET(String url, MultivaluedMap<String, String> headersMap)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpResponsePojo ret = new HttpResponsePojo();
        CloseableHttpClient closeableHttpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            if (headersMap != null) {
                Header[] headers = convert2Headers(headersMap);
                httpGet.setHeaders(headers);
            }
            closeableHttpClient = createHttpClient(url);
            closeableHttpResponse = closeableHttpClient.execute(httpGet);
            processResponse(ret, closeableHttpResponse);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("get http response error. ", e);
            throw e;
        } catch (KeyManagementException e) {
            LOG.error("get http response error. ", e);
            throw e;
        } catch (Exception e) {
            LOG.error("get http response error. ", e);
            throw e;
        } finally {
            closeHttpResponse(closeableHttpResponse);
            closeHttpClient(closeableHttpClient);
        }
        return ret;
    }

    public static HttpResponsePojo getResponse4Delete(String url, MultivaluedMap<String, String> headersMap)
            throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpResponsePojo ret = new HttpResponsePojo();
        CloseableHttpClient closeableHttpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            HttpDelete httpdelete = new HttpDelete(url);
            if (headersMap != null) {
                Header[] headers = convert2Headers(headersMap);
                httpdelete.setHeaders(headers);
            }
            closeableHttpClient = createHttpClient(url);
            closeableHttpResponse = closeableHttpClient.execute(httpdelete);
            processResponse(ret, closeableHttpResponse);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("delete http response error", e);
            throw e;
        } catch (KeyManagementException e) {
            LOG.error("delete http response error", e);
            throw e;
        } catch (Exception e) {
            LOG.error("delete http response error", e);
            throw e;
        } finally {
            closeHttpResponse(closeableHttpResponse);
            closeHttpClient(closeableHttpClient);
        }
        return ret;
    }

    public static HttpResponsePojo httpRequest(HttpRequestBase httpRequestBase) throws IOException {
        CloseableHttpClient closeableHttpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            closeableHttpClient = createHttpClient("");
            closeableHttpResponse = closeableHttpClient.execute(httpRequestBase);
            HttpResponsePojo httpResponsePojo = new HttpResponsePojo();
            processResponse(httpResponsePojo, closeableHttpResponse);
            return httpResponsePojo;
        } catch (Exception e) {
            LOG.error("Http request error.", e);
            return null;
        } finally {
            closeHttpResponse(closeableHttpResponse);
            closeHttpClient(closeableHttpClient);
        }
    }

    private static void closeHttpResponse(CloseableHttpResponse httpResponse) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (IOException e) {
            LOG.error("close http response error.", e);
        }
    }

    private static void closeHttpClient(CloseableHttpClient closeableHttpClient) {
        try {
            if (closeableHttpClient != null) {
                closeableHttpClient.close();
            }
        } catch (IOException e) {
            LOG.error("close http client error.", e);
        }
    }

    private static CloseableHttpClient createHttpClient(String uri) throws NoSuchAlgorithmException, KeyManagementException {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).
                setSocketTimeout(timeout).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        if (StringUtils.isEmpty(uri)) {
            return httpClient;
        }
        if (uri.startsWith("https")) {
            HttpsClientTrustManager secureRestClientTrustManager = new HttpsClientTrustManager();
            SSLContext sc = SSLContext.getInstance(SSL);
            sc.init(null, new TrustManager[]{secureRestClientTrustManager}, new SecureRandom());

            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc,
                    new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, null, new NoopHostnameVerifier());
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        }
        return httpClient;
    }

    private static Header[] convert2Headers(MultivaluedMap<String, String> headersMap) {
        if (headersMap == null) {
            return null;
        }
        List<Header> headers = new ArrayList<>();
        Set<Map.Entry<String, List<String>>> entrySet = headersMap.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                Header header = new BasicHeader(key, value);
                headers.add(header);
            }
        }

        return headers.toArray(new Header[0]);
    }

    private static void processErrorResponse(HttpResponsePojo httpResponsePojo) {
        httpResponsePojo.setStatusCode(org.apache.commons.httpclient.HttpStatus.SC_INTERNAL_SERVER_ERROR);
        httpResponsePojo.setReasonPhrase("Execute Http Request Error");
        httpResponsePojo.setBody(null);
    }

    private static void processResponse(HttpResponsePojo httpResponsePojo, HttpResponse httpResponse) throws IOException {
        HttpEntity entity = httpResponse.getEntity();
        String entityStr = "";
        if (entity != null) {
            entityStr = EntityUtils.toString(entity);
        }
        StatusLine statusLine = httpResponse.getStatusLine();
        if (null != statusLine) {
            httpResponsePojo.setStatusCode(statusLine.getStatusCode());
            httpResponsePojo.setReasonPhrase(statusLine.getReasonPhrase());
        }
        httpResponsePojo.setBody(entityStr);
        EntityUtils.consume(entity);
    }
}

