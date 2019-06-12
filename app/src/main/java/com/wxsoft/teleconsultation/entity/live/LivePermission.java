package com.wxsoft.teleconsultation.entity.live;

import com.wxsoft.teleconsultation.entity.Entity2;

public class LivePermission extends Entity2 {

    /// <summary>
    /// 医院Id
    /// </summary>
    public String hospitalId;

    /// <summary>
    /// 医院名称
    /// </summary>
    public String hospitalName;

    /// <summary>
    /// 医生Id
    /// </summary>
    public String doctorId;

    /// <summary>
    /// 医生名称
    /// </summary>
    public String doctorName;

    /// <summary>
    /// 申请时间
    /// </summary>
    public String applyDate;

    /// <summary>
    /// 申请描述
    /// </summary>
    public String description;

    /// <summary>
    /// 审核人
    /// </summary>
    public String approver;

    /// <summary>
    /// 状态
    /// </summary>
    public String status;

    public String statusName;
}
