package com.sust.monitorapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Created by yhl on 2020/2/18.
 */

public class JsonUtil {

//    private static Gson gson = new Gson();

    //静态变量，全局只有一个对象
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            .create();


    /**
     * 将 json字符串转换成 java 对象
     *
     * @param jsonString json字符串
     * @param typeOfT    目标对象类型，复杂对象类型实例：
     *                   new TypeToken<HashMap<String, String>>(){}.getType()
     * @param <T>        目标对象类型
     * @return 转换之后的 java对象
     */
    public static <T> T jsonToBean(String jsonString, Type typeOfT) {
        T t = gson.fromJson(jsonString, typeOfT);
        return t;
    }

    /**
     * 将对象转换成 json字符串
     *
     * @param object 原对象
     * @return 转换之后的json字符串
     */
    public static String objToJson(Object object) {
        return gson.toJson(object);
    }
}
