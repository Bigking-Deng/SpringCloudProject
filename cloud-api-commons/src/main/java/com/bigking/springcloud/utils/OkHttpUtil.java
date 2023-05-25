package com.bigking.springcloud.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;

public class OkHttpUtil {

    private static final MediaType jsonType = MediaType.parse("application/json");

    private static OkHttpClient clientInstance=null;
    private static OkHttpClient clientInstancewithNoRedirects=null;

    public static OkHttpClient getClientInstance(){
        if(clientInstance==null){
            synchronized (OkHttpUtil.class){
                if(clientInstance==null){
                    init();
                }
            }
        }
        return clientInstance;
    }

    public static OkHttpClient getClientInstancewithNoRedirects(){
        if(clientInstancewithNoRedirects==null){
            synchronized (OkHttpUtil.class){
                if(clientInstancewithNoRedirects==null){
                    init();
                }
            }
        }
        return clientInstancewithNoRedirects;
    }


    private static void init(){

        if(clientInstance==null){
            clientInstance = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
        }

        if(clientInstancewithNoRedirects==null){
            clientInstancewithNoRedirects = new OkHttpClient().newBuilder()
                    .followRedirects(false)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
        }
    }

    public static Response get(String url, OkHttpClient client){
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
           e.printStackTrace();
        }
        return response;
    }


    public static Response get(String url, Headers headers, OkHttpClient client){
        Request request = new Request.Builder().url(url).headers(headers).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static Response post(String url, RequestBody body, Headers headers, OkHttpClient client){
        Response response = null;
        Request request = new Request.Builder().url(url).headers(headers).post(body).build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }



    public static void main(String[] args) {
        String ss= "<form id=\"login-form\" method=\"post\" name=\"login-form\" action=\"(.+?)\">";
        Pattern p = Pattern.compile(ss);
        Matcher m = p.matcher("\n" +
                "              <div class=\"info-text\">\n" +
                "                <div class=\"close\"><span tabindex=\"0\"></span></div>\n" +
                "                <span data-text=\"Cisco_Login_Fed_Local_Msg_Update_Email_3\">If you <a class=\"modref\" href=\"//rpfa.cloudapps.cisco.com/rpfa/profile/profile_management.do\">update your Cisco.com account with your WebEx/Spark email address</a>, you can link your accounts in the future (which enables you to access secure Cisco, WebEx, and Spark resources using your WebEx/Spark login)</span>\n" +
                "              </div>\n" +
                "\n" +
                "              <form id=\"login-form\" method=\"post\" name=\"login-form\" action=\"https://cloudsso.cisco.com//as/uAuk1/resume/as/authorization.ping\">\n" +
                "                <ul>\n" +
                "                  <li id=\"containUser\">\n" +
                "                    <label id=\"usernameLabel\" for=\"userInput\" class=\"username-label hint\" data-text=\"Cisco_Login_Fed_Local_Content_User_Name\">Username or email</label>\n" +
                "                                          <input id=\"userInput\" class=\"\" type=\"text\" name=\"pf.username\" value=\"\" size=\"33\" maxlength=\"105\" autocomplete=\"off\" autocorrect=\"off\" autocapitalize=\"off\" tabindex=\"1\" />\n" +
                "                                        <p class=\"msg idNull\" data-text=\"Cisco_Login_Fed_Local_Content_Error_Msg_Id_Null\">We couldn't find that. Try again.</p>\n" +
                "                    <p class=\"forget\"><a class=\"modref\" data-url=\"CISCO_LOGIN_LOCAL_CONTENT_FORGOT_PASSWORD_URL\" href=\"//idpswd.cloudapps.cisco.com/emailSubmitAction.do\" data-text=\"Cisco_Login_Fed_Local_Content_Forgot_Username\">Forgot username?</a></p>\n" +
                "                  </li>\n" +
                "                  <li id=\"containPass\">\n" +
                "                    <label id=\"passwordLabel\" for=\"passwordInput\" class=\"password-label hint\" data-text=\"Cisco_Login_Fed_Local_Content_Password\">Password</label>\n" +
                "                    <input id=\"passwordInput\" class=\"\" type=\"password\" name=\"pf.pass\" value=\"\" size=\"33\" maxlength=\"105\"  autocomplete=\"off\" autocorrect=\"off\" autocapitalize=\"off\"  tabindex=\"2\" />\n" +
                "                    <p class=\"forget\"><a class=\"modref\" data-url=\"CISCO_LOGIN_LOCAL_CONTENT_FORGOT_PASSWORD_URL\" href=\"//idpswd.cloudapps.cisco.com/emailSubmitAction.do\" data-text=\"Cisco_Login_Fed_Local_Content_Forgot_Password\">Forgot password?</a></p>\n" +
                "                  </li>\n" +
                "                  <input type=\"hidden\" name=\"target\" value=\"\"/>\n" +
                "                </ul>\n" +
                "\n" +
                "                <!-- discovery helper message -->\n" +
                "                <div id=\"warning-msg-discovery\" class=\"warning-msg msg\">\n" +
                "                  <ul>\n" +
                "                    <!-- 3 new data-text ids matching orig 3 for continuity -->\n" +
                "                    <li data-text=\"Cisco_Login_Fed_Local_Content_Error_Msg_Password_Item_4\">Try entering your username (if you haven’t tried that already).</li>\n" +
                "                    <li data-text=\"Cisco_Login_Fed_Local_Content_Error_Msg_Password_Item_5\">If you recently created your account or changed your email address, check your email for a validation link from us. </li>\n" +
                "                    <li data-text=\"Cisco_Login_Fed_Local_Content_Error_Msg_Password_Item_6\"><a href=\"mailto:web-help@cisco.com\">Contact Cisco Support</a> for help.</li>\n" +
                "                  </ul>\n" +
                "                </div>\n" +
                "\n" +
                "                \n" +
                "                <!-- pass lookup values to/from ping -->\n" +
                "                                  <input type=\"hidden\" id=\"usertype\" name=\"pf.userType\" value=\"\">\n" +
                "                \n" +
                "                                  <input type=\"hidden\" id=\"idpId\" name=\"pf.idpId\" value=\"\">\n" +
                "                \n" +
                "                                  <input type=\"hidden\" id=\"isUserStale\" name=\"pf.isUserStale\" value=\"\">\n" +
                "                \n" +
                "                                  <input type=\"hidden\" id=\"idpurl\" name=\"pf.idpUrl\" value=\"\">\n" +
                "                \n" +
                "                                  <input type=\"hidden\" id=\"targetresource\" name=\"pf.TargetResource\" value=\"\">\n" +
                "                \n" +
                "                \n" +
                "                <input id=\"login-button\" type=\"submit\" name=\"login-button\" data-value=\"Cisco_Login_Fed_Local_Content_Btn_Login\" value=\"Log in\" tabindex=\"3\" />\n" +
                "                  <span id=\"login-button-next\" class=\"textholder\" data-text=\"Cisco_Login_Fed_Local_Content_Btn_Next\">Next</span>\n" +
                "                  <span id=\"login-button-submit\" class=\"textholder\" data-text=\"Cisco_Login_Fed_Local_Content_Btn_Login\">Log in</span>\n" +
                "                  <input type=\"hidden\" class=\"cancel\" name=\"pf.cancel\" value=\"\"/>\n" +
                "\n" +
                "              </form>\n" +
                "\n" +
                "              <div id=\"back-link\"> <a href=\"\" data-text=\"Cisco_Login_Fed_Local_Content_Back_Link\">&lt; Back</a></div>\n" +
                "\n" +
                "              <div class=\"\">\n" +
                "               <aside id=\"register\">\n" +
                "                                      <a class=\"modref\" data-url=\"CISCO_LOGIN_LOCAL_BTN_REGISTER_NOW_URL\" href=\"//idreg.cloudapps.cisco.com/idreg/guestRegistration.do\" data-text=\"Cisco_Login_Fed_Local_Btn_Register_Now\n" +
                "\">Create a new account</a>\n" +
                "                                </aside>\n" +
                "              </div>\n" +
                "          </article>\n" +
                "                  </div>\n" +
                "\n" +
                "        <div id=\"link-accounts-wrapper\">\n" +
                "                  </div>\n" +
                "\n" +
                "        <div id=\"redirect-msg-wrapper\">\n" +
                "                  </div>\n" +
                "\n" +
                "        <div id=\"create-cdc-wrapper\">\n" +
                "                  </div>\n" +
                "\n" +
                "        <!-- pass lookup values to/from ping - post -->\n" +
                "                  <input type=\"hidden\" id=\"link_idpurl\" name=\"pf.idpUrl\" value=\"\">\n" +
                "        \n" +
                "        <footer id=\"fw-footer\" class=\"container\">\n" +
                "          <nav id=\"fw-footer-nav\">\n" +
                "            <ul>\n" +
                "              <li><a data-url=\"CISCO_LOGIN_FOOTER_TERMS_AND_CONDITIONS_URL\" href=\"//www.cisco.com/web/siteassets/legal/terms_condition.html\" data-text=\"Cisco_Login_Footer_Terms_And_Conditions\">Terms &amp; Conditions</a></li>\n" +
                "              <li><a data-url=\"CISCO_LOGIN_FOOTER_PRIVACY_STATEMENT_URL\" href=\"//www.cisco.com/web/siteassets/legal/privacy_full.html\" data-text=\"Cisco_Login_Footer_Privacy_Short\">Privacy</a></li>\n" +
                "              <li><a data-url=\"CISCO_LOGIN_FOOTER_COOKIES_URL\" href=\"//www.cisco.com/web/siteassets/legal/privacy.html#cookies\" data-text=\"Cisco_Login_Footer_Cookies_Short\">Cookie Policy</a></li>\n" +
                "              <li><a data-url=\"CISCO_LOGIN_FOOTER_TRADEMARK_URL\" href=\"//www.cisco.com/web/siteassets/legal/trademark.html\" data-text=\"Cisco_Login_Footer_Trademarks\">Trademarks</a></li>\n" +
                "            </ul>\n" +
                "          </nav>\n" +
                "        </footer>\n" +
                "\n" +
                "      </div>\n" +
                "\n" +
                "    </div>\n");
        while(m.find()){
            System.out.println(m.group(1));
        }

    }
}
