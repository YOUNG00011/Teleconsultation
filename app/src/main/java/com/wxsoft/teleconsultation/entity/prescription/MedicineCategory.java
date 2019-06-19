package com.wxsoft.teleconsultation.entity.prescription;

import java.util.List;

public class MedicineCategory {

	public String id;
	public String name;
	public String parentId;
	public String parentName;
	public String category;
	public String medicines;
	public List<MedicineCategory> medicineCategorys;
}
