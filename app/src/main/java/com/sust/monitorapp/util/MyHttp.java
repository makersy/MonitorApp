package com.sust.monitorapp.util;

import com.sust.monitorapp.common.AppConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by yhl on 2020/2/17.
 *
 * 参考：https://blog.csdn.net/wang1171405487/article/details/80731864
 */

public class MyHttp {

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * http get方式
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
     * @param method 调用的接口名
     * @param requestBody 请求体
     *  RequestBody requestBody = new FormBody.Builder()
     *      .add("strRequest",order)
     *      .build();
     * @return
     * @throws IOException
     */
    public static Response post(String method, RequestBody requestBody) throws IOException {

        String url = AppConfig.BASEURL + method;

        System.out.println("---url is: " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response;
    }
}
