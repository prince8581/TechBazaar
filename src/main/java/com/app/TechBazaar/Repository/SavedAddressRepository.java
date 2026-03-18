package com.app.TechBazaar.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.SavedAddress;
import com.app.TechBazaar.Model.Users;

@Repository
public interface SavedAddressRepository extends JpaRepository<SavedAddress, Long> {

	List<SavedAddress> findAllByUser(Users user);

	SavedAddress findByUserAndActive(Users user, boolean b);

	List<SavedAddress> findByUserId(long id);

	//List<SavedAddress> findByUser(Users user);

}
