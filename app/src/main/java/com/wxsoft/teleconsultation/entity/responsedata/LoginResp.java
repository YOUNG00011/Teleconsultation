package com.wxsoft.teleconsultation.entity.responsedata;

import com.google.gson.annotations.SerializedName;
import com.wxsoft.teleconsultation.entity.Archive;
import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Education;
import com.wxsoft.teleconsultation.entity.HWAccount;
import com.wxsoft.teleconsultation.entity.User;

import java.util.List;


public class LoginResp {

    private String password;

    private String id;

    private Doctor doctorInfoDTO;

    private Archive archives;

    private List<UserRole> userRole;

    private JMessagAccount jMessagAccount;

    private HWAccount hwAccount;

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctorInfoDTO;
    }

    public Archive getArchives() {
        return archives;
    }

    public List<UserRole> getUserRole() {
        return userRole;
    }

    public LoginResp.JMessagAccount getJMessagAccount() {
        return jMessagAccount;
    }

    public HWAccount getHwAccount() {
        return hwAccount;
    }

    public static class UserRole {

        public static final String ROLE_TRANSLATIONER  = "Translationer";
        public static final String ROLE_DOCTOR  = "Doctor";

        private String id;

        private String userId;

        private String roleId;

        public String getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }

        public String getRoleId() {
            return roleId;
        }
    }

    public static class JMessagAccount {

        private String userId;

        private String userName;

        @SerializedName("j_UserName")
        private String jUserName;

        @SerializedName("j_Nickname")
        private String jNickname;

        @SerializedName("j_Avatar")
        private String jAvatar;

        @SerializedName("j_Password")
        private String jPassword;

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getjUserName() {
            return jUserName;
        }

        public String getjNickname() {
            return jNickname;
        }

        public String getjAvatar() {
            return jAvatar;
        }

        public String getjPassword() {
            return jPassword;
        }
    }
}
