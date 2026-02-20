package com.app.TechBazaar.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String orderNumber;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", nullable = false)
	private Users seller;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Products product;
	
	//Product SnapShot
	private String productName;
	private int quantity;
	private double price;
	private double subtotal;
	
	//Price Summary
	private double shippingCharge;
	private double discountAmount;
	private double finalAmount;
	
	
	
	//payment info
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	
	private String razorpayOrderId;
	private String transactionId;
	private String paymentSignature;
	
	//Address SnapShot
	private String fullname;
	private String phone;
	private String address;
	private String city;
	private String state;
	private String pincode;
	

	//Order Status
	@Enumerated(EnumType.STRING)
	private OrderStatus orderstatus;
	
	@Enumerated(EnumType.STRING)
	private OrderSource ordersource;
	
	
	private LocalDateTime orderedAt;
	private LocalDateTime paymentTime;
	private LocalDateTime deliveredAt;
	private LocalDateTime cancelledAt;
	private LocalDateTime refundedAt;
	
	public enum OrderStatus{
		PLACED,         //User Placed
		CONFIRMED,       //Seller/Admin
		PROCESSING,        //Preparing Product
		DISPATCHED,        //Handed over for shipping
		SHIPPED,
		OUT_FOR_DELIVERY,
		DELIVERED,                 // Completed
		CANCELLED,
		RETURN_REQUESTED,          //Buyer asked return
		RETURNED                    //
		
	}
	
	public enum OrderSource{
		BUY_NOW,
		CART
	}
	
	public enum PaymentMethod{
		COD,
		ONLINE
	}
	
	public enum PaymentStatus{
		PENDING, 
		SUCCESS,
		FAILED,
		REFUNDED
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public Users getSeller() {
		return seller;
	}

	public void setSeller(Users seller) {
		this.seller = seller;
	}

	public Products getProduct() {
		return product;
	}

	public void setProduct(Products product) {
		this.product = product;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public double getShippingCharge() {
		return shippingCharge;
	}

	public void setShippingCharge(double shippingCharge) {
		this.shippingCharge = shippingCharge;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public double getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(double finalAmount) {
		this.finalAmount = finalAmount;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getRazorpayOrderId() {
		return razorpayOrderId;
	}

	public void setRazorpayOrderId(String razorpayOrderId) {
		this.razorpayOrderId = razorpayOrderId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPaymentSignature() {
		return paymentSignature;
	}

	public void setPaymentSignature(String paymentSignature) {
		this.paymentSignature = paymentSignature;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public OrderStatus getOrderstatus() {
		return orderstatus;
	}

	public void setOrderstatus(OrderStatus orderstatus) {
		this.orderstatus = orderstatus;
	}

	public LocalDateTime getOrderedAt() {
		return orderedAt;
	}

	public void setOrderedAt(LocalDateTime orderedAt) {
		this.orderedAt = orderedAt;
	}

	public LocalDateTime getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(LocalDateTime paymentTime) {
		this.paymentTime = paymentTime;
	}

	public LocalDateTime getDeliveredAt() {
		return deliveredAt;
	}

	public void setDeliveredAt(LocalDateTime deliveredAt) {
		this.deliveredAt = deliveredAt;
	}

	public LocalDateTime getCancelledAt() {
		return cancelledAt;
	}

	public void setCancelledAt(LocalDateTime cancelledAt) {
		this.cancelledAt = cancelledAt;
	}

	public LocalDateTime getRefundedAt() {
		return refundedAt;
	}

	public void setRefundedAt(LocalDateTime refundedAt) {
		this.refundedAt = refundedAt;
	}

	public OrderSource getOrdersource() {
		return ordersource;
	}

	public void setOrdersource(OrderSource ordersource) {
		this.ordersource = ordersource;
	}

	
	
	
	
	
	
	
}
