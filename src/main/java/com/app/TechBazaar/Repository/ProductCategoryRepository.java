package com.app.TechBazaar.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long>{

	boolean existsByCategoryName(String categoryName);

	ProductCategory findByCategoryName(String selectedCategory);

}
