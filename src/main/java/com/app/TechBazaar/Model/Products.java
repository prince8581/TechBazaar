package com.app.TechBazaar.Model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Products {
	
	//Product Details
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false)
	private String productName;
	
	@Column(nullable = false, length = 1000)
	private String productDescription;
	
	@Column(nullable = false)
	private String brandName;
	
	@ManyToOne
	private ProductCategory category;
	
	@ManyToOne
	private Users seller;
	
	@Column(nullable = false)
	private boolean visibility;
	
	//Product Pricing 
	@Column(nullable = false)
	private double pricePerUnit;
	
	@Column(nullable = false)
	private int discount;
	
	@Column(nullable = false)
	private double finalPrice;
	
	//Product inventory
	@Column(nullable = false)
	private int quantityAvailable;
	
	@Enumerated(EnumType.STRING)
	private ProductStatus status;         //Available, out_of_stock
	
	//Warranty Details
	@Column(nullable = false)
	private boolean warranty;
    private int warrantyDuration;  //in numbers
    private String warrantyUnit;    //Days, months,years
    
    @Column(length = 1000)
    private String warrantyTerms;    
    
    
    //Return Policy
    @Column(nullable = false)
    private boolean returnAvailable;
    
    private int returnDays;
    
    @Column(length = 1000)
    private String returnConditions;
    
    //cancellation policy
    @Column(nullable = false)
    private boolean  cancellationAllowed;
    
    //Shipping Details
    private String shippingType;  //free, paid, express
    
    private double shippingCharge;
    
    private int minDeliveryDays;
    
    private int maxDeliveryDays;
    
    @Column(nullable = false)
    private boolean codAvailable;
    
    @ElementCollection
	private List<String> productImages;
    
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public enum ProductStatus{
		OUT_OF_STOCK, AVAILABLE
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public Users getSeller() {
		return seller;
	}

	public void setSeller(Users seller) {
		this.seller = seller;
	}

	public boolean isVisibility() {
		return visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(double finalPrice) {
		this.finalPrice = finalPrice;
	}

	public int getQuantityAvailable() {
		return quantityAvailable;
	}

	public void setQuantityAvailable(int quantityAvailable) {
		this.quantityAvailable = quantityAvailable;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public void setStatus(ProductStatus status) {
		this.status = status;
	}

	public boolean isWarranty() {
		return warranty;
	}

	public void setWarranty(boolean warranty) {
		this.warranty = warranty;
	}

	public int getWarrantyDuration() {
		return warrantyDuration;
	}

	public void setWarrantyDuration(int warrantyDuration) {
		this.warrantyDuration = warrantyDuration;
	}

	public String getWarrantyUnit() {
		return warrantyUnit;
	}

	public void setWarrantyUnit(String warrantyUnit) {
		this.warrantyUnit = warrantyUnit;
	}

	public String getWarrantyTerms() {
		return warrantyTerms;
	}

	public void setWarrantyTerms(String warrantyTerms) {
		this.warrantyTerms = warrantyTerms;
	}

	public boolean isReturnAvailable() {
		return returnAvailable;
	}

	public void setReturnAvailable(boolean returnAvailable) {
		this.returnAvailable = returnAvailable;
	}

	public int getReturnDays() {
		return returnDays;
	}

	public void setReturnDays(int returnDays) {
		this.returnDays = returnDays;
	}

	public String getReturnConditions() {
		return returnConditions;
	}

	public void setReturnConditions(String returnConditions) {
		this.returnConditions = returnConditions;
	}

	public boolean isCancellationAllowed() {
		return cancellationAllowed;
	}

	public void setCancellationAllowed(boolean cancellationAllowed) {
		this.cancellationAllowed = cancellationAllowed;
	}

	public String getShippingType() {
		return shippingType;
	}

	public void setShippingType(String shippingType) {
		this.shippingType = shippingType;
	}

	public double getShippingCharge() {
		return shippingCharge;
	}

	public void setShippingCharge(double shippingCharge) {
		this.shippingCharge = shippingCharge;
	}

	public int getMinDeliveryDays() {
		return minDeliveryDays;
	}

	public void setMinDeliveryDays(int minDeliveryDays) {
		this.minDeliveryDays = minDeliveryDays;
	}

	public int getMaxDeliveryDays() {
		return maxDeliveryDays;
	}

	public void setMaxDeliveryDays(int maxDeliveryDays) {
		this.maxDeliveryDays = maxDeliveryDays;
	}

	public boolean isCodAvailable() {
		return codAvailable;
	}

	public void setCodAvailable(boolean codAvailable) {
		this.codAvailable = codAvailable;
	}

	public List<String> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<String> productImages) {
		this.productImages = productImages;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	

	
}
