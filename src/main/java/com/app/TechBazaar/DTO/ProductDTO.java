package com.app.TechBazaar.DTO;



import com.app.TechBazaar.Model.ProductCategory;



public class ProductDTO {
	
	//Product Details
		private String productName;
		private String productDescription;
		private String brandName;
		private ProductCategory category;
		
		//Product Pricing 
		private double pricePerUnit;
		private int discount;
		private double finalPrice;      
		
		//Product inventory
		private int quantityAvailable;
	      
		
		//Warranty Details
		private boolean warranty;
	    private int warrantyDuration;  //in numbers
	    private String warrantyUnit;    //Days, months,years
	    private String warrantyTerms;    
	    
	    
	    //Return Policy
	    private boolean returnAvailable;
	    private int returnDays;
	    private String returnConditions;
	    
	    //cancellation policy
	    private boolean  cancellationAllowed;
	    
	    //Shipping Details
	    private String shippingType;  //free, paid, express
	    private double shippingCharge;
	    private int minDeliveryDays;
	    private int maxDeliveryDays;
	    private boolean codAvailable;
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
		

	    
}
