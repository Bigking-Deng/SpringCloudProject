package com.bigking.springcloud.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PropertiesUtil {
    private static Log logger = LogFactory.getLog(PropertiesUtil.class);

    private static List<String> fileNames = Arrays.asList("/opt/webex/mats/WBXmatsfraudbackendservice/config/application.properties",
            "/opt/webex/mats/WBXmatsfraudbackendservice/config/secret.properties");
    private static Properties ps = new Properties();

    static {
        InputStream is = null;
        try {
            for(String fileName : fileNames){
                File propertyFile = new File(fileName);
                if(propertyFile.exists()){
                    is = new FileInputStream(propertyFile);
                    ps.load(is);
                }
            }
        } catch (IOException e) {
            logger.error("config file is not exist...", e);
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("close file to fail...", e);
                }
            }
        }
    }

    private PropertiesUtil() {
    }

    public static String getProperty(String key) {
        String value = "";
        value = ps.getProperty(key);
        return value;
    }

    public static Properties getProperties() {
        return ps;
    }
}
