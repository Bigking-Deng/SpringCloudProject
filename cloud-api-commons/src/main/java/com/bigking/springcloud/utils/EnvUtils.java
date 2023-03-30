package com.bigking.springcloud.utils;

import sun.tools.java.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EnvUtils {
    public static String getIPAddress(){
        String ip = "localhost";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
