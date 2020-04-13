package com.sust.monitorapp.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by yhl on 2020/4/11.
 *
 * 查询全国移动联通电信2G/3G/4G基站位置数据，接口返回值
 *
 * 继承序列化接口可以用 intent在 activity间传递 对象
 */

@Data
public class Location implements Serializable {

    /**
     * 0: 成功
     * 10000: 参数错误
     * 10001: 无查询结果
     */
    private int errcode;

    /**
     * 纬度
     */
    private double lat;

    /**
     * 经度
     */
    private double lon;

    /**
     * 精度半径
     */
    private int radius;

    /**
     * 地址
     */
    private String address;
}
