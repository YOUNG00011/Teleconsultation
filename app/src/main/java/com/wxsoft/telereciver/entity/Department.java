package com.wxsoft.telereciver.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Department implements Serializable {

    private String id;
    private String name;
    private String organizationId;
    @SerializedName("PY")
    private String PY;
    @SerializedName("wb")
    private String WB;

    public static List<Department> getDepartments() {
        List<Department> departments = new ArrayList<>();
        Department department1 = new Department();
        department1.id = "6666";
        department1.name = "神经科";
        department1.organizationId = "11111111";
        departments.add(department1);

        Department department2 = new Department();
        department2.id = "77777";
        department2.name = "妇产科";
        department2.organizationId = "11111111";
        departments.add(department2);

        Department department3 = new Department();
        department3.id = "88888";
        department3.name = "骨科";
        department3.organizationId = "222222222";
        departments.add(department3);
        return departments;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getPY() {
        return PY;
    }

    public String getWB() {
        return WB;
    }

    @Override
    public boolean equals(Object obj) {
        // 如果为同一对象的不同引用,则相同
        if (this == obj) {
            return true;
        }
        // 如果传入的对象为空,则返回false
        if (obj == null) {
            return false;
        }

        // 如果两者属于不同的类型,不能相等
        if (getClass() != obj.getClass()) {
            return false;
        }

        // 类型相同, 比较内容是否相同
        Department department = (Department) obj;

        return id.equals(department.id);
    }
}
