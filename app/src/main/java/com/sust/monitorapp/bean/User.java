package com.sust.monitorapp.bean;

import com.google.gson.annotations.JsonAdapter;

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
public class User {

    //姓名
    String username;

    //密码
    String password;

    //编号（服务端生成，唯一）
    String userId;

    //权限（管理员/设备操作员）
    String authority;

    //性别
    String sex;

    //邮箱
    String email;

    //手机
    String tel;
}
