package com.wxsoft.telereciver.entity.requestbody;

public class QueryDoctorInfoBody {

    private String provinceId;
    private String cityId;
    private String districtId;
    private String name;
    private String goodAt;
    private String positionTitle;
    private String selfHospitalId;
    public String businessType  ;

    public QueryDoctorInfoBody() {
    }

    public QueryDoctorInfoBody(String provinceId, String cityId, String districtId, String name, String goodAt, String positionTitle, String selfHospitalId) {
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.districtId = districtId;
        this.name = name;
        this.goodAt = goodAt;
        this.positionTitle = positionTitle;
        this.selfHospitalId = selfHospitalId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoodAt() {
        return goodAt;
    }

    public void setGoodAt(String goodAt) {
        this.goodAt = goodAt;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getSelfHospitalId() {
        return selfHospitalId;
    }

    public void setSelfHospitalId(String selfHospitalId) {
        this.selfHospitalId = selfHospitalId;
    }
}
