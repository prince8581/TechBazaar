package com.app.TechBazaar.API;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.annotation.PostConstruct;

@Service
public class RazorpayService {
	
	
	private RazorpayClient razorpayClient;
	
	@Value("${razorpay.key}")
	private String razorpayKey;
	@Value("${razorpay.secret}")
	private String razorpaySecret;
	
	@PostConstruct
	public void init() throws RazorpayException
	{
		this.razorpayClient= new RazorpayClient(razorpayKey, razorpaySecret);
	}
	
	public Order createRazorpayOrder(double  amount) throws RazorpayException
	{
		double amountInPaisa = amount * 100;
		if(amountInPaisa<100) {
			throw new RuntimeException("Amount must be atleast 1 rupees.");
		}
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amountInPaisa); //amount in paisa
		orderRequest.put("currency", "INR");
		//orderRequest.put("reciept", transactionId);
		
		return razorpayClient.orders.create(orderRequest);
	}
	
	public String generateSignature(String razorpayOrderId, String paymentId) {
		
		String data = razorpayOrderId + "|" +paymentId;
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(razorpaySecret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			
			
			byte[] hash = sha256_HMAC.doFinal(data.getBytes());
			return new String(Hex.encodeHex(hash));
			
		}catch(Exception e) {
			throw new RuntimeException("Signature generation failed");
		}

		
	}
	public String getRazorpayKey() {
		return razorpayKey;
	}

}