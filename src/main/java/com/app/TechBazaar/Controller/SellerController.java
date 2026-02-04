package com.app.TechBazaar.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Seller")
public class SellerController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/Dashboard")
	public String ShowDashboard() {
		if(session.getAttribute("loggedInSeller") == null) {
			return "redirect:/Login";
		}
		return "Seller/Dashboard";
	}
	
	@GetMapping("/AddProduct")
	public String ShowAddProduct() {
		if(session.getAttribute("loggedInSeller") == null) {
			return "redirect:/Login";
		}
		return "Seller/AddProduct";
	}
	
	@GetMapping("/ManageProduct")
	public String ShowManageProduct() {
		return "Seller/ManageProduct";
	}
	
	@GetMapping("/ManageOrders")
	public String ShowManageOrders() {
		return "Seller/ManageOrders";
	}
	
	@GetMapping("/UserProfile")
	public String ShowUserProfile() {
		return "Seller/UserProfile";
	}
	
	@GetMapping("/EditProfile")
	public String ShowEditProfile() {
		return "Seller/EditProfile";
	}
	
	@GetMapping("/ChangePassword")
	public String ShowChangePassword() {
		
		//attributes.addFlashAttribute("msg", "Session Expired..");
		if(session.getAttribute("loggedInSeller") == null) {
			return "redirect:/Login";
		}
		return "Seller/ChangePassword";
	}
	
	
	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {
		try {
			String newPassword = request.getParameter("newPass");
			String oldPassword = request.getParameter("oldPass");
			String confirmPassword = request.getParameter("confirmPass");
			
			Users seller = (Users) session.getAttribute("loggedInSeller");
			userService.changePassword(seller, oldPassword, newPassword, confirmPassword);
			attributes.addFlashAttribute("msg", "Password Successfully Changed");
			return "redirect:/Login";
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/Seller/ChangePassword";
		}
	}
	
	
	//Logout
		@GetMapping("/logout")
		public String Logout() {
			
			session.removeAttribute("loggedInSeller");
			return "redirect:/Login";
		}

}
