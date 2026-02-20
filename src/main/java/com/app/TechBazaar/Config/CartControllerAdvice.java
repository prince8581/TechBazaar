package com.app.TechBazaar.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.CartItemRepository;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class CartControllerAdvice {
	
	@Autowired
	private CartItemRepository cartItemRepo;

	@SuppressWarnings("unused")
	@ModelAttribute("cartItemCount")
	public int getCartItemCount(HttpSession session) {
		Users user = (Users) session.getAttribute("loggedInUser");
		
		
		//loggedInUser
		if(user!= null) {
			return  cartItemRepo.countByUser(user);
		}
		
		//Guest User
		if(session!=null) {
			return cartItemRepo.countBySessionId(session.getId());
		}
		return 0;
	}
}
