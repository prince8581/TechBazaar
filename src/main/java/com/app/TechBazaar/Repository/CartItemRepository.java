package com.app.TechBazaar.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.CartItem;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Users;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	CartItem findByUserAndProduct(Users user, Products product);

	//@Query("SELECT sum(quantity) FROM CartItem WHERE user = :user")
	int countByUser(Users user);

	CartItem findAllBySessionIdAndProduct(String id, Products product);

	//@Query("SELECT sum(quantity) FROM CartItem WHERE sessionId = :id")
	int countBySessionId(String id);

	List<CartItem> findAllBySessionId(String sessionId);

	List<CartItem> findAllByUser(Users user);

	void deleteByUserAndProduct(Users user, Products product);





	
}
