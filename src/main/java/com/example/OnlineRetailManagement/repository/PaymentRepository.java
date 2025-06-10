package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(value = "SELECT * FROM payment WHERE user_id = :userid", nativeQuery = true)
    List<Payment> getPaymentByUserId(@Param("userid") long userid);
}
