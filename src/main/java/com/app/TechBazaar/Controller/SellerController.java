package com.app.TechBazaar.Controller;

import java.util.List;

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

import com.app.TechBazaar.DTO.ProductCategoryDTO;
import com.app.TechBazaar.DTO.ProductDTO;
import com.app.TechBazaar.Model.ProductCategory;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.ProductCategoryRepository;
import com.app.TechBazaar.Repository.ProductRepository;
import com.app.TechBazaar.Repository.UserRepository;
import com.app.TechBazaar.Service.ProductService;
import com.app.TechBazaar.Service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Seller")
public class SellerController {
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductCategoryRepository productCategoryRepo;
	
	@Autowired
	private ProductRepository  productRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@GetMapping("/Dashboard")
	public String ShowDashboard() {
		if(session.getAttribute("loggedInSeller") == null) {
			return "redirect:/Login";
		}
		return "Seller/Dashboard";
	}
	
	@GetMapping("/AddProduct")
	public String ShowAddProduct(Model model) {
		if(session.getAttribute("loggedInSeller") == null) {
			return "redirect:/Login";
		}
		List<ProductCategory>  categories = productCategoryRepo.findAll();
				
		model.addAttribute("categories", categories);
	    model.addAttribute("productDto", new ProductDTO());
		return "Seller/AddProduct";
	}
	
	@PostMapping("/AddProduct")
	public String AddProduct(@ModelAttribute("productDto") ProductDTO dto, @RequestParam("productMultiImages") MultipartFile[] productImages, RedirectAttributes attributes) {
		try {
			Users seller = (Users) session.getAttribute("loggedInSeller");
			productService.saveProduct(dto, productImages, seller);
			attributes.addFlashAttribute("msg", "Product Successfully Added!");
		}catch(Exception e) {
			attributes.addFlashAttribute("msg", e.getMessage());
		}
		return "redirect:/Seller/AddProduct";
	}
	
	
	
	@GetMapping("/ManageProduct")
	public String ShowManageProduct(Model model) {
		if(session.getAttribute("loggedInSeller") == null) {
			return "redirect:/Login";
		}
		Users seller = (Users) session.getAttribute("loggedInSeller");
		List<Products> products = productRepo.findAllBySeller(seller);
		model.addAttribute("products", products);
		
		return "Seller/ManageProduct";
	}
	
	@GetMapping("/ProductVisibility/{id}")
	public String ProductVisibility(@PathVariable long id) {
		productService.ProductVisibility(id);
		return "redirect:/Seller/ManageProduct";
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
	public String ShowEditProfile(@RequestParam("id") long id, Model model) {
		
		Users seller = userRepo.findById(id).orElseThrow(null);
		model.addAttribute("loggedInSeller", seller);
		return "Seller/EditProfile";
	}
	
	@PostMapping("/EditProfile")
	public String EditProfile(@ModelAttribute Users seller, @RequestParam("profileImage") MultipartFile profileImage, RedirectAttributes attributes) {
		try {
			userService.updateEditProfile(seller, profileImage);
			attributes.addFlashAttribute("msg", "Profile Update Successfully!");
		}catch(Exception e) {
			attributes.addFlashAttribute("msg","Error: "+ e.getMessage());
		}
		return "redirect:/Seller/EditProfile?id="+ seller.getId();
		
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
//		@GetMapping("/logout")
//		public String Logout() {
//			
//			session.removeAttribute("loggedInSeller");
//			return "redirect:/Login";
//		}

}
