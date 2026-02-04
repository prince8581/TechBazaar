package com.app.TechBazaar.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.DTO.ProductCategoryDTO;
import com.app.TechBazaar.Model.ProductCategory;
import com.app.TechBazaar.Repository.ProductCategoryRepository;

@Service
public class ProductCategoryService {

	@Autowired
	private ProductCategoryRepository productCategoryRepo;
	
	public void saveProductCategory(ProductCategoryDTO dto) {
		 if(productCategoryRepo.existsByCategoryName(dto.getCategoryName())) {
			 throw new RuntimeException("Category Already exists!");
		 }
		 
		 ProductCategory category = new ProductCategory();
		 category.setCategoryName(dto.getCategoryName());
		 category.setDescription(dto.getDescription());
		 category.setActive(true);
		 
		 productCategoryRepo.save(category);
	}
	
	public void updateCategoryStatus(long id) {
		ProductCategory category = productCategoryRepo.findById(id).orElseThrow(()-> new RuntimeException("Somthing went wrong.."));
		if(category.isActive()) {
			category.setActive(false);
		}
		else {
			category.setActive(true);
		}
		productCategoryRepo.save(category);
	}
}
