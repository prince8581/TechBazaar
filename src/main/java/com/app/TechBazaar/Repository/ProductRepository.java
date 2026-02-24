package com.app.TechBazaar.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.ProductCategory;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Users;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {

	List<Products> findAllBySeller(Users seller);

	List<Products> findAllByCategoryAndVisibility(ProductCategory category, boolean b);

	List<Products> findAllByVisibility(boolean b);

	Object countBySeller(Users seller);

	
	@Query("SELECT SUM(p.finalPrice * p.quantityAvailable) FROM Products p WHERE seller =:seller")
	double getTotalInStockRevenueBySeller(Users seller);
	
}
