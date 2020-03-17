package com.wxsoft.telereciver.event;

public class UpdatePrescriptionConStatusEvent {
    public String id;
    public String status;

    public UpdatePrescriptionConStatusEvent(String id, String status){
        this.id=id;
        this.status=status;
    }
}
