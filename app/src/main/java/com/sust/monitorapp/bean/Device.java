package com.sust.monitorapp.bean;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * Created by yhl on 2020/2/26.
 */

@Data
@Builder
public class Device implements Serializable {

    //设备id（服务端生成，唯一）
    String devId;

    //MAC地址
    String devMac;

    //操作者
    String owner;

    //备注
    String note;

    //当前油面温度
    Float nowYoumianTem;

    //位置信息: 基站号
    //例：mcc=460 mnc=1 lac=4301 ci=20986
    String lac;
    String cellid;

    //纬度
    private double lat;

    //经度
    private double lon;

    //详细地址
    String address;
}
