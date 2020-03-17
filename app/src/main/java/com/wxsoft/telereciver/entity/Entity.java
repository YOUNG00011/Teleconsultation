package com.wxsoft.telereciver.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * 被创建实体基类
 */
public abstract class Entity  implements Serializable{

    public String id;

    public String createdDate;

    public String updateDate;

    public String creatorId;

    public String creatorName;

    public String updaterId;

    public String updaterName;
}
