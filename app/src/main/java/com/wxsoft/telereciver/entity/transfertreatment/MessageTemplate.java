package com.wxsoft.telereciver.entity.transfertreatment;

import com.wxsoft.telereciver.entity.Doctor;

import java.io.Serializable;

public class MessageTemplate implements Serializable {

    public String id;
    public String name;
    public String messageType;
    public String messageTypeName;
    public String doctorId;
    public String messageNote;
    public String createDate;
    public Doctor doctorInfo;
}
