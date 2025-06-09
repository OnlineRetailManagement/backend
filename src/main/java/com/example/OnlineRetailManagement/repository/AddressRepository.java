package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(value = "SELECT * FROM address WHERE user_id = :userid", nativeQuery = true)
    List<Address> getAddressesByUserId(@Param("userid") long userid);
}
