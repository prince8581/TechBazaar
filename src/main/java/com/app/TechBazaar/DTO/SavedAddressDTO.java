package com.app.TechBazaar.DTO;

import jakarta.persistence.Column;

public class SavedAddressDTO {

	private Long id;
	private String name;
	private String contactNo;
	private String pincode;
	private String locality;
	private String address;
	private String cityDistrict;
	private String state;
	private String landmark;
	private String altContactNo;
	private String addressType;
	//private Users user;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCityDistrict() {
		return cityDistrict;
	}
	public void setCityDistrict(String cityDistrict) {
		this.cityDistrict = cityDistrict;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getAltContactNo() {
		return altContactNo;
	}
	public void setAltContactNo(String altContactNo) {
		this.altContactNo = altContactNo;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}
