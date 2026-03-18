package com.app.TechBazaar.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Orders.OrderStatus;
import com.app.TechBazaar.Model.Users;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
	
	@Query("SELECT COUNT(o) FROM Orders o WHERE YEAR(o.orderedAt) = :year")
	Long countByYear(@Param("year") int year);

	List<Orders> findAllByRazorpayOrderId(String razorPayOrderId);

	List<Orders> findAllByUser(Users user);

	Object countByOrderStatus(OrderStatus cancelled);

	List<Orders> findAllBySellerAndOrderStatus(Users user, OrderStatus orderStatus);

	List<Orders> findAllBySeller(Users user);

	@Query(value = "SELECT MONTH(o.ordered_at) AS month, COUNT(*) AS total FROM Orders o GROUP BY MONTH(o.ordered_at) ORDER BY month", nativeQuery = true)
	List<Object[]> getMonthlyOrderStats();

	Object countBySeller(Users seller);

	Object countBySellerAndOrderStatus(Users seller, OrderStatus delivered);

	List<Orders> findTop5BySellerOrderByOrderedAtDesc(Users seller);
	
	///Double getTotalRevenueBySeller(Users seller);
	@Query("""
		       SELECT COALESCE(SUM(o.finalAmount), 0)
		       FROM Orders o
		       WHERE o.seller = :seller
		       AND o.orderStatus = 'DELIVERED'
		       """)
		Double getTotalRevenueBySeller(@Param("seller") Users seller);
	//@Query("SELECT SUM(o.finalAmount) FROM Orders o WHERE o.seller = :seller AND o.orderStatus = 'DELIVERED' or o.paymentStatus ='SUCCESS'")
	//double getTotalRevenueBySeller(Users seller);
	

	@Query("""
		       SELECT COALESCE(SUM(o.finalAmount), 0) 
		       FROM Orders o 
		       WHERE o.seller = :seller 
		       AND o.orderStatus = 'DELIVERED'
		       AND MONTH(o.deliveredAt) = MONTH(CURRENT_DATE)
		       AND YEAR(o.deliveredAt) = YEAR(CURRENT_DATE)
		       """)
		Double getCurrentMonthRevenueBySeller(@Param("seller") Users seller);
	//@Query("SELECT SUM(o.finalAmount) FROM Orders o WHERE o.seller = :seller AND o.orderStatus = 'DELIVERED' AND MONTH(o.deliveredAt) = MONTH(CURRENT_DATE) AND YEAR(o.deliveredAt) = YEAR(CURRENT_DATE)")
	//Double getCurrentMonthRevenueBySeller(Users seller);
	//double getCurrentMonthRevenueBySeller(Users seller);
	
	
	@Query("SELECT o.product.productName FROM Orders o WHERE o.seller = :seller AND o.orderStatus = 'DELIVERED' GROUP BY o.product ORDER BY SUM(o.quantity) DESC LIMIT 1")
	String getTopSellingProductBySeller(Users seller);
	
}
