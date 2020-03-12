package com.sust.monitorapp;

import android.widget.ScrollView;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.util.JsonUtil;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    private Scanner scanner;

    /**
     * 测试 JsonUtil 的使用
     */
    @Test
    public void lineChartData() {
        FileInputStream fis;

        try {
            fis = new FileInputStream("D:\\Code\\android\\MonitorApp\\app\\src\\test\\java\\com\\sust\\monitorapp\\a.txt");
            scanner = new Scanner(new InputStreamReader(fis, "utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Map<String, Float> lineData = new LinkedHashMap<>();

        int index = 0;
        while (scanner.hasNext()) {
            int num = scanner.nextInt();
            index++;
            lineData.put(String.valueOf(index), (float)num);
        }

        System.out.println();
        MyResponse myResponse = MyResponse.builder()
                .statusCode("101")
                .data(JsonUtil.objToJson(lineData))
                .build();
        System.out.println(JsonUtil.objToJson(myResponse));
    }

    @Test
    public void jsonFactory() {
        Device device = Device.builder()
                .devId("001")
                .devName("sust01")
                .owner("张三")
                .note("这是一个备注")
                .build();
        User user = User.builder()
                .username("张三")
                .email("afda@163.com")
                .userId("0001")
                .authority("管理员")
                .sex("男")
                .tel("13211111111")
                .build();
        MyResponse response = MyResponse.builder()
                .statusCode("101").data(JsonUtil.objToJson(user))
                .build();
        // {"statusCode":"101","data":"{\"devName\":\"sust01\",\"owner\":\"张三\"}"}
        /*
         {"statusCode":"101","data":"{\"username\":\"张三\",\"userId\":\"0001\",\"authority\":\"管理员\",\"sex\":\"男\",\"email\":\"afda@163.com\"}"}
         */
        System.out.println(JsonUtil.objToJson(response));
        // {"devName":"sust01","devId":"001","owner":"张三","note":"这是一个备注"}
//        System.out.println(JsonUtil.objToJson(user));

//        System.out.println(JsonUtil.objToJson(response));

    }

    @Test
    public void addition_isCorrect() {

        User user = User.builder()
                .username("张三").authority("管理员").email("abcde@163.com")
                .build();

        String str = JsonUtil.objToJson(user);

        MyResponse myResponse = MyResponse.builder()
                .statusCode("101")
                .data(str)
                .build();

        System.out.println(JsonUtil.objToJson(myResponse));

        User user1 = JsonUtil.jsonToBean(myResponse.getData(), User.class);
        System.out.println(user1.toString());
    }

    @Test
    public void jsonList() {

        List<Device> deviceList = new ArrayList<>();
        deviceList.add(Device.builder().devId("001").devName("sust 1号").owner("mike").note("备注1").build());
        deviceList.add(Device.builder().devId("002").devName("sust 2号").owner("john").note("备注2").build());
        deviceList.add(Device.builder().devId("003").devName("sust 3号").owner("nico").note("备注3").build());
        deviceList.add(Device.builder().devId("004").devName("sust 4号").owner("bob").note("备注4").build());

        TreeMap<String, String> map = Maps.newTreeMap();
        HashMap<String, String> map1 = Maps.newHashMap();
        map.put("0001", "张一");
        map.put("0002", "张二");
        map.put("0003", "张三");
        map.put("0004", "张四");
        map.put("0005", "张五");
        map.put("0006", "张六");
        map.put("0007", "张七");
        map.put("0008", "张八");
        map.put("0009", "张九");
        map.put("0010", "张十");


//        for (Device device : deviceList) {
//            map.put(device.getDevId(), device.getDevName());
//        }
        String jsonstr = JsonUtil.objToJson(map);

//        System.out.println(jsonstr);

        MyResponse myResponse = MyResponse.builder()
                .statusCode("101")
                .data(jsonstr)
                .build();

//        System.out.println(JsonUtil.objToJson(myResponse));

        TreeMap<String, String> idAndNameMap = JsonUtil.jsonToBean(jsonstr, new TypeToken<TreeMap<String, String>>() {
        }.getType());
        System.out.println(idAndNameMap.size());
//        for (Map.Entry<String, String> stringStringEntry : idAndNameMap.entrySet()) {
//            System.out.println(stringStringEntry.getKey() + " " + stringStringEntry.getValue());
//        }
    }
}