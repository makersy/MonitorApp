package com.sust.monitorapp.util;

import com.sust.monitorapp.common.AppConfig;

import java.io.IOException;

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

public class MyHttp {

    //设置request的数据编码
    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * http get方式
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

    /**
     * http post方式
     *
     * @param method   调用的接口名
     * @param postBody 请求参数
     *                 username=xxx&userid=xxx
     * @return
     * @throws IOException
     */
    public static Response post(String method, String postBody) throws IOException {

        String url = AppConfig.BASEURL + method;

        System.out.println("---url is: " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response;
    }
}
