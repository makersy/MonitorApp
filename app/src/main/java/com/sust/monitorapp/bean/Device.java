package com.sust.monitorapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yhl on 2020/2/26.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    //设备名
    String devName;

    //设备id（服务端生成，唯一）
    String devId;

    //操作者
    String owner;

    //备注
    String note;

    //当前绕组温度
    Float nowRaozuTem;

    //当前油面温度
    Float nowYoumianTem;
}
