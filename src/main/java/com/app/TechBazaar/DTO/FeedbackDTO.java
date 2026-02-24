package com.app.TechBazaar.DTO;

import org.springframework.web.multipart.MultipartFile;

public class FeedbackDTO {
	
	private int rating;
	private String message;
	private MultipartFile profilePic;
	
	
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public MultipartFile getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(MultipartFile profilePic) {
		this.profilePic = profilePic;
	}
	
    
	
    

}