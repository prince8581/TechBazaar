package com.app.TechBazaar.API;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Users;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SendEmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	/* =====================================================
    COMMON HTML EMAIL SENDER
     ===================================================== */
     private void sendHtmlMail(String to, String subject, String content) {
     try {
     MimeMessage mimeMessage = mailSender.createMimeMessage();
     MimeMessageHelper helper =
    new MimeMessageHelper(mimeMessage, true, "UTF-8");

     helper.setTo(to);
     helper.setSubject(subject);
     helper.setText(content, true);

    mailSender.send(mimeMessage);

    } catch (Exception e) {
    throw new RuntimeException(e.getMessage());
     }
    }

/* =====================================================
    COMMON TEMPLATE BUILDER
===================================================== */
     private String buildTemplate(String title,
                 String userName,
                 String bodyContent) {

       return "<!DOCTYPE html>" +
       "<html><head><style>" +
        "body{font-family:Arial;background:#f4f6f9;margin:0;padding:0}" +
        ".container{max-width:600px;margin:40px auto;background:#fff;border-radius:10px;overflow:hidden;box-shadow:0 4px 15px rgba(0,0,0,0.1)}" +
         ".header{background:linear-gradient(90deg,#ff416c,#ff4b2b);color:white;padding:20px;text-align:center}" +
         ".content{padding:30px;text-align:center}" +
       ".footer{background:#f1f1f1;padding:15px;text-align:center;font-size:12px;color:#666}" +
      ".btn{display:inline-block;padding:10px 20px;background:#ff416c;color:#fff;text-decoration:none;border-radius:5px;margin-top:15px}" +
     "</style></head>" +

      "<body>" +
     "<div class='container'>" +

     "<div class='header'>" +
     "<h2>" + title + "</h2>" +
     "</div>" +

   "<div class='content'>" +
   "<h3>Hello " + userName + " 👋</h3>" +
    bodyContent +
    "</div>" +

   "<div class='footer'>" +
   "Need help? Contact TechBazaar Support 💬<br>" +
  "© 2026 TechBazaar. All rights reserved." +
   "</div>" +

     "</div></body></html>";
    }

      /* =====================================================
        1️⃣ REGISTRATION OTP
       ===================================================== */
      public void sendRegistrationOTP(Users user, String otp) {

       String body =
       "<p>Thanks for registering with us! 🎉</p>" +
       "<p>Your OTP is:</p>" +
       "<div style='font-size:28px;font-weight:bold;color:#ff416c;margin:20px 0'>" +
        otp + "</div>" +
        "<p>⏳ Valid for 5 minutes.</p>" +
      "<p>Please do not share it with anyone 🔒</p>";

       String content = buildTemplate("🔐 TechBazaar Registration OTP",
      user.getName(), body);

       sendHtmlMail(user.getEmail(),
      "Your TechBazaar OTP",
       content);
      }

      /* =====================================================
        2️⃣ ORDER CONFIRMED
     ===================================================== */
    public void sendOrderConfirmed(Users user, Orders order) {

      String body =
     "<p>🎉 Your order has been <b>Confirmed</b> successfully!</p>" +
     "<p><b>Order ID:</b> #" + order.getId() + "</p>" +
     "<p>We are preparing your item for shipment 🚚</p>";

    String content = buildTemplate("📦 Order Confirmed",
    user.getName(), body);

    sendHtmlMail(user.getEmail(),
     "Order Confirmed - TechBazaar",
    content);
     }

/* =====================================================
        3️⃣ ORDER CANCELLED
===================================================== */
      public void sendOrderCancelled(Users user, Orders order) {

     String body =
      "<p>❌ Your order has been <b>Cancelled</b>.</p>" +
      "<p><b>Order ID:</b> #" + order.getId() + "</p>" +
      "<p>If payment was made, refund will be processed soon 💰</p>";

      String content = buildTemplate("Order Cancelled",
     user.getName(), body);

     sendHtmlMail(user.getEmail(),
    "Order Cancelled - TechBazaar",
     content);
     }

    /* =====================================================
        4️⃣ ORDER DELIVERED
      ===================================================== */
       public void sendOrderDelivered(Users user, Orders order) {

       String body =
       "<p>🎉 Your order has been <b>Delivered Successfully!</b></p>" +
       "<p><b>Order ID:</b> #" + order.getId() + "</p>" +
         "<p>We hope you love your purchase ❤️</p>" +
      "<p>Don’t forget to leave feedback ⭐</p>";

      String content = buildTemplate("🚚 Order Delivered",
      user.getName(), body);

       sendHtmlMail(user.getEmail(),
       "Order Delivered - TechBazaar",
       content);
       }

/* =====================================================
        5️⃣ ORDER RETURN
===================================================== */
       public void sendOrderReturn(Users user, Orders order) {

        String body =
        "<p>🔁 Your return request has been received.</p>" +
        "<p><b>Order ID:</b> #" + order.getId() + "</p>" +
        "<p>Our team will review and process shortly.</p>";

        String content = buildTemplate("Return Request Received",
        user.getName(), body);

        sendHtmlMail(user.getEmail(),
        "Return Request - TechBazaar",
        content);
        }
		
		
		
	
	
}
