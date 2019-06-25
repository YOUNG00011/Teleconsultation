package com.wxsoft.teleconsultation.entity;

import java.io.Serializable;

public class CommEnum implements Serializable {

    private String id;
    private String memo;
    private String itemName;
    private String enumDict;
    private String enumDictId;
    private boolean isEnable;

    public CommEnum(){}
    public CommEnum(String enumDict,String itemName){
        this.enumDict=enumDict;
        this.itemName=itemName;
    }
    public String getId() {
        return id;
    }

    public String getMemo() {
        return memo;
    }

    public String getItemName() {
        return itemName;
    }

    public String getEnumDict() {
        return enumDict;
    }

    public String getEnumDictId() {
        return enumDictId;
    }

    public boolean isEnable() {
        return isEnable;
    }
}
