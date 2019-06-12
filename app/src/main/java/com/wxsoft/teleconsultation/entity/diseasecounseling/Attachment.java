package com.wxsoft.teleconsultation.entity.diseasecounseling;

import com.wxsoft.teleconsultation.entity.Entity;

public class Attachment extends Entity {

    public String fileName;
    public String fileType;
    public int fileSize;
    public String serverUrl;
    public String diseaseCounselingId;
    public String attachmentId;
    public String url;
    public DiseaseCounseling diseaseCounseling;
}
