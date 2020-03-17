package com.wxsoft.telereciver.entity.requestbody;

import java.util.ArrayList;
import java.util.List;

public class ClinicAddDoctorRequestBody {

    private String consultationId;

    private List<String> doctors;

    public ClinicAddDoctorRequestBody(String consultationId, String doctId) {
        this.consultationId = consultationId;
        this.doctors = new ArrayList<>();
        this.doctors.add(doctId);
    }
}
