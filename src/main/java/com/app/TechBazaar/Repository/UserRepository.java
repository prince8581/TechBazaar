package com.app.TechBazaar.Repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

	Users findByEmail(String email);

	boolean existsByEmailAndIsVerified(String email, boolean b);





}
