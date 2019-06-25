package com.wxsoft.teleconsultation.entity.prescription;

import com.wxsoft.teleconsultation.entity.diseasecounseling.DiseaseCounseling;

import java.io.Serializable;

public class ChatRecord implements Serializable{

    public String id;
    public String doctorId;
    public String doctorName;
    public String weChatAccountId;
    public String weChatNickName;
    public String sendDateTime;
    public String content;
    public String contentType;
    public String contentTypeName;
    public String msgDirectionType;
    public String msgDirectionTypeName;
    public String businessId;
}
