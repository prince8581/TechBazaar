package com.app.TechBazaar.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Users;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
	
	@Query("SELECT COUNT(o) FROM Orders o WHERE YEAR(o.orderedAt) = :year")
	Long countByYear(@Param("year") int year);

	List<Orders> findAllByRazorpayOrderId(String razorPayOrderId);

	List<Orders> findAllByUser(Users user);
	

}
