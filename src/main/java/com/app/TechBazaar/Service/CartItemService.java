package com.app.TechBazaar.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.Model.CartItem;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.CartItemRepository;
import com.app.TechBazaar.Repository.ProductRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class CartItemService {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CartItemRepository cartItemRepo;

	public int addToCart(long productId, HttpSession session, Users user) {
		
		Products product = productRepo.findById(productId).orElseThrow(()-> new RuntimeException("Product Not found"));
		
		//If User Exists 
		if(user!=null) {
			CartItem existingCartItem = cartItemRepo.findByUserAndProduct(user, product);
			
			if(existingCartItem != null) {
			return  cartItemRepo.countByUser(user);
		 	
		    }
			
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setPrice(product.getFinalPrice());
			cartItem.setQuantity(1);
			cartItem.setUser(user);
			cartItem.setAddedAt(LocalDateTime.now());
			cartItemRepo.save(cartItem);
			return cartItemRepo.countByUser(user);
		}
		
		//if User not exists --- do it by sessionId
		if(session!=null) {
			CartItem exisitCartItem  = cartItemRepo.findAllBySessionIdAndProduct(session.getId(), product);
			
			if(exisitCartItem!=null) {
				return cartItemRepo.countBySessionId(session.getId());
				
			}
			
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setPrice(product.getFinalPrice());
			cartItem.setQuantity(1);
			cartItem.setSessionId(session.getId());
			cartItem.setAddedAt(LocalDateTime.now());
			cartItemRepo.save(cartItem);
			return cartItemRepo.countBySessionId(session.getId());
		}
		return 0;
	}
	
	//Merge Guest cart into user cart
	@Transactional
	public void mergeGuestCartToUserCart(HttpSession session, Users user) {
		String sessionId = session.getId();
		List<CartItem> cartItems = cartItemRepo.findAllBySessionId(sessionId);
		
		for(CartItem item : cartItems) {
			CartItem existingItem = cartItemRepo.findByUserAndProduct(user, item.getProduct());
			
			if(existingItem!=null) {
				cartItemRepo.delete(existingItem);
			}
			
				item.setUser(user);
				item.setSessionId(null);
				item.setAddedAt(LocalDateTime.now());
				cartItemRepo.save(item);
			
		}
	}
	
	public List<CartItem> getCartItems(HttpSession session){
		Users user = (Users) session.getAttribute("loggedInUser");
		if(user!=null) {
			List<CartItem> cartItems = cartItemRepo.findAllByUser(user);
			return cartItems;
		}
		else {
			List<CartItem> cartItems = cartItemRepo.findAllBySessionId(session.getId());
			return cartItems;
		}
	}
	
	public void updateQuantity(long cartId, int quantity) {
		CartItem cartItem = cartItemRepo.findById(cartId).orElseThrow(()-> new RuntimeException("Item not found!"));
		if(quantity>cartItem.getProduct().getQuantityAvailable()) {
			throw new RuntimeException("Limited Stock, you can add maximum  "+cartItem.getProduct().getQuantityAvailable()+ " item only");
		}
		
		cartItem.setQuantity(quantity);
		cartItemRepo.save(cartItem);
	}
	
}
