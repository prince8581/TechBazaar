package com.app.TechBazaar.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.TechBazaar.DTO.SavedAddressDTO;
import com.app.TechBazaar.Model.SavedAddress;
import com.app.TechBazaar.Model.Users;
import com.app.TechBazaar.Repository.SavedAddressRepository;

@Service
public class SavedAddressService {

	@Autowired
	private SavedAddressRepository addressRepo;
	
	public void saveNewAddress(SavedAddressDTO dto, Users user) {
		
		SavedAddress newAddress = new SavedAddress();
		newAddress.setName(dto.getName());
        newAddress.setContactNo(dto.getContactNo());
        newAddress.setPincode(dto.getPincode());
        newAddress.setLocality(dto.getLocality());
        newAddress.setAddress(dto.getAddress());
        newAddress.setCityDistrict(dto.getCityDistrict());
        newAddress.setState(dto.getState());
        newAddress.setLandmark(dto.getLandmark());
        newAddress.setAltContactNo(dto.getAltContactNo());
        newAddress.setAddressType(dto.getAddressType());
        newAddress.setUser(user);
        newAddress.setAddedDate(LocalDateTime.now());
        
        addressRepo.save(newAddress);
        
        changeAddress(user, newAddress.getId());
        
	}
	
	
	//change address for only login this user
	public  void changeAddress(Users user, long id) {
		
		List<SavedAddress> addresses = addressRepo.findAllByUser(user);
	    for(SavedAddress add : addresses) {
	    	if(add.getId()==id) 
	    		add.setActive(true);
	    	
	    	else 
	    		add.setActive(false);
	    	
	    	addressRepo.save(add);
	    	
	    }
	}
	
	// ===== Fetch Address by ID (for Edit) =====
    public SavedAddress getAddressById(Long id) {
        return addressRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with ID: " + id));
    }
	
    //update existing address
	public void editAddress(SavedAddressDTO dto) 
	{
		
		SavedAddress address=addressRepo.findById(dto.getId()).orElseThrow(()-> new RuntimeException("Address not Found"));
		
		address.setName(dto.getName());
		address.setContactNo(dto.getContactNo());
		address.setPincode(dto.getPincode());
		address.setLocality(dto.getLocality());
		address.setAddress(dto.getAddress());
		address.setCityDistrict(dto.getCityDistrict());
		address.setState(dto.getState());
		address.setLandmark(dto.getLandmark());
		address.setAltContactNo(dto.getAltContactNo());
		address.setAddressType(dto.getAddressType());
		
		addressRepo.save(address);
		
	}
	
	public void deleteAddress(Long id) {
		if(addressRepo.existsById(id)) {
			addressRepo.deleteById(id);
		}
		else {
			throw new RuntimeException("Address not found with id: "+id);
		}
	}
}
