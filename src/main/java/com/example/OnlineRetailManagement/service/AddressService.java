package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.entity.Address;
import com.example.OnlineRetailManagement.entity.Cart;
import com.example.OnlineRetailManagement.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    public List<Address> getAdressesByUserId(Long userId){
        return addressRepository.getAddressesByUserId(userId);
    }
}
