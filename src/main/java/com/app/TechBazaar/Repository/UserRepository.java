package com.app.TechBazaar.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Model.Users.UserRole;
import com.app.TechBazaar.Model.Users.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

	Users findByEmail(String email);

	boolean existsByEmailAndIsVerified(String email, boolean b);

	List<Users> findAllByUserRoleAndIsVerifiedAndUserStatusNot(UserRole seller, boolean b, UserStatus status);





}
