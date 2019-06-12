package com.wxsoft.teleconsultation.entity;

import com.google.gson.annotations.SerializedName;

public class HWAccount {

    private String id;

    @SerializedName("hW_UserName")
    private String hwUserName;

    @SerializedName("hW_Nickname")
    private String hwNickname;

    @SerializedName("hW_Password")
    private String hwPassword;

    public String getId() {
        return id;
    }

    public String getHwUserName() {
        return hwUserName;
    }

    public String getHwNickname() {
        return hwNickname;
    }

    public String getHwPassword() {
        return hwPassword;
    }
}
