package com.app.TechBazaar.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.DTO.FeedbackDTO;
import com.app.TechBazaar.Model.Feedback;
import com.app.TechBazaar.Model.Orders;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.FeedbackRepository;
import com.app.TechBazaar.Repository.OrderRepository;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepo;

    @Autowired
    private OrderRepository orderRepo;

    private final String uploadDir = "public/feedback/";

    public void saveFeedback(FeedbackDTO dto, Long orderId, Users user) {

        try {

            Orders order = orderRepo.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Feedback feedback = new Feedback();

            // Image Upload
            String storageFileName = null;

            if (dto.getProfilePic() != null && !dto.getProfilePic().isEmpty()) {

                String originalFileName = dto.getProfilePic().getOriginalFilename();
                storageFileName = UUID.randomUUID() + "_" + originalFileName;

                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = dto.getProfilePic().getInputStream()) {
                    Files.copy(inputStream,
                            uploadPath.resolve(storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // Set Data
            feedback.setOrder(order);
            feedback.setProduct(order.getProduct());
            feedback.setSeller(order.getProduct().getSeller());
            feedback.setUser(user);   // 🔥 VERY IMPORTANT

            feedback.setMessage(dto.getMessage());
            feedback.setRating(dto.getRating());
            feedback.setImages(storageFileName);

            feedback.setSubmittedAt(LocalDateTime.now());
            feedback.setActive(true);

            feedbackRepo.save(feedback);

        } catch (Exception e) {
            throw new RuntimeException("Error saving feedback: " + e.getMessage());
        }
    }
}