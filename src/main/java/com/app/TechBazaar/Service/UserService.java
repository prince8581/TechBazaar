package com.app.TechBazaar.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	private final String uploadDir = "public/uploads/";
	
	public String generateOTP() {
		return String.valueOf(100000 + new Random().nextInt(900000));
	}
	
	public void saveUserBuyer(UserDTO dto) {
		try {
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
		}catch(Exception e) {
			System.err.println("Error from Service : "+e.getMessage());
			throw new RuntimeException("Something Went Wrong, please try again later");
			}
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
	
	public void ResendOTP(String email) {
		
		String otp = generateOTP();
		Users user = userRepo.findByEmail(email);
		user.setExpiryTime(LocalDateTime.now().plusMinutes(5));
		user.setOtp(otp);
		//System.err.println("New Resend Otp: "+otp);
		emailService.sendRegistrationOTP(user, otp);
		userRepo.save(user);
		
	}
	
	public void changePassword(Users user, String oldPass, String newPass, String confirmPass) {
		try {
			if(!newPass.equals(confirmPass)) {
				throw new RuntimeException("New Password And Confirm Password are not same");
			}
			if(newPass.equals(user.getPassword())) {
				throw new RuntimeException("New password can't be same as old password");
			}
			if(!oldPass.equals(user.getPassword())) {
				throw new RuntimeException("Invalid Old Password");
			}
			user.setPassword(confirmPass);
			userRepo.save(user);
			
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	public void saveUserSeller(UserDTO dto) throws IOException {
		
		       Users seller = new Users();
		
		        //File Uploading
				String storageFileName =  UUID.randomUUID() +"_"+ dto.getProfilePic().getOriginalFilename();
			
				Path uploadPath = Paths.get(uploadDir);
				
				if(!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				
				try(InputStream inputStream = dto.getProfilePic().getInputStream()){
					Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				
				
		
		    try {
		
			//Data from Dto
			seller.setName(dto.getName());
			seller.setContactNo(dto.getContactNo());
			seller.setEmail(dto.getEmail());
			seller.setPassword(dto.getPassword());
			seller.setGender(dto.getGender());
			seller.setProfilePic(storageFileName);
			seller.setPanCard(dto.getPanCard());
			seller.setAadharNo(dto.getAadharNo());
			seller.setGstNo(dto.getGstNo());
			seller.setAddress(dto.getAddress());
			
			//Manual Data
			seller.setLoginStatus(LoginStatus.INACTIVE);
			seller.setUserRole(UserRole.SELLER);
			seller.setUserStatus(UserStatus.UNBLOCKED);
			seller.setRegDate(LocalDateTime.now());
			
			//OTP Verification  & Authentication
			String otp = generateOTP();
			seller.setOtp(otp);
			seller.setExpiryTime(LocalDateTime.now().plusMinutes(5));
			seller.setVerified(false);
			
			userRepo.save(seller);
			emailService.sendRegistrationOTP(seller, otp);
			System.err.println(otp+ " OTP for email  "+seller.getEmail());
		}catch(Exception e) {
			System.err.println("Error from Service : "+e.getMessage());
			throw new RuntimeException("Something Went Wrong, please try again later");
	    }
	}
	
	public void updateUserStatus(long id) {
		Users user = userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found!"));
		if(user.getUserStatus().equals(UserStatus.BLOCKED)) {
			user.setUserStatus(UserStatus.UNBLOCKED);
			//Update product
		}
		else if(user.getUserStatus().equals(UserStatus.UNBLOCKED)) {
			user.setUserStatus(UserStatus.BLOCKED);
			//update product
			
		}
		userRepo.save(user);
	}
	
	public void deleteUser(long id) {
		Users user = userRepo.findById(id).orElseThrow(()-> new RuntimeException("User not found!"));
		if(user.getUserRole().equals(UserRole.SELLER)) {
			user.setUserStatus(UserStatus.DELETED);
			//Deactivate product also
		}
		else {
			user.setUserStatus(UserStatus.DELETED);
		}
		userRepo.save(user);
	}
	
	public void updateProfilePic(Users user, MultipartFile profilePic) throws IOException {
		String storageFileName = UUID.randomUUID()+"_"+profilePic.getOriginalFilename();
		String uploadDir = "public/ProfilePic/";
		Path uploadPath= Paths.get(uploadDir);
		
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		if(Paths.get(uploadDir+user.getProfilePic()) != null) {
			Files.deleteIfExists(Paths.get(uploadDir+user.getProfilePic()));
			
		}
		Files.copy(profilePic.getInputStream(), Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
		
		user.setProfilePic(storageFileName);
         userRepo.save(user);		
	}
	
	//Update Seller Edit Profile
	public void updateEditProfile(Users seller, MultipartFile profileImage) throws IOException  {
		Users existUsers = userRepo.findById(seller.getId()).orElseThrow();
		
		//Delete Existing profilepic from source
		if(existUsers.getProfilePic() != null && !existUsers.getProfilePic().isEmpty()) {
			Path filePath = Paths.get(uploadDir+existUsers.getProfilePic());
			Files.deleteIfExists(filePath);
		}
		
		String storageFileName = UUID.randomUUID()+"_"+profileImage.getOriginalFilename();
		Files.copy(profileImage.getInputStream(), Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
		
		existUsers.setName(seller.getName());
		existUsers.setContactNo(seller.getContactNo());
		existUsers.setPanCard(seller.getPanCard());
		existUsers.setAadharNo(seller.getAadharNo());
		existUsers.setGstNo(seller.getGstNo());
		existUsers.setAddress(seller.getAddress());
		existUsers.setProfilePic(storageFileName);
		
		userRepo.save(existUsers);
		
	}
	
}
