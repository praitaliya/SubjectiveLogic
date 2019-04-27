package com.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Disease")
public class DiseaseModel {
	private int id;
	private int callID;
	private String primaryDisease;
	private String SecondaryDisease;
	private int age;
	private String ageGroup;
	private String gender;
	private int flag;

	@Id
	@Column(name = "ID")
	@GenericGenerator(name = "increment", strategy = "increment")
	@GeneratedValue(generator = "increment")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "PrimaryDisease")
	public String getPrimaryDisease() {
		return primaryDisease;
	}

	public void setPrimaryDisease(String primaryDisease) {
		this.primaryDisease = primaryDisease;
	}

	@Column(name = "SecondaryDisease")
	public String getSecondaryDisease() {
		return SecondaryDisease;
	}

	public void setSecondaryDisease(String secondaryDisease) {
		SecondaryDisease = secondaryDisease;
	}

	@Column(name = "Age")
	@ColumnDefault("0")
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Column(name = "Gender")
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = "Flag")
	@ColumnDefault("1")
	public int isFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	@Column(name="CallID")
	public int getCallID() {
		return callID;
	}

	public void setCallID(int callID) {
		this.callID = callID;
	}
	@Column(name="AgeGroup")
	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}
}
