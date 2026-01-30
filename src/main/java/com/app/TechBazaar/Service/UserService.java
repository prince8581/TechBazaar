package com.app.TechBazaar.Service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.API.SendEmailService;
import com.app.TechBazaar.DTO.UserDTO;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Model.Users.LoginStatus;
import com.app.TechBazaar.Model.Users.UserRole;
import com.app.TechBazaar.Model.Users.UserStatus;
import com.app.TechBazaar.Repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private SendEmailService emailService;
	
	public String generateOTP() {
		return String.valueOf(100000 + new Random().nextInt(900000));
	}
	
	public void saveUserBuyer(UserDTO dto) {
		Users buyer = new Users();
		
		//Data from Dto
		buyer.setName(dto.getName());
		buyer.setContactNo(dto.getContactNo());
		buyer.setEmail(dto.getEmail());
		buyer.setPassword(dto.getPassword());
		buyer.setGender(dto.getGender());
		
		//Manual Data
		buyer.setLoginStatus(LoginStatus.INACTIVE);
		buyer.setUserRole(UserRole.BUYER);
		buyer.setUserStatus(UserStatus.UNBLOCKED);
		buyer.setRegDate(LocalDateTime.now());
		
		//OTP Verification  & Authentication
		String otp = generateOTP();
		buyer.setOtp(otp);
		buyer.setExpiryTime(LocalDateTime.now().plusMinutes(5));
		buyer.setVerified(false);
		
		userRepo.save(buyer);
		emailService.sendRegistrationOTP(buyer, otp);
		System.err.println(otp+ " OTP for email  "+buyer.getEmail());
	}
	
	public boolean verifyOTP(String email, String otp) throws Exception {
		
		Users user = userRepo.findByEmail(email);
		
		if(user.getOtp().equals(otp) && LocalDateTime.now().isBefore(user.getExpiryTime())) {
			user.setVerified(true);
			userRepo.save(user);
			return true;
		}
		return false;
	
	}
}
