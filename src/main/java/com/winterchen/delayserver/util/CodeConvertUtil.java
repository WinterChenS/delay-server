package com.winterchen.delayserver.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author winterchen 2020/5/20
 */
public class CodeConvertUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeConvertUtil.class);

    private static final Map<String, String> codeMsg = new ConcurrentHashMap<>();


    static {
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource("i18n/delay_server_zh_CN.properties"));
            Iterator<Map.Entry<Object, Object>> iterator = properties.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> next = iterator.next();
                codeMsg.put(next.getKey().toString(), next.getValue() == null ? null : next.getValue().toString());
            }
        } catch (IOException e) {
            LOGGER.error("init code convert source error !! ");
        }
    }

    public static String convertCodeToMsg(String code) {
        return codeMsg.get(code);
    }


}
