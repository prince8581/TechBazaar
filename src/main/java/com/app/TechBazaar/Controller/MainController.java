
package com.app.TechBazaar.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.TechBazaar.DTO.EnquiryDTO;
import com.app.TechBazaar.DTO.UserDTO;
import com.app.TechBazaar.Model.CartItem;
import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.ProductCategory;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.SavedAddress;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Model.Users.UserRole;
import com.app.TechBazaar.Model.Users.UserStatus;
import com.app.TechBazaar.Repository.CartItemRepository;
import com.app.TechBazaar.Repository.EnquiryRepository;
import com.app.TechBazaar.Repository.OrderRepository;
import com.app.TechBazaar.Repository.ProductCategoryRepository;
import com.app.TechBazaar.Repository.ProductRepository;
import com.app.TechBazaar.Repository.SavedAddressRepository;
import com.app.TechBazaar.Repository.UserRepository;
import com.app.TechBazaar.Service.CartItemService;
import com.app.TechBazaar.Service.EnquiryService;
import com.app.TechBazaar.Service.OrderService;
import com.app.TechBazaar.Service.SavedAddressService;
import com.app.TechBazaar.Service.UserService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
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
	
	@Autowired
	private ProductCategoryRepository categoryRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private HttpSession session;
	
	@Autowired
	private CartItemService cartItemService;
	
	@Autowired
	private CartItemRepository cartItemRepo;
	
	@Autowired
	private SavedAddressRepository savedAddressRepo;
	
	@Autowired
	private SavedAddressService addressService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepo;

	
	@GetMapping("/")
	public String ShowIndex(Model model) {
		
		return "index";
	}
	
	@GetMapping("/Orders")
	public String ShowOrders(Model model) {
		if(session.getAttribute("loggedInUser") == null) {
			return "redirect:/Login";
		}
		
		Users user = (Users) session.getAttribute("loggedInUser");
		List<Orders> orders = orderRepo.findAllByUser(user);
		model.addAttribute("orders", orders);
		return "Orders";
	}
	
	@GetMapping({"/Products","/Products/{selectedCategory}"})
	public String ShowProducts(@PathVariable(value = "selectedCategory", required = false) String selectedCategory , Model model) {
		
		List<ProductCategory> categories = categoryRepo.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("selectedCategory", selectedCategory);
		
		//List<Products> products;
		
		if(selectedCategory!=null) {
			ProductCategory category = categoryRepo.findByCategoryName(selectedCategory);
			List<Products> products = productRepo.findAllByCategoryAndVisibility(category, true);
			model.addAttribute("products",products);
			
		}
		else {
			List<Products> products = productRepo.findAllByVisibility(true);
			model.addAttribute("products",products);
		}
		//model.addAttribute("products", products);
		
		return "Products";
	}
	
	@GetMapping("/ViewProduct/{SelectedProduct}/{id}")
	public String ShowViewProduct(@PathVariable("id") long id, Model model) {
		
		Products products = productRepo.findById(id).orElseThrow(()-> new RuntimeException("Prdouct not found!"));
		model.addAttribute("product", products);
		return "ViewProduct";
	}
	
	@PostMapping("/product/addToCart/{id}")
	@ResponseBody
	public Map<String, Object> AddToCart(@PathVariable("id") long id, RedirectAttributes attributes) {
		
		
		try {
			Users user = (Users) session.getAttribute("loggedInUser");
			
			int count = cartItemService.addToCart(id, session, user);
			
			return Map.of(
					"message", "Product successfully added into cart!",
					"count", count
					);
			
		}catch(Exception e) {
			
			return Map.of("message",e.getMessage());
		}
		
	}
	
	@GetMapping("/ViewCart")
	public String ShowViewCart(Model model) {
		
		List<CartItem> cartItems = cartItemService.getCartItems(session);
		
		double totalPrice = cartItems.stream()
				.mapToDouble(cartItem -> 
						cartItem.getQuantity()*cartItem.getProduct().getPricePerUnit()
						).sum();
		
		double finalPrice = cartItems.stream()
				.mapToDouble(cartItem ->
				cartItem.getQuantity()*cartItem.getProduct().getFinalPrice()
						).sum();
		
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("finalPrice", finalPrice);
		model.addAttribute("cartItems", cartItems);
		
		Users user = (Users) session.getAttribute("loggedInUser");
		List<SavedAddress> addresses = savedAddressRepo.findAllByUser(user);
		model.addAttribute("addresses", addresses);
		
		SavedAddress address = savedAddressRepo.findByUserAndActive(user,true);
		model.addAttribute("address", address);
		
	    return "ViewCart";	
	}
	
	
	@GetMapping("/RemoveCartItem/{id}")
	public String RemoveCartItem(@PathVariable("id") long  cartItemId) {
		cartItemRepo.deleteById(cartItemId);
		return "redirect:/ViewCart";
		
	}
	
	@PostMapping("/UpdateQuantity/{id}")
	@ResponseBody
	public Map<String, Object> UpdateItemQuantity(@PathVariable("id") long cartId, @RequestParam("quantity") int quantity) {
		//service se method call hoga -- cart service
		cartItemService.updateQuantity(cartId, quantity);
		
         List<CartItem> cartItems = cartItemService.getCartItems(session);
		
		double totalPrice = cartItems.stream()
				.mapToDouble(cartItem -> 
						cartItem.getQuantity()*cartItem.getProduct().getPricePerUnit()
						).sum();
		
		double finalPrice = cartItems.stream()
				.mapToDouble(cartItem ->
				cartItem.getQuantity()*cartItem.getProduct().getFinalPrice()
						).sum();
		
		return Map.of(
				"totalPrice" , totalPrice,
				"finalPrice", finalPrice
				);
		
	}
	
	@PostMapping("/ChangeAddress")
	public String ChangeAddress(@RequestParam("addressId") long addressId, HttpServletRequest request) {
		
		Users user = (Users) session.getAttribute("loggedInUser");
		addressService.changeAddress(user, addressId);
		
		String referer = request.getHeader("Referer");
		if(referer !=null) {
			return "redirect:"+referer;
		}
		else {
			return "redirect:/";
		}
		//return "redirect:/ViewCart";
	}
	
	//Checkout, buynow and Payment start from here
	@GetMapping("/checkout")
    public String ShowCheckout(@RequestParam(required = false) Long productId, @RequestParam(value = "qty", required = false, defaultValue = "1") Integer quantity, Model model, RedirectAttributes attributes) {
		
    	if(session.getAttribute("loggedInUser") ==null) {
    		attributes.addFlashAttribute("msg", "Please Login First!");
    		return "redirect:/Login";
    	}
    	Users user = (Users) session.getAttribute("loggedInUser");
    	double totalPrice = 0;
    	double finalPrice = 0;
    	double shippingCharge = 0;
    	boolean cod = true;
    	
       if(productId!=null) {
    	   Products buyNowProduct = productRepo.findById(productId).orElse(null);
    	   totalPrice = buyNowProduct.getPricePerUnit()*quantity;
    	   finalPrice = buyNowProduct.getFinalPrice()*quantity;
    	   shippingCharge = buyNowProduct.getShippingCharge();
    	   
    	   model.addAttribute("buyNowProduct", buyNowProduct);
    	  
    	   
    	   model.addAttribute("quantity", quantity);    //very important
    	   model.addAttribute("isBuyNow",true);
    	   model.addAttribute("cod", buyNowProduct.isCodAvailable());
    	   
       }else {
    	   
    	   List<CartItem> cartItems = cartItemRepo.findAllByUser(user);
   		
         totalPrice = cartItems.stream()
   				.mapToDouble(cartItem -> 
   						cartItem.getQuantity()*cartItem.getProduct().getPricePerUnit()
   						).sum();
   		
   		finalPrice = cartItems.stream()
   				.mapToDouble(cartItem ->
   				cartItem.getQuantity()*cartItem.getProduct().getFinalPrice()
   						).sum();
   		shippingCharge = cartItems.stream()
   				.mapToDouble(cartItem -> cartItem.getProduct().getShippingCharge()).sum();
   		
   		for(CartItem item : cartItems) {
   			if(!item.getProduct().isCodAvailable()) {
   				cod = false;
   				break;
   				
   			}
   		}
   		model.addAttribute("cod", cod);
   		model.addAttribute("cartItems", cartItems);
   		model.addAttribute("isBuyNow", false);
       }
		
		
		
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("finalPrice", finalPrice+shippingCharge);
		model.addAttribute("shippingCharge",shippingCharge);
		
		
		List<SavedAddress> addresses = savedAddressRepo.findAllByUser(user);
		model.addAttribute("addresses", addresses);
		
		SavedAddress address = savedAddressRepo.findByUserAndActive(user,true);
		model.addAttribute("address", address);

    	
    	
    	return "checkout";
    }
	
	@PostMapping("/place-order")
	@ResponseBody
	public ResponseEntity<?> placeOrder(@RequestParam String paymentMethod, @RequestParam(required = false) Long productId, @RequestParam(value = "qty", required = false) Integer quantity) {
		
		try {
			Users user = (Users) session.getAttribute("loggedInUser");
			return ResponseEntity.ok(orderService.checkOut(user, productId, quantity, paymentMethod));
		}catch(Exception e) {
			
			return ResponseEntity.ok(Map.of(
					"message",e.getMessage(),
					"status","FAILED"
					
					));
		}
		
	}
	
	@PostMapping("/verify-payment")
	@ResponseBody
	public ResponseEntity<String> verifyPayment(@RequestBody Map<String, String> payload){

		try {
			orderService.verifyPayment(
					payload.get("razorpay_signature"),
					payload.get("razorpay_order_id"),
					payload.get("razorpay_payment_id")
					);
			return ResponseEntity.ok("Payment Successfull, Order Confirmed");
			
		}catch(Exception e) {
			return ResponseEntity.ok(e.getMessage());
		}
	}
	
	@GetMapping("/Services")
	public String ShowServices() {
		return "Services";
	}
	
	@GetMapping("/AboutUs")
	public String ShowAboutUs() {
		return "AboutUs";
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
					cartItemService.mergeGuestCartToUserCart(session, user);
					session.setAttribute("loggedInUser", user);
					return "redirect:/User/";
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
	
	//Logout
	@GetMapping("/logout")
	public String Logout() {
		
		//session.removeAttribute("loggedInAdmin");
		session.invalidate();
		return "redirect:/Login";
	}
	
	


}
