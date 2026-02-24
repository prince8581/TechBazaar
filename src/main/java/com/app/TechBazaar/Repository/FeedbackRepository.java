package com.app.TechBazaar.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
