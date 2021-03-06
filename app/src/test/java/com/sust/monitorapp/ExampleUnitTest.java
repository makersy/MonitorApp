package com.sust.monitorapp;

import android.widget.ScrollView;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.sust.monitorapp.bean.Device;
import com.sust.monitorapp.bean.MyResponse;
import com.sust.monitorapp.bean.User;
import com.sust.monitorapp.common.Constants;
import com.sust.monitorapp.util.DateUtil;
import com.sust.monitorapp.util.JsonUtil;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
     * 生成日期和温度数据写入文件保存，用来模拟填充表格。测试采用易文档mock
     */
    @Test
    public void lineChartData() throws ParseException, IOException {

        try {
            FileInputStream fis = null;
            fis = new FileInputStream("D:\\Code\\android\\MonitorApp\\app\\src\\test\\java\\com\\sust\\monitorapp\\a.txt");
            scanner = new Scanner(new InputStreamReader(fis, "utf-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMddHHmm");
        int day = 20200310;

        List<String> dates = new ArrayList<>();
        Date date = sf1.parse("202003100000");
        dates.add(sf1.format(date));

        //5分钟为间隔生成3天的时间字符串
        for (int i = 0; i < 864; i++) {
            date = new Date(date.getTime() + 300 * 1000);
            dates.add(sf1.format(date));
//            System.out.println(dates.get(dates.size()-1));
        }

        Map<String, Float> lineData = new LinkedHashMap<>();
        Random random = new Random();

        int index = 0;
        for (int i = 0; i < dates.size(); i++) {
            //25-35随机数
            int value = random.nextInt(11) + 25;

            index++;
            lineData.put(dates.get(i), (float) value);
        }

//        System.out.println();
        MyResponse myResponse = MyResponse.builder()
                .statusCode("101")
                .data(JsonUtil.objToJson(lineData))
                .build();
        String json = JsonUtil.objToJson(myResponse);
//        System.out.println();

        //写入文件
        File file = new File("D:\\Code\\android\\MonitorApp\\app\\src\\test\\java\\com\\sust\\monitorapp\\b.txt");
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);

        try {
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            fos.close();
        }

    }

    @Test
    public void jsonFactory() {
        //实体类转json字符串
        Device device = Device.builder()
                .devId("001")
                .devMac("00:01:6C:06:A6:29")
                .owner("张一")
                .note("这是一个备注")
                .nowYoumianTem(60.2f)
                .lac("4301")
                .cellid("20986")
                .build();

        Device device1 = Device.builder()
                .devId("001")
                .devMac("00:01:6C:06:A6:28")
                .owner("张二")
                .note("这是一个备注")
                .nowYoumianTem(60.2f)
                .lac("4303")
                .cellid("20984")
                .build();

        Device device2 = Device.builder()
                .devId("001")
                .devMac("00:01:6C:06:A6:23")
                .owner("张三")
                .note("这是一个备注")
                .nowYoumianTem(60.2f)
                .lac("4213")
                .cellid("20980")
                .build();

        Device device3 = Device.builder()
                .devId("1")
                .devMac("00:01:6C:06:A6:23")
                .owner("15511111111")
                .note("这是一个备注")
                .lac("4301")
                .cellid("20986")
                .build();
        ArrayList<Device> devices = new ArrayList<>();
        devices.add(device);
        devices.add(device1);
        devices.add(device2);

        User user = User.builder()
                .email("afda@163.com")
                .userId("13211111111")
                .authority(Constants.ADMIN)
                .sex("男")
                .build();
        MyResponse response = MyResponse.builder()
                .statusCode("101").data(JsonUtil.objToJson(device3))
                .build();
        // {"statusCode":"101","data":"{\"devName\":\"sust01\",\"owner\":\"张三\"}"}
        /*
         {"statusCode":"101","data":"{\"username\":\"张三\",\"userId\":\"0001\",\"authority\":\"管理员\",\"sex\":\"男\",\"email\":\"afda@163.com\"}"}
         */
        String json = "{\n" +
                "\t\"statusCode\": \"101\",\n" +
                "\t\"data\": \"[20.1, 20.2]\"\n" +
                "}";
//        MyResponse response1 = JsonUtil.jsonToBean(json, MyResponse.class);
//        System.out.println(JsonUtil.jsonToBean(json, MyResponse.class).toString());
//        float[] strs = JsonUtil.jsonToBean(response1.getData(), float[].class);
//        System.out.println(strs.length + " " + strs[0] + " " + strs[1]);
        // {"devName":"sust01","devId":"001","owner":"张三","note":"这是一个备注"}
//        System.out.println(JsonUtil.objToJson(devices));

        System.out.println(JsonUtil.objToJson(response));

    }

    @Test
    public void addition_isCorrect() {

        User user = User.builder()
                .authority(Constants.ADMIN).email("abcde@163.com")
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
        deviceList.add(Device.builder().devId("1").owner("15200000001").note("备注1").build());
        deviceList.add(Device.builder().devId("2").owner("15200000002").note("备注2").build());
        deviceList.add(Device.builder().devId("3").owner("15200000003").note("备注3").build());
        deviceList.add(Device.builder().devId("4").owner("15200000004").note("备注4").build());



//        for (Device device : deviceList) {
//            map.put(device.getDevId(), device.getDevName());
//        }

//        System.out.println(jsonstr);


        String record0 = "2020-05-18 16:36:18 用户:18736606416登录";
        String record1 = "2020-05-18 16:36:18 用户:18736606417登录";
        String record2 = "2020-05-18 16:36:18 用户:18736606418登录";
        String record3 = "2020-05-18 16:36:18 用户:18736606419登录";

        ArrayList<String> list = new ArrayList<>();
        list.add(record0);
        list.add(record1);
        list.add(record2);
        list.add(record3);

        MyResponse myResponse = MyResponse.builder()
                .statusCode("101")
                .data(JsonUtil.objToJson(list))
                .build();

        System.out.println(JsonUtil.objToJson(myResponse));

//        for (Map.Entry<String, String> stringStringEntry : idAndNameMap.entrySet()) {
//            System.out.println(stringStringEntry.getKey() + " " + stringStringEntry.getValue());
//        }
    }

    @Test
    public void hex2dec() {
        String hex = "a";
        Integer value = Integer.valueOf(hex, 16);
        System.out.println(value);
    }

    @Test
    public void parseFloat(){
        /*
        Float.parseFloat转空字符串时直接抛出异常，不处理的话app会闪退。
         */
        String f = "";
        float v = Float.parseFloat(StringUtils.defaultIfBlank(f, "0"));
        System.out.println(v == 0f);
    }
}