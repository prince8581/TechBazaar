package com.app.TechBazaar.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.TechBazaar.DTO.ProductDTO;
import com.app.TechBazaar.Model.Products;
import com.app.TechBazaar.Model.Products.ProductStatus;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.ProductCategoryRepository;
import com.app.TechBazaar.Repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ProductCategoryRepository productCategoryRepo;
	
	//Logics
	public void saveProduct(ProductDTO productDTO, MultipartFile[] images, Users user) {
		
		try {
			if(images.length < 1 ) {
				throw new RuntimeException("At Least Upload 1 image");
			}
			if(images.length > 5 ) {
				throw new RuntimeException("You can upload maximum 5 images");
			}
			
			if(productDTO.getQuantityAvailable() < 3 ) {
				throw new RuntimeException("Low Available Qunatity, you can't list your product.");
			}
			
			String uploadDir = "public/ProductImages/";
			File folder = new File(uploadDir);
			
			if(!folder.exists()) {
				folder.mkdirs();
			}
			List<String> productImagesNames = new ArrayList<>();
			for(MultipartFile image : images) {
				String storageFileName = UUID.randomUUID()+"_"+image.getOriginalFilename();
				Path uploadPath = Paths.get(uploadDir, storageFileName);
				Files .copy(image.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
				productImagesNames.add(storageFileName);
				
			}
			
			//Save product into model
			Products product = new Products();
			product.setProductName(productDTO.getProductName());
			product.setProductDescription(productDTO.getProductDescription());
			product.setBrandName(productDTO.getBrandName());
			product.setPricePerUnit(productDTO.getPricePerUnit());
			product.setDiscount(productDTO.getDiscount());
			product.setFinalPrice(productDTO.getFinalPrice());
			product.setQuantityAvailable(productDTO.getQuantityAvailable());
			product.setVisibility(true);
			
			product.setCategory(productDTO.getCategory());
			
			product.setCancellationAllowed(productDTO.isCancellationAllowed());
			product.setCodAvailable(productDTO.isCodAvailable());
			product.setShippingType(productDTO.getShippingType());
			product.setShippingCharge(productDTO.getShippingCharge());
			product.setMinDeliveryDays(productDTO.getMinDeliveryDays());
			product.setMaxDeliveryDays(productDTO.getMaxDeliveryDays());
			product.setReturnAvailable(productDTO.isReturnAvailable());
			product.setReturnConditions(productDTO.getReturnConditions());
			product.setReturnDays(productDTO.getReturnDays());
			product.setWarranty(productDTO.isWarranty());
			product.setWarrantyDuration(productDTO.getWarrantyDuration());
			product.setWarrantyTerms(productDTO.getWarrantyTerms());
			product.setWarrantyUnit(productDTO.getWarrantyUnit());
			product.setStatus(ProductStatus.AVAILABLE);
			product.setCreatedAt(LocalDateTime.now());
			product.setUpdatedAt(LocalDateTime.now());
			product.setSeller(user);
			product.setProductImages(productImagesNames);
			
			productRepo.save(product);
			
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage());
			
		}
		
	}
	
	public void ProductVisibility(long id) {
		Products product = productRepo.findById(id).orElseThrow(()-> new RuntimeException("Product not found!"));
		if(product.isVisibility()) {
			product.setVisibility(false);
		}
		else {
			product.setVisibility(true);
		}
		productRepo.save(product);
	}

}
