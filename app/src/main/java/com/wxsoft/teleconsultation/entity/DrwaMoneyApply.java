package com.wxsoft.teleconsultation.entity;

import java.util.List;

public class DrwaMoneyApply extends Entity2 {

    public String doctorId;
    public String doctorName;
    public String bankAccount;
    public String payee;
    public String bankName;
    public String applyDate;
    public String applyAmount;
    public float transferAmount;

    public List<Object> drawMoneyRecords;




    public String status;
    public String statusName;
    public String memo;

    public String modifiedDate;
}
