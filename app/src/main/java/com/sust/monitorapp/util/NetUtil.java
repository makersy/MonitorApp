package com.sust.monitorapp.util;

import com.sust.monitorapp.common.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yhl on 2020/2/17.
 * <p>
 * 参考：https://blog.csdn.net/wang1171405487/article/details/80731864
 */

public class NetUtil {

    //设置request的数据编码
    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final int CONNECTION_TIME_OUT = 2000;//连接超时时间
    private static final int SOCKET_TIME_OUT = 2000;//读写超时时间

    //初始化OkHttpClient
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(SOCKET_TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(SOCKET_TIME_OUT, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(false) //自动重连设置为false
            .connectionPool(new ConnectionPool(10, 5000, TimeUnit.MILLISECONDS))
            .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.MILLISECONDS)
            .build();

    /**
     * 访问后台服务器：http get方式
     *
     * @param method：api名+参数
     * @return 服务器响应
     */
    public static Response get(String method) throws IOException {

        String url = AppConfig.BASEURL + method;

        System.out.println("-----------url is: " + url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response;
    }

    public static Response urlget(String url) throws IOException {

        System.out.println("-----------url is: " + url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response;
    }
    /**
     * 访问后台服务器：http post方式
     *
     * @param method   调用的接口名
     * @param postBody 请求参数 username=xxx&userid=xxx
     * @return 服务器响应
     */
    public static Response post(String method, String postBody) throws IOException {

        String url = AppConfig.BASEURL + method;

        JSONObject jsonObject = new JSONObject();
        String[] kv = postBody.split("&");
        for (String str : kv) {
            String[] strs = str.split("=");
            try {
                jsonObject.put(strs[0], strs[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println("---url is: " + url);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, String.valueOf(jsonObject));
        System.out.println(requestBody.contentLength());

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response;
    }

    /**
     * 查询LBS地址信息
     * @param method 具体方法及参数
     * @return 服务器响应
     */
    public static Response queryAddress(String method) throws IOException {

        String url = AppConfig.LbsQueryURL + method;

        System.out.println("-----------url is: " + url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response;
    }
}
