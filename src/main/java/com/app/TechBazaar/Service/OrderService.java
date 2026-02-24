package com.app.TechBazaar.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.API.RazorpayService;
import com.app.TechBazaar.API.SendEmailService;
import com.app.TechBazaar.Model.CartItem;
import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Orders.OrderSource;
import com.app.TechBazaar.Model.Orders.OrderStatus;
import com.app.TechBazaar.Model.Orders.PaymentMethod;
import com.app.TechBazaar.Model.Orders.PaymentStatus;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Products.ProductStatus;
import com.app.TechBazaar.Model.SavedAddress;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.CartItemRepository;
import com.app.TechBazaar.Repository.OrderRepository;
import com.app.TechBazaar.Repository.ProductRepository;
import com.app.TechBazaar.Repository.SavedAddressRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private SavedAddressRepository addressRepo;
	
	@Autowired
	private CartItemRepository cartItemRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private RazorpayService razorpayService;
	
	@Autowired
	private SendEmailService emailService;
	
	
	@Transactional
	public Map<String, Object> checkOut(Users user, Long productId, Integer quantity, String paymentMethod){
		
		//Buy Now Logic
		Map<String, Object> response = new HashMap<>();
		
		SavedAddress address = addressRepo.findByUserAndActive(user, true);
		if(address == null) 
			throw new RuntimeException("No Active Address Found! ");
			
		List<CartItem> cartItems;
		
			// Buy Now Logic (place order)
			if(productId!=null) {
				Products product = productRepo.findById(productId).orElseThrow(()-> new RuntimeException("Product Not Found!"));
				
				if(product.getQuantityAvailable() < quantity) {
					throw new RuntimeException("Insufficient Stock");
				}
				CartItem item =new CartItem();
				item.setProduct(product);
				item.setQuantity(quantity);
				item.setUser(user);
				
				cartItems = List.of(item);
			}
			else {
				//cart Item Logic(Place Order)
				cartItems = cartItemRepo.findAllByUser(user);
				if(cartItems == null)
					throw new RuntimeException("Cart is Empty! ");
			}
			
		  List<Orders> onlineOrderList = new ArrayList<>();
		  double totalAmountToPay = 0;
		 // String transactionId = "txn_"+System.currentTimeMillis();
		  
			
			for(CartItem item : cartItems) {
				 Products product = item.getProduct();
				 
				 if(product.getQuantityAvailable() < item.getQuantity()) 
					 throw new RuntimeException(product.getProductName()+ " is Out of Stock");
				 
				 if(paymentMethod.equalsIgnoreCase("COD") && !product.isCodAvailable())
					 throw new RuntimeException("COD is not Available for "+product.getProductName());
				 
				 
				 
				 Orders order = new Orders();
				 order.setOrderNumber(generateOrderNumber());
				 order.setUser(user);
				 order.setSeller(product.getSeller());
				 order.setProduct(product);
				 
				 order.setProductName(product.getProductName());
				 order.setQuantity(item.getQuantity());
				 order.setPrice(product.getFinalPrice());
				 order.setSubtotal(product.getFinalPrice()*item.getQuantity());
				 
				 order.setShippingCharge(product.getShippingCharge());
				 order.setDiscountAmount(product.getPricePerUnit()-product.getFinalPrice());
				 order.setFinalAmount(product.getFinalPrice()*item.getQuantity()+product.getShippingCharge());
				 order.setPaymentMethod(paymentMethod.equalsIgnoreCase("COD") ? PaymentMethod.COD : PaymentMethod.ONLINE);
				 order.setPaymentStatus(PaymentStatus.PENDING);
				 //rzpId, txnid, psign
				 
				 //Address Snapshot
				 order.setFullName(address.getName());
				 order.setPhone(address.getContactNo());
				 order.setAddress(address.getAddress());
				 order.setCity(address.getCityDistrict());
				 order.setState(address.getState());
				 order.setPincode(address.getPincode());
				 
				 order.setOrderStatus(paymentMethod.equalsIgnoreCase("COD") ? OrderStatus.CONFIRMED : OrderStatus.PLACED);
				 order.setOrderedAt(LocalDateTime.now());
				 
				 if(productId!=null) {
					 order.setOrderSource(OrderSource.BUY_NOW);
				 }
				 else {
					 order.setOrderSource(OrderSource.CART);
				 }
				 
				 orderRepo.save(order);
				 
				 totalAmountToPay = totalAmountToPay +  order.getFinalAmount();
				 
				 if(paymentMethod.equalsIgnoreCase("ONLINE")) {
					 onlineOrderList.add(order);
					 //order.setTransactionId(transactionId);
					 
				 }
				 if(paymentMethod.equalsIgnoreCase("COD")) {
					 //reduce stock
					 
					 product.setQuantityAvailable(product.getQuantityAvailable() - item.getQuantity());
					 if(product.getQuantityAvailable()  < 1) {
						 product.setStatus(ProductStatus.OUT_OF_STOCK);
					 }
					 productRepo.save(product);
				 }
			}
		
			
			//Response
			//COD
			if(paymentMethod.equalsIgnoreCase("COD")) {
				response.put("status", "COD_SUCCESS");
				if(productId==null) {
					cartItemRepo.deleteAll(cartItems);
				}
				return response;
			}
			
			//ONLINE -- try-------createRazorpayOrder
			try {
				Order  razorpayOrder = razorpayService.createRazorpayOrder(totalAmountToPay);
				String razorpayOrderId = razorpayOrder.get("id");
				
				for(Orders order : onlineOrderList) {
					order.setRazorpayOrderId(razorpayOrderId);
					orderRepo.save(order);
					
				}
				
				response.put("status", "ONLINE");
				response.put("amount", totalAmountToPay * 100);
				response.put("key", razorpayService.getRazorpayKey());
				response.put("razorpayOrderId", razorpayOrderId);
				
				return response;
				
			}catch(Exception e) {
				throw new RuntimeException("Payment Failed");
			}
		
		
	}
	
	public String generateOrderNumber() {
		
		int year = LocalDate.now().getYear();
		long count = orderRepo.countByYear(year);
		
		//TB-2026-000001
		return "TB-"+year+"-"+String.format("%05d", count + 1);
	}
	
	
	//verify payment method
	@Transactional
	public void verifyPayment(String signature, String razorPayOrderId, String paymentId){
		
		// find orders by razorpayorderid
		List<Orders> orders = orderRepo.findAllByRazorpayOrderId(razorPayOrderId);
	    if(orders == null) {
	    	throw new RuntimeException("No orders found!");
	    }
	    
	    if(orders.get(0).getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
	    	throw new RuntimeException("Already Paid!");
	    }
	    try {
	    	//generate payment signature
	    	String generatedSignature = razorpayService.generateSignature(razorPayOrderId, paymentId);
	    	
	    	if(!generatedSignature.equals(signature)) {
	    		throw new RuntimeException("Invalid payment Signature!");
	    	}
	    	
	    	for(Orders order : orders) {
	    		order.setPaymentStatus(PaymentStatus.SUCCESS);
	    		order.setPaymentSignature(signature);
	    		order.setTransactionId(paymentId);
	    		order.setPaymentTime(LocalDateTime.now());
	    		order.setOrderStatus(OrderStatus.CONFIRMED);
	    		orderRepo.save(order);
	    		
	    		Products product = order.getProduct();
	    		product.setQuantityAvailable(product.getQuantityAvailable() - order.getQuantity());
	    		if(product.getQuantityAvailable() < 1 ) {
	    			product.setStatus(ProductStatus.OUT_OF_STOCK);
	    		}
	    		productRepo.save(product);
	    		
	    		if(order.getOrderSource().equals(OrderSource.CART)) {
	    			cartItemRepo.deleteByUserAndProduct(order.getUser(), order.getProduct());
	    		}
	    		
	    		
	    	}
	    	
	    }catch(Exception e) {
	    	for(Orders order : orders) {
	    		order.setPaymentStatus(PaymentStatus.FAILED);
	    		orderRepo.save(order);
	    		
	    	}

	    	throw new RuntimeException(e.getMessage());
	    	
	    	
	    }
	}
	//new code
	 public OrderStatus updateOrderStatus(long id) throws RazorpayException
	 {
		 Orders order = orderRepo.findById(id).orElseThrow(()->new RuntimeException("Order Not Found"));
		 if (order.getOrderStatus().equals(OrderStatus.CONFIRMED)) {
			 emailService.sendOrderConfirmed(order.getUser(), order);
			order.setOrderStatus(OrderStatus.PROCESSING);
			
		}
		 else if(order.getOrderStatus().equals(OrderStatus.PROCESSING))
		 {
			 order.setOrderStatus(OrderStatus.DISPATCHED);
		 }
		 else if(order.getOrderStatus().equals(OrderStatus.DISPATCHED))
		 {
			 order.setOrderStatus(OrderStatus.SHIPPED);
		 }
		 else if(order.getOrderStatus().equals(OrderStatus.SHIPPED))
		 {
			 order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
		 }
		 else if(order.getOrderStatus().equals(OrderStatus.OUT_FOR_DELIVERY))
		 {
			 
			 order.setOrderStatus(OrderStatus.DELIVERED);
			 order.setDeliveredAt(LocalDateTime.now());
			 if (order.getPaymentMethod().equals(PaymentMethod.COD)) {
				order.setPaymentStatus(PaymentStatus.SUCCESS);
				emailService.sendOrderDelivered(order.getUser(), order);
			}
		 }
		 else if(order.getOrderStatus().equals(OrderStatus.RETURN_REQUESTED))
		 {
			 order.setOrderStatus(OrderStatus.RETURNED);
			 order.setReturnedAt(LocalDateTime.now());
			 order.setPaymentStatus(PaymentStatus.REFUNDED);
			 emailService.sendOrderReturn(order.getUser(), order);
			 order.setRefundedAt(LocalDateTime.now());
			 if (order.getPaymentMethod().equals(PaymentMethod.ONLINE)&& order.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
				razorpayService.refundPayment(order.getTransactionId());
				
			}
		 }
		 
		 orderRepo.save(order);
		 return order.getOrderStatus();
	 }
	 
	 
	 public  void cancelOrder(long id) throws RazorpayException
	 {
		 Orders order = orderRepo.findById(id).orElseThrow(()-> new RuntimeException("Order Not Found"));
		 if (order.getPaymentMethod().equals(PaymentMethod.ONLINE)) {
			 
			 if (!order.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
				throw new RuntimeException("Something Went Wrong");
			}
			 order.setPaymentStatus(PaymentStatus.REFUNDED);
			 order.setRefundedAt(LocalDateTime.now());
			 
			razorpayService.refundPayment(order.getTransactionId());
			
		}
		 order.setOrderStatus(OrderStatus.CANCELLED);
		 
		 order.setCancelledAt(LocalDateTime.now());
		 
		 orderRepo.save(order);
		 emailService.sendOrderCancelled(order.getUser(), order);
	 }
	 
	 public void returnRequest(long id)
	 {
		 Orders order = orderRepo.findById(id).orElseThrow(()->new RuntimeException("Order Not  Found"));
		 if (order.getProduct().isReturnAvailable())
		 {
			order.setOrderStatus(OrderStatus.RETURN_REQUESTED);
			orderRepo.save(order);
		}
	 }


}
