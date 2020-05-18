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
public class User {

//    //姓名
//    String username;

    //密码
    String password;

    //用户id，这里直接用的手机号
    String userId;

    //权限（1 管理员/0 普通用户）
    int authority;

    //性别
    String sex;

    //邮箱
    String email;

//    //手机
//    String tel;
}
