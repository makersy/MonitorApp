package com.sust.monitorapp.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yhl on 2020/2/16.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyResponse {

    //状态码。根据需要设置。
    String statusCode;

    // json字符串，用来传输数据
    String data;
}
