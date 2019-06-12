package com.wxsoft.teleconsultation.entity;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SystemMessage {

    private static final String MSG_TYPE_SYSTEM = "213-0001";
    private static final String MSG_TYPE_CLINIC = "213-0002";

    private String id;
    private String title;
    private String content;
    private String noticeType;
    private String noticeTypeName;
    private String senderId;
    private String senderName;
    private String receiveId;
    private String receiveName;
    private String createdDate;
    private String consultationId;
    private String extendFiled;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public String getNoticeTypeName() {
        return noticeTypeName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public boolean isSystem() {
        return MSG_TYPE_SYSTEM.equals(noticeType);
    }

    public boolean isClinic() {
        return MSG_TYPE_CLINIC.equals(noticeType);
    }

    public ExtendFiled getExtendFiled() {
        if (TextUtils.isEmpty(extendFiled)) {
            return null;
        }
        return new Gson().fromJson(extendFiled, ExtendFiled.class);
    }

    public static class ExtendFiled implements Serializable {

        public static final String MSG_TYPE_CLINIC = "consultation";
        public static final String MSG_TYPE_SYSTEM = "SystemNotification";

        @SerializedName("MsgType")
        private String msgType;

        @SerializedName("Object")
        private Object object;

        public String getMsgType() {
            return msgType;
        }

        public Object getObject() {
            return object;
        }
    }
}
