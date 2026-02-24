package com.app.TechBazaar.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.TechBazaar.DTO.EnquiryDTO;
import com.app.TechBazaar.DTO.ProductCategoryDTO;
import com.app.TechBazaar.Model.Enquiry;
import com.app.TechBazaar.Model.Feedback;
import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Orders.OrderStatus;
import com.app.TechBazaar.Model.ProductCategory;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Model.Users.UserRole;
import com.app.TechBazaar.Model.Users.UserStatus;
import com.app.TechBazaar.Repository.EnquiryRepository;
import com.app.TechBazaar.Repository.FeedbackRepository;
import com.app.TechBazaar.Repository.OrderRepository;
import com.app.TechBazaar.Repository.ProductCategoryRepository;
import com.app.TechBazaar.Repository.ProductRepository;
import com.app.TechBazaar.Repository.UserRepository;
import com.app.TechBazaar.Service.EnquiryService;
import com.app.TechBazaar.Service.ProductCategoryService;
import com.app.TechBazaar.Service.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Admin")
public class AdminController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductCategoryRepository categoryRepo; 
	
	@Autowired
	private ProductCategoryService productcategoryService;
	
	@Autowired
	private EnquiryRepository enquiryRepo;
	
	@Autowired
	private EnquiryService enquiryService;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private FeedbackRepository feedbackRepo;
	
	

	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model) {
		
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		
		model.addAttribute("sellerCount", userRepo.countByUserRoleAndUserStatusNot(UserRole.SELLER, UserStatus.DELETED));
		model.addAttribute("buyerCount", userRepo.countByUserRoleAndUserStatusNot(UserRole.BUYER, UserStatus.DELETED));
		model.addAttribute("productCount", productRepo.count());
		model.addAttribute("orderCount", orderRepo.count());
		model.addAttribute("categoryCount", categoryRepo.count());
		model.addAttribute("enquiryCount", enquiryRepo.count());
		model.addAttribute("cancelledOrders", orderRepo.countByOrderStatus(OrderStatus.CANCELLED));
		model.addAttribute("confirmOrders", orderRepo.countByOrderStatus(OrderStatus.CONFIRMED));
		model.addAttribute("deliveredOrders", orderRepo.countByOrderStatus(OrderStatus.DELIVERED));
		
		List<Enquiry> recentEnquiries = enquiryRepo.findTop5ByOrderByEnquiryDateDesc();
		model.addAttribute("recentEnquiries", recentEnquiries);
		
		//Monthly Order stats count for chart
		List<Object[]> stats = orderRepo.getMonthlyOrderStats();
		Map<Integer, Long> monthCountMap = new HashMap<>();
		for(Object[] row: stats) {
			int monthNumber = ((Integer) row[0]).intValue();
			long count = ((Long) row[1]).longValue();
			monthCountMap.put(monthNumber, count);
		}
		
		List<String> orderMonths = new ArrayList<>();
		List<Long> orderCounts = new ArrayList<>();
		
		String monthNames[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		for(int i=1;i<=monthNames.length;i++) {
			orderMonths.add(monthNames[i-1]);
			orderCounts.add(monthCountMap.getOrDefault(i, 0L));
			
		}
		model.addAttribute("orderMonths",orderMonths);
		model.addAttribute("orderCounts", orderCounts);
		
		return "Admin/Dashboard";
	}
	
	@GetMapping("/ManageSellers")
	public String showManageSellers(Model model) {
		
		List<Users> sellers = userRepo.findAllByUserRoleAndIsVerifiedAndUserStatusNot(UserRole.SELLER, true,UserStatus.DELETED);
		model.addAttribute("sellers",sellers);
		
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		
		return "Admin/ManageSellers";
	}
	
	@GetMapping("/ManageUsers")
	public String showManageUsers(Model model) {
		List<Users> users = userRepo.findAllByUserRoleAndIsVerifiedAndUserStatusNot(UserRole.BUYER, true,UserStatus.DELETED);
		model.addAttribute("users",users);
		
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		
		return "Admin/ManageUsers";
	}
	
	@GetMapping("/UpdateUserStatus/{id}")
	public String UpdateUserStatus(@PathVariable long id, RedirectAttributes attributes, HttpServletRequest request) {
		try {
			userService.updateUserStatus(id);
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		String referer = request.getHeader("Referer"); 
		return "redirect:"+referer;
	}
	
	@GetMapping("/DeleteUser/{id}")
	public String DeleteUser(@PathVariable long id, HttpServletRequest request, RedirectAttributes attributes) {
		try {
			userService.deleteUser(id);
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		String referer = request.getHeader("Referer"); 
		return "redirect:"+referer;
	}
	
	@GetMapping("/ViewOrders")
	public String showViewOrders(Model model) {
		
		if(session.getAttribute("loggedInAdmin")==null) {
			return "redirect:/Login";
		}
		List<Orders> orders=orderRepo.findAll();
		model.addAttribute("orders", orders.reversed());
		return "Admin/ViewOrders";
	}
	
	@GetMapping("/AddCategory")
	public String showAddCategory(Model model) {
		List<ProductCategory> categories = categoryRepo.findAll();
		model.addAttribute("categories", categories);
		
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		model.addAttribute("dto", new ProductCategory());
		return "Admin/AddCategory";
	}
	
	@PostMapping("/AddCategory")
	public String AddCategory(@ModelAttribute("dto") ProductCategoryDTO dto, RedirectAttributes attributes) {
		try {
			
			productcategoryService.saveProductCategory(dto);
			attributes.addFlashAttribute("msg", dto.getCategoryName() + " category Successfully Saved!");
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/Admin/AddCategory";
	}
	
	@GetMapping("/CategoryStatus/{id}")
	public String CategoryStatus(@PathVariable long id) {
		productcategoryService.updateCategoryStatus(id);
		return "redirect:/Admin/AddCategory";
	}
	
	@GetMapping("/Feedback")
	public String showFeedback(Model model) {

		if(session.getAttribute("loggedInAdmin") == null) {
		return "redirect:/Login";
		}
		List<Feedback> feedback = feedbackRepo.findAll();
		model.addAttribute("feedback", feedback);
		
		return "Admin/Feedback";
	}
	
	
	@GetMapping("/deleteFeedback/{id}")
	public String deleteFeedback(@PathVariable Long id,
	                             HttpSession session,
	                             RedirectAttributes attributes) {

	    if (session.getAttribute("loggedInAdmin") == null) {
	        return "redirect:/Login";
	    }

	    feedbackRepo.deleteById(id);

	    attributes.addFlashAttribute("success",
	            "Feedback deleted successfully!");

	    return "redirect:/Admin/Feedback";
	}
	
	@GetMapping("/Enquiry")
	public String showEnquiry(Model model) {
		List<Enquiry> enquiry = enquiryRepo.findAll();
		model.addAttribute("enquiry", enquiry);
		
		if(session.getAttribute("loggedInAdmin") == null) {
		return "redirect:/Login";
		}
		model.addAttribute("dto", new Enquiry());
		return "Admin/Enquiry";
	}
	
	@PostMapping("/Enquiry")
	public String Enquiry(@ModelAttribute("dto") EnquiryDTO dto, RedirectAttributes attributes) {
		try {
			
			enquiryService.saveEnquiry(dto);
			attributes.addFlashAttribute("msg", dto.getName() + " Enquiry Successfully Saved!");
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/Admin/AddCategory";
	}
	
	@GetMapping("/UdateProfilePic")
	public String ShowUdateProfilePic() {
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
			}
		return "Admin/UpdateProfilePic";
	}
	
	@PostMapping("/UdateProfilePic")
	public String UdateProfilePic(@RequestParam("profilePic") MultipartFile profilePic, RedirectAttributes attributes) {
		try {
			
			Users admin = (Users) session.getAttribute("loggedInAdmin");
			userService.updateProfilePic(admin, profilePic);
			attributes.addFlashAttribute("msg", "Profile Pic Successfully updated!");
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/Admin/UdateProfilePic";
	}
	
	@GetMapping("/ChangePassword")
	public String ShowChangePassword() {
		
		//attributes.addFlashAttribute("msg", "Session Expired..");
		if(session.getAttribute("loggedInAdmin") == null) {
			return "redirect:/Login";
		}
		return "Admin/ChangePassword";
	}
	
	
	@PostMapping("/ChangePassword")
	public String ChangePassword(HttpServletRequest request, RedirectAttributes attributes) {
		try {
			String newPassword = request.getParameter("newPass");
			String oldPassword = request.getParameter("oldPass");
			String confirmPassword = request.getParameter("confirmPass");
			
			Users admin = (Users) session.getAttribute("loggedInAdmin");
			userService.changePassword(admin, oldPassword, newPassword, confirmPassword);
			attributes.addFlashAttribute("msg", "Password Successfully Changed");
			return "redirect:/Login";
			
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
			return "redirect:/Admin/ChangePassword";
		}
	}
	
	//Logout
//	@GetMapping("/logout")
//	public String Logout() {
//		
//		session.removeAttribute("loggedInAdmin");
//		return "redirect:/Login";
//	}
	
	
}
