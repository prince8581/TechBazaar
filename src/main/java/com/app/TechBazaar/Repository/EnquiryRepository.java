package com.app.TechBazaar.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.TechBazaar.Model.Enquiry;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

}
