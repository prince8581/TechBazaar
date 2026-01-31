package com.app.TechBazaar.API;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.UserRepository;

@Component
public class UserDeleteSchedularService {

	@Autowired
	private UserRepository userRepo;
	
	@Scheduled(fixedRate = 120000)
	public void deleteUnverifiedUsers() {
		//System.err.println("Method run");
		List<Users> users = userRepo.findAll();
		for(Users user:users) {
			if(!user.isVerified() && user.getExpiryTime().isBefore(LocalDateTime.now())) {
				
				userRepo.delete(user);
				  
			}
		
		}
	}
}
