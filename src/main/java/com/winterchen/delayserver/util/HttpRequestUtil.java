package com.winterchen.delayserver.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.winterchen.delayserver.constants.Constants;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author winterchen 2020/5/20
 */
public class HttpRequestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestUtil.class);

    private static OkHttpClient client;

    private final static int CONNECT_TIMEOUT =20;
    private final static int READ_TIMEOUT=15;
    private final static int WRITE_TIMEOUT=10;

    static {
        client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public static String get(String url){
        return get(url,null, new Headers.Builder().add(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE).build());
    }


    public static String get(String url, Map<String, Object> params){
        return get(url, params, new Headers.Builder().add(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE).build());
    }

    public static String get(String url, Headers headers){
        return get(url, null, headers);
    }

    public static String get(String url, Map<String, Object> params, Headers headers){
        if (StringUtils.isEmpty(url))
            throw new NullPointerException("request url cannot null");
        url = url + formatGetParams(params);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE)
                .headers(headers)
                .build();
        try(Response response = client.newCall(request).execute()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("请求返回值： {}", response);
            }
            if (response.isSuccessful()){
                return response.body().string();
            }else {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("请求失败", e);
        }
        return null;
    }

    public static JsonObject post(String url, Map<String, Object> params){
        return post(url, params,  new Headers.Builder().add(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE).build());
    }

    public static JsonObject post(String url, Map<String, Object> params, Headers headers){
        if (StringUtils.isEmpty(url))
            throw new NullPointerException("请求url为空");
        String json = formatPostJsonFromMap(params);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header(Constants.USER_AGENT_KEY, Constants.USER_AGENT_VALUE)
                .headers(headers)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            Gson gson = new Gson();
            return gson.fromJson(response.body().string(), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String formatPostJsonFromMap(Map<String, Object> params){
        if (null == params || 0 == params.size())
            return "";
        Gson gson = new Gson();
        return gson.toJson(params);
    }

    /**
     * transfer map<K,V> to url params
     * @param params
     * @return
     */
    private static String formatGetParams(Map<String, Object> params){
        if (null == params || 0 == params.size())
            return "";
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        result.append("?");
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            result.append(next.getKey());
            result.append("=");
            result.append(next.getValue());
            if (iterator.hasNext())
                result.append("&");
        }
        return result.toString();
    }
}
