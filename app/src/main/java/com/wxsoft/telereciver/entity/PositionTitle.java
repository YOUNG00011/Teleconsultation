package com.wxsoft.telereciver.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PositionTitle implements Serializable{

    public static final String ID_NO_LIMIT = "200-0000";

    @SerializedName("positionTitle")
    private String positionTitleEnum;

    private String positionTitleName;

    public static List<PositionTitle> getPositionTitles(List<CommEnum> commEnums) {
        List<PositionTitle> positionTitles = new ArrayList<>();
        for (CommEnum commEnum : commEnums) {
            PositionTitle positionTitle = new PositionTitle(commEnum.getId(), commEnum.getItemName());
            positionTitles.add(positionTitle);
        }

        return positionTitles;
    }

    public static PositionTitle getNoLimitPositionTitle() {
        return new PositionTitle(ID_NO_LIMIT, "不限");
    }

    public boolean isNoLimit() {
        return positionTitleEnum.equals(ID_NO_LIMIT);
    }


    public PositionTitle(String positionTitleEnum, String positionTitleName) {
        this.positionTitleEnum = positionTitleEnum;
        this.positionTitleName = positionTitleName;
    }

    public String getPositionTitleEnum() {
        return positionTitleEnum;
    }

    public void setPositionTitleEnum(String positionTitleEnum) {
        this.positionTitleEnum = positionTitleEnum;
    }

    public String getPositionTitleName() {
        return positionTitleName;
    }

    public void setPositionTitleName(String positionTitleName) {
        this.positionTitleName = positionTitleName;
    }

//    private String id;
//    private String memo;
//    private String itemName;
//    private String enumDict;
//    private String enumDictId;
//    private boolean isEnable;
//
//    public static PositionTitle getNoLimitPositionTitle() {
//        PositionTitle positionTitle = new PositionTitle();
//        positionTitle.id = ID_NO_LIMIT;
//        positionTitle.memo = "不限";
//        return positionTitle;
//    }
//

//
//    public String getId() {
//        return id;
//    }
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public String getItemName() {
//        return itemName;
//    }
//
//    public String getEnumDict() {
//        return enumDict;
//    }
//
//    public String getEnumDictId() {
//        return enumDictId;
//    }
//
//    public boolean isEnable() {
//        return isEnable;
//    }

    @Override
    public boolean equals(Object obj) {
        // 如果为同一对象的不同引用,则相同
        if (this == obj) {
            return true;
        }
        // 如果传入的对象为空,则返回false
        if (obj == null) {
            return false;
        }

        // 如果两者属于不同的类型,不能相等
        if (getClass() != obj.getClass()) {
            return false;
        }

        // 类型相同, 比较内容是否相同
        PositionTitle positionTitle = (PositionTitle) obj;

        return positionTitleEnum.equals(positionTitle.getPositionTitleEnum());
    }
}
