//package com.bigking.springcloud.pojo;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.webex.mats.fraud.aop.ApiOAuthScope;
//import com.webex.mats.fraud.bean.FraudApiServiceResponse;
//import com.webex.mats.fraud.bean.InvokeServiceResultPOJO;
//import com.webex.mats.fraud.bean.metriclog.MetricsTimeLine;
//import com.webex.mats.fraud.bean.metriclog.ThreadLocalMetricsTimeLine;
//import com.webex.mats.fraud.common.utils.DateUtils;
//import com.webex.mats.fraud.common.utils.IPUtils;
//import com.webex.mats.fraud.service.ci.CiService;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.Date;
//
///**
// * @author devwang
// */
//public class AuthenticationInterceptor implements HandlerInterceptor {
//    private static final Log LOG = LogFactory.getLog(AuthenticationInterceptor.class);
//    private static final String TRACKING_CODE_HEADER = "Tracking-Code";
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String SOURCE_HEADER = "Source";
//    private static final String SOURCE_MATS_PORTAL = "MATS_PORTAL";
//    private static final String AUTHORIZATION_TYPE_BASIC = "Basic";
//    private static final String AUTHORIZATION_TYPE_OAUTH = "Bearer";
//    private static final String BLANK_SPACE = " ";
//    private static final String COLON = ":";
//    private static final String SESSION_TIME_OUT_FAILURE_REASON = "Invalid access token";
//
//    @Value("${authentication.basic.username}")
//    private String basicUserName;
//    @Value("${authentication.basic.password}")
//    private String basicPassword;
//
//    @Autowired
//    private CiService ciService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
//        ThreadLocalMetricsTimeLine.add(new MetricsTimeLine());
//        Date currentUtcDateTime = DateUtils.getCurrentUTCDateTime();
//        ThreadLocalMetricsTimeLine.getMetricsTimeLine().setHttpRequestStartTime(currentUtcDateTime);
//        if (httpServletRequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
//            return true;
//        }
//        String trackingCode = httpServletRequest.getHeader(TRACKING_CODE_HEADER);
//        String source = httpServletRequest.getHeader(SOURCE_HEADER);
//        String[] scopes = null;
//        String message, errorMessage;
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod h = (HandlerMethod) handler;
//            if (h.hasMethodAnnotation(ApiOAuthScope.class)) {
//                scopes = h.getMethodAnnotation(ApiOAuthScope.class).scopes();
//            }
//        }
//        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
//
//        String requestURI = httpServletRequest.getRequestURI();
//        String remoteIp = httpServletRequest.getHeader("XX-Remote-IP");//customized headers XX-Remote-IP, XX-Remote-Host
//        if (StringUtils.isEmpty(remoteIp)) {
//            remoteIp = IPUtils.getRealIP(httpServletRequest);
//        }
//
//        String remoteHost = httpServletRequest.getHeader("XX-Remote-Host");
//        if (StringUtils.isEmpty(remoteHost)) {
//            InetAddress inetAddress = InetAddress.getByName(remoteIp);
//            remoteHost = inetAddress.getHostName();
//        }
//
//        String localIp = httpServletRequest.getLocalAddr();
//        String localHost = httpServletRequest.getLocalName();
//
//        LOG.info(String.format("Authentication start, HttpRequest:uri=%s, source=%s, method=%s, scope=%s, remoteIp=%s, remoteHost=%s, localIp=%s, localHost=%s, trackingCode=%s, authorization=%s",
//                requestURI,
//                source,
//                httpServletRequest.getMethod(),
//                Arrays.toString(scopes),
//                remoteIp,
//                remoteHost,
//                localIp,
//                localHost,
//                trackingCode,
//                authorizationHeader));
//        if (StringUtils.isBlank(authorizationHeader)) {
//            message = "Invalid authorization, wrong format, authorization is empty";
//            errorMessage = String.format("Authentication failed, uri=%s, source=%s, trackingCode=%s, error=%s, remoteIp=%s, remoteHost=%s, localIp=%s, localHost=%s, authorization=%s",
//                    requestURI, source, trackingCode, message, remoteIp, remoteHost, localIp, localHost, authorizationHeader);
//            LOG.error(errorMessage);
//            this.write401Response(httpServletResponse, message);
//            return false;
//        }
//
//        if (!authorizationHeader.contains(BLANK_SPACE)) {
//            message = "Invalid authorization, wrong format, authorization not contains blank space";
//            errorMessage = String.format("Authentication failed, uri=%s, source=%s, trackingCode=%s, error=%s, remoteIp=%s, remoteHost=%s, localIp=%s, localHost=%s, authorization=%s",
//                    requestURI, source, trackingCode, message, remoteIp, remoteHost, localIp, localHost, authorizationHeader);
//            LOG.error(errorMessage);
//            this.write401Response(httpServletResponse, message);
//            return false;
//        }
//
//        String[] authorizationHeaderArr = authorizationHeader.split(BLANK_SPACE);
//        if (authorizationHeaderArr.length > 2) {
//            message = "Invalid authorization, wrong format";
//            errorMessage = String.format("Authentication failed, uri=%s, source=%s, trackingCode=%s, error=%s, remoteIp=%s, remoteHost=%s, localIp=%s, localHost=%s, authorization=%s",
//                    requestURI, source, trackingCode, message, remoteIp, remoteHost, localIp, localHost, authorizationHeader);
//            LOG.error(errorMessage);
//            this.write401Response(httpServletResponse, message);
//            return false;
//        }
//        InvokeServiceResultPOJO invokeServiceResultPOJO;
//        switch (authorizationHeaderArr[0]) {
//            case AUTHORIZATION_TYPE_BASIC:
//                invokeServiceResultPOJO = this.basicAuthentication(authorizationHeaderArr, trackingCode);
//                break;
//            case AUTHORIZATION_TYPE_OAUTH:
//                invokeServiceResultPOJO = this.oauthAuthentication(authorizationHeaderArr, trackingCode, scopes);
//                break;
//            default:
//                invokeServiceResultPOJO = this.otherAuthentication(authorizationHeaderArr, trackingCode);
//                break;
//        }
//        if (!invokeServiceResultPOJO.isSuccess()) {
//            errorMessage = String.format("Authentication failed, uri=%s, source=%s, trackingCode=%s, error=%s, remoteIp=%s, remoteHost=%s, localIp=%s, localHost=%s, authorization=%s",
//                    requestURI, source, trackingCode, invokeServiceResultPOJO.getFailureReason(), remoteIp, remoteHost, localIp, localHost, authorizationHeader);
//            if (SOURCE_MATS_PORTAL.equalsIgnoreCase(source)
//                    && StringUtils.isNotBlank(invokeServiceResultPOJO.getFailureReason())
//                    && invokeServiceResultPOJO.getFailureReason().contains(SESSION_TIME_OUT_FAILURE_REASON)) {
//                LOG.warn(errorMessage);
//            } else {
//                LOG.error(errorMessage);
//            }
//            this.write401Response(httpServletResponse, invokeServiceResultPOJO.getFailureReason());
//            return false;
//        }
//        return true;
//    }
//
//    private void write401Response(HttpServletResponse httpServletResponse, String message) throws IOException {
//        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
//        httpServletResponse.setHeader("Content-Type", "application/json");
//
//        FraudApiServiceResponse fraudApiServiceResponse = new FraudApiServiceResponse();
//        fraudApiServiceResponse.setCode(HttpStatus.UNAUTHORIZED.value());
//        fraudApiServiceResponse.setMessage(message);
//        fraudApiServiceResponse.setResult(null);
//
//        PrintWriter printWriter = httpServletResponse.getWriter();
//        printWriter.println(JSON.toJSONString(fraudApiServiceResponse, SerializerFeature.WriteMapNullValue));
//        printWriter.flush();
//        printWriter.close();
//    }
//
//    private InvokeServiceResultPOJO basicAuthentication(String[] authorizationHeaderArr, String tackingCode) {
//        String token = authorizationHeaderArr[1];
//        LOG.info(String.format("basicAuthentication, %s is: %s, token is: %s", TRACKING_CODE_HEADER, tackingCode, token));
//
//        InvokeServiceResultPOJO invokeServiceResultPOJO = new InvokeServiceResultPOJO();
//        boolean isSuccess = null != token && token.equals(new String(Base64.getEncoder().encode((basicUserName + COLON + basicPassword).getBytes())));
//        invokeServiceResultPOJO.setSuccess(isSuccess);
//        if (!isSuccess) {
//            invokeServiceResultPOJO.setFailureReason("Invalid authorization");
//        }
//        return invokeServiceResultPOJO;
//    }
//
//    private InvokeServiceResultPOJO oauthAuthentication(String[] authorizationHeaderArr, String tackingCode, String[] scopes) {
//        LOG.info(String.format("oauthAuthentication, %s is: %s, scope is: %s", TRACKING_CODE_HEADER, tackingCode, Arrays.toString(scopes)));
//        try {
//            ciService.verifyAccessToken(tackingCode, authorizationHeaderArr[1], scopes);
//            return new InvokeServiceResultPOJO(true, "", null);
//        } catch (Exception e) {
//            LOG.warn(String.format("oauthAuthentication error, tackingCode=%s, error=%s.", tackingCode, e.getMessage()), e);
//            return new InvokeServiceResultPOJO(false, e.getMessage(), null);
//        }
//    }
//
//    private InvokeServiceResultPOJO otherAuthentication(String[] authorizationHeaderArr, String tackingCode) {
//        LOG.error(String.format("Not support authorization type: %s, %s is: %s", authorizationHeaderArr[0], TRACKING_CODE_HEADER, tackingCode));
//        return new InvokeServiceResultPOJO(false, "Invalid authorization, unsupported authorization type", null);
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
//        LOG.debug("---AuthenticationInterceptor postHandle---");
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) throws Exception {
//        LOG.debug("---AuthenticationInterceptor afterCompletion---");
//    }
//}
