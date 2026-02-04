package com.app.TechBazaar.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.DTO.EnquiryDTO;
import com.app.TechBazaar.Model.Enquiry;
import com.app.TechBazaar.Repository.EnquiryRepository;

@Service
public class EnquiryService {

	@Autowired
	private EnquiryRepository enquiryRepo;
	
	
	
	public void saveEnquiry(EnquiryDTO dto) {
		Enquiry enquiry=new Enquiry();
		enquiry.setName(dto.getName());
		enquiry.setContactNo(dto.getContactNo());
		enquiry.setEmail(dto.getEmail());
		enquiry.setTitle(dto.getTitle());
		enquiry.setMessage(dto.getMessage());
		enquiry.setEnquiryDate(LocalDateTime.now());
		
		enquiryRepo.save(enquiry);
	}
}
