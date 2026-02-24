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
import com.app.TechBazaar.Model.Feedback;
import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Orders.OrderStatus;
import com.app.TechBazaar.Model.ProductCategory;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.FeedbackRepository;
import com.app.TechBazaar.Repository.OrderRepository;
import com.app.TechBazaar.Repository.ProductCategoryRepository;
import com.app.TechBazaar.Repository.ProductRepository;
import com.app.TechBazaar.Repository.UserRepository;
import com.app.TechBazaar.Service.OrderService;
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
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private FeedbackRepository feedbackRepo;
	
	@GetMapping("/Dashboard")
	public String ShowDashboard(Model model) {
		
		Users seller = (Users) session.getAttribute("loggedInSeller");
		if(seller == null) {
			return "redirect:/Login";
		}
		model.addAttribute("totalProducts",productRepo.countBySeller(seller));
		model.addAttribute("totalOrders",orderRepo.countBySeller(seller));
		model.addAttribute("completedOrders", orderRepo.countBySellerAndOrderStatus(seller,OrderStatus.DELIVERED));
		model.addAttribute("cancelledOrders", orderRepo.countBySellerAndOrderStatus(seller,OrderStatus.CANCELLED));
		
		
		//model.addAttribute("deliveredOrders", orderRepo.countByOrderStatus(OrderStatus.DELIVERED));
		//model.addAttribute("cancelledOrders", orderRepo.countByOrderStatus(OrderStatus.CANCELLED));
		
		List<Orders> recentOrders = orderRepo.findTop5BySellerOrderByOrderedAtDesc(seller);
		model.addAttribute("recentOrders", recentOrders);
		
		model.addAttribute("topProduct", orderRepo.getTopSellingProductBySeller(seller));
		model.addAttribute("monthlyRevenue", orderRepo.getCurrentMonthRevenueBySeller(seller));
		model.addAttribute("totalRevenue", orderRepo.getTotalRevenueBySeller(seller));
		model.addAttribute("inStockRevenue", productRepo.getTotalInStockRevenueBySeller(seller));
		
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
	public String ShowManageOrders(RedirectAttributes attributes,@RequestParam(value="selectedStatus",required = false) OrderStatus orderStatus,Model model) {
		
		
		if(session.getAttribute("loggedInSeller")==null) {
			attributes.addFlashAttribute("msg","SessionExpire");
			return "redirect:/Login";
		
		}
		
		Users user=(Users) session.getAttribute("loggedInSeller");
		if (orderStatus !=null) {
			List<Orders> orders=orderRepo.findAllBySellerAndOrderStatus(user,orderStatus);
			model.addAttribute("orders", orders);
		}
		else 
		{
			List<Orders> orders=orderRepo.findAllBySeller(user);
			model.addAttribute("orders", orders);
		}
		model.addAttribute("orderStatus",Orders.OrderStatus.values());
		
		return "Seller/ManageOrders";
	}
	
	@GetMapping("/UpdateOrderStatus/{id}")
	public String UpdateOrderStatus(@PathVariable("id") long id,RedirectAttributes attributes) 
	{
		try {
			
			attributes.addFlashAttribute("msg", "Order Successfuly"+orderService.updateOrderStatus(id));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return "redirect:/Seller/ManageOrders";
	}
	
	@GetMapping("/CancelOrder/{id}")
	public String CancelOrder(@PathVariable("id") long id,RedirectAttributes attributes) 
	{
		try {
			orderService.cancelOrder(id);
			attributes.addFlashAttribute("msg", "order  cancelled successfuly");
			return "redirect:/Seller/ManageOrders";
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg",e.getMessage());
			return "redirect:/Seller/ManageOrders";
		}
		
		
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
	
	@GetMapping("/UpdateProduct{id}")
	public String ShowUpdateProduct(@PathVariable long id,Model model,RedirectAttributes attributes) 
	{
		if(session.getAttribute("loggedInSeller")==null) {
			attributes.addFlashAttribute("msg","SessionExpire");
			return "redirect:/Login";
		
		}
		Products product=productRepo.findById(id).orElseThrow(null);
		ProductDTO productdto=new ProductDTO();
		List<ProductCategory> categories=productCategoryRepo.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("product", product);
		model.addAttribute("productdto", productdto);
		
		return "Seller/UpdateProduct";
	}
	
	@PostMapping("/UpdateProduct")
	public String UpdateProduct(@RequestParam("id") long id,@ModelAttribute ProductDTO productDto,RedirectAttributes attributes) 
	{
		try {
			
			productService.updateProduct(id, productDto);
			attributes.addFlashAttribute("msg","Product Successfuly Update");
			
		} catch (Exception e) {
			attributes.addFlashAttribute("msg",e.getMessage());
		}
		return "redirect:/Seller/ManageProducts";
	}
	
	
	
	@GetMapping("/Feedback")
    public String showAllFeedback(Model model, HttpSession session) {

        if (session.getAttribute("loggedInSeller") == null) {
            return "redirect:/Login";
        }

        List<Feedback> feedback = feedbackRepo.findAll();

        model.addAttribute("feedback", feedback);
        
        

        return "Seller/Feedback";
    }
	@GetMapping("/deleteFeedback/{id}")
	public String deleteFeedback(@PathVariable Long id,
	                             HttpSession session,
	                             RedirectAttributes attributes) {

	    if (session.getAttribute("loggedInSeller") == null) {
	        return "redirect:/Login";
	    }

	    feedbackRepo.deleteById(id);

	    attributes.addFlashAttribute("success",
	            "Feedback deleted successfully!");

	    return "redirect:/Seller/Feedback";
	}
	
	//Logout
//		@GetMapping("/logout")
//		public String Logout() {
//			
//			session.removeAttribute("loggedInSeller");
//			return "redirect:/Login";
//		}

}
