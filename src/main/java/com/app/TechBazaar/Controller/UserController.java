package com.app.TechBazaar.Controller;

import java.util.List;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.TechBazaar.DTO.SavedAddressDTO;
import com.app.TechBazaar.Model.SavedAddress;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.SavedAddressRepository;
import com.app.TechBazaar.Service.SavedAddressService;
import com.app.TechBazaar.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/User")
public class UserController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SavedAddressRepository addressRepo;
	
	@Autowired
	private SavedAddressService addressService;
	
	
	
	@GetMapping("/")
	public String showDashboard() {
		
		if(session.getAttribute("loggedInUser") == null) {
			return "redirect:/Login";
		}
		return "index";
	}
	
	@GetMapping("/Profile")
	public String ShowProfile() {
		if(session.getAttribute("loggedInUser") == null) {
			return "redirect:/Login";
		}
		return "User/Profile";
	}
	
	@GetMapping("/ManageAddress")
	public String ShowManageAddress(Model model) {
	
		if(session.getAttribute("loggedInUser") == null) {
			return "redirect:/Login";
		}
		List<SavedAddress> savedAddress  =  addressRepo.findAll();
		model.addAttribute("savedAddress", savedAddress);
		
		SavedAddressDTO dto = new SavedAddressDTO();
		model.addAttribute("dto", dto);
		
		return "User/ManageAddress";
		
		
		
	}
	
	@PostMapping("/ManageAddress")
	public String ManageAddress(@ModelAttribute("dto") SavedAddressDTO dto, RedirectAttributes attributes) {
		
		try {
			if(session.getAttribute("loggedInUser") == null) {
				return "redirect:/Login";
			}
			
			Users user = (Users) session.getAttribute("loggedInUser");
			addressService.saveNewAddress(dto, user);
			
			return "redirect:/User/ManageAddress";
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/User/ManageAddress";
	}
	
	@GetMapping("/ChangePassword")
	public String ShowChangePassword() {
		if(session.getAttribute("loggedInUser") == null) {
			return "redirect:/Login";
		}
		return "User/ChangePassword";
	}
	
	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {
		try {
			String newPassword = request.getParameter("newPass");
			String oldPassword = request.getParameter("oldPass");
			String confirmPassword = request.getParameter("confirmPass");
			
			Users user = (Users) session.getAttribute("loggedInUser");
			userService.changePassword(user, oldPassword, newPassword, confirmPassword);
			attributes.addFlashAttribute("msg", "Password Successfully Changed");
			return "redirect:/Login";
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/User/ChangePassword";
		}
	}
		
	//Logout
//	@GetMapping("/logout")
//	public String Logout() {
//		
//		session.invalidate();                //-> browser ke ssession ko khatam kar deta hai
//		//session.removeAttribute("loggedInUser");
//		return "redirect:/Login";
//	}
	
	
	
	
	
	
	
	

}
