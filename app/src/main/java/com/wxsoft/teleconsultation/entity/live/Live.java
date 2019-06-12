package com.wxsoft.teleconsultation.entity.live;

import com.wxsoft.teleconsultation.entity.Doctor;
import com.wxsoft.teleconsultation.entity.Entity2;

import java.util.List;

public class Live extends Entity2 {

    public String doctorId;
    public String doctorName;
    public String liveTitle;
    public String description;
    public String publishedDate;
    public String liveDate;
    public float price;
    public float minutes;
    public boolean isNeedBroadcast;
    public String status;
    public String statusName;
    public String conferenceId;
    public String frameURL;
    public String liveURL;
    public String cancelDate;
    public String cancelReason;
    public String startDate;
    public String endDate;
    public String attachmentId;
    public String url;
    public Doctor doctorInfo;
    public List<Object> livePaymentRecords;
}
