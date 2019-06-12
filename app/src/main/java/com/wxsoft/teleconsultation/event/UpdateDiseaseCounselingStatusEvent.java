package com.wxsoft.teleconsultation.event;

public class UpdateDiseaseCounselingStatusEvent {
    public String id;
    public String status;

    public UpdateDiseaseCounselingStatusEvent(String id,String status){
        this.id=id;
        this.status=status;
    }
}
