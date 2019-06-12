package com.wxsoft.teleconsultation.entity;

import java.io.Serializable;
import java.util.List;

public class City implements Serializable {

    private String id;
    private String name;
    private List<City> cityList;

    public City() {
    }

    public City(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<City> getCityList() {
        return cityList;
    }
}
