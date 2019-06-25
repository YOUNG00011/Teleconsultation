package com.wxsoft.teleconsultation.entity;

import com.wxsoft.teleconsultation.entity.responsedata.LoginResp;

import java.util.List;

public class WeChatAccount extends Entity {

    public String openId;
    public String nickname;
    public int sex;
    public String province;
    public String city;
    public String country;
    public String headimgurl;
    public List<String> privilege;
    public String unionid;
    public String phone;
    public String name;
    //public AssociatedPatient associatedPatients;
    public LoginResp.JMessagAccount jMessagAccount;
}
