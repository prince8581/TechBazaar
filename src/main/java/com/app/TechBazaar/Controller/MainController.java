package com.app.TechBazaar.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.TechBazaar.DTO.EnquiryDTO;
import com.app.TechBazaar.DTO.UserDTO;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Model.Users.UserRole;
import com.app.TechBazaar.Model.Users.UserStatus;
import com.app.TechBazaar.Repository.EnquiryRepository;
import com.app.TechBazaar.Repository.UserRepository;
import com.app.TechBazaar.Service.EnquiryService;
import com.app.TechBazaar.Service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private EnquiryRepository enquiryRepo;
	
	@Autowired
	private EnquiryService enquiryService;

	
	@GetMapping("/")
	public String ShowIndex() {
		return "index";
	}
	
	@GetMapping("/Orders")
	public String ShowOrders() {
		return "Orders";
	}
	
	@GetMapping("/Products")
	public String ShowProducts() {
		return "/Products";
	}
	
	@GetMapping("/Services")
	public String ShowServices() {
		return "/Services";
	}
	
	@GetMapping("/AboutUs")
	public String ShowAboutUs() {
		return "/AboutUs";
	}
	
	@GetMapping("/ContactUs")
	public String ShowContactUs(Model model) {
		model.addAttribute("dto", new EnquiryDTO());
		return "/ContactUs";
	}
	
	@PostMapping("/ContactUs")
	public String ContactUs(@ModelAttribute("dto") EnquiryDTO dto, RedirectAttributes attributes, HttpSession session) {
		
		enquiryService.saveEnquiry(dto);
		//attributes.addFlashAttribute("msg", "Your Enquiry has been Successfully Saved!");
		return "redirect:/ContactUs";
	}
	
	
	@GetMapping("/Login")
	public String ShowLogin(Model model) {
		
		model.addAttribute("dto", new UserDTO());
		return "/Login";
	}
	
	@PostMapping("/Login")
	public String UserLogin(@ModelAttribute("dto") UserDTO dto, RedirectAttributes attributes, HttpSession session) {
		try {
			if(!userRepo.existsByEmailAndIsVerified(dto.getEmail(),true)) {
				attributes.addFlashAttribute("msg", "User not found!");
				return "redirect:/Login";
			}
			
			Users user = userRepo.findByEmail(dto.getEmail());
			if(!user.getPassword().equals(dto.getPassword())) {
				attributes.addFlashAttribute("msg", "Invalid User and Password");
				return "redirect:/Login";
			}
			
			if(user.getUserStatus().equals(UserStatus.UNBLOCKED)) {
				if(user.getUserRole().equals(UserRole.ADMIN)) {
					session.setAttribute("loggedInAdmin", user);
					return "redirect:/Admin/Dashboard";
				}
				else if(user.getUserRole().equals(UserRole.SELLER)) {
					session.setAttribute("loggedInSeller", user);
					return "redirect:/Seller/Dashboard";
				}
				else if(user.getUserRole().equals(UserRole.BUYER)) {
					session.setAttribute("loggedInBuyer", user);
					return "redirect:/User/Dashboard";
				}
			}
			else {
				attributes.addFlashAttribute("msg","Login Disabled, Please Contact Administration!");
			}
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/Login";
	}
	
	@GetMapping("/Register")
	public String ShowRegister(Model model) {
		
		model.addAttribute("dto",new  UserDTO());
		return "/Register";
	}
	
	@PostMapping("/Register")
	public String Register(@ModelAttribute("dto") UserDTO dto, HttpSession session, RedirectAttributes attributes) {
		
		try {
			if(userRepo.existsByEmailAndIsVerified(dto.getEmail(), true)) {
				attributes.addFlashAttribute("msg", "User Already Exists");
				return "redirect:/Register";
			}
			userService.saveUserBuyer(dto);
			session.setAttribute("email", dto.getEmail());
			return "redirect:/verify-otp";
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/Register";
		}
	}
	
	@GetMapping("/verify-otp")
	public String ShowVerifyOTP(HttpSession session) {
		if(session.getAttribute("email") == null) {
			return "redirect:/Register";
		}
		return "VerifyOTP";
	}
	
	
	@PostMapping("/verify-otp")
	public String VerifyRegisterOTP(@RequestParam("otp") String otp, HttpSession session, RedirectAttributes attributes) {
		
		try {
			String email = (String) session.getAttribute("email");
			if(!userService.verifyOTP(email, otp)) {
			attributes.addFlashAttribute("msg", "Invalid or Expired OTP");
			return "redirect:/verify-otp";
			}
			session.removeAttribute("email");
			//attributes.addFlashAttribute("msg", "OTP Verification Successful, Registration Completed");
			return "redirect:/Login";
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/verify-otp";
		}
	}
	
	@GetMapping("/ResendOTP")
	public String ResendOTP(HttpSession session, RedirectAttributes attributes) {
		
		try {
			String email = (String) session.getAttribute("email");
			userService.ResendOTP(email);
			
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/verify-otp";
	}
	
	@GetMapping("BecomeSeller")
	public String ShowSellerRegister(Model model){
		model.addAttribute("dto", new UserDTO());
		return "/BecomeSeller";
		
	}
	
	@PostMapping("/BecomeSeller")
	public String SellerRegister(@ModelAttribute("dto") UserDTO dto, HttpSession session, RedirectAttributes attributes) {
		
		try {
			if(userRepo.existsByEmailAndIsVerified(dto.getEmail(), true)) {
				attributes.addFlashAttribute("msg", "User Already Exists");
				return "redirect:/BecomeSeller";
			}
			userService.saveUserSeller(dto);
			session.setAttribute("email", dto.getEmail());
			return "redirect:/verify-otp";
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/BecomeSeller";
		}
	}

}
