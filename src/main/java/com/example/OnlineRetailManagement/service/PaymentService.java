package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.entity.Payment;
import com.example.OnlineRetailManagement.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment addPayment(Payment payment){
        return paymentRepository.save(payment);
    }

    public List<Payment> getPayments(Long userId){
        return paymentRepository.getPaymentByUserId(userId);
    }

    public void deletePayment(Long paymentId){
        paymentRepository.deleteById(paymentId);
    }

    public Payment getPayment(Long paymentId){
        return paymentRepository.findById(paymentId).orElseThrow(
                () -> new EntityNotFoundException("Payment not found with id: " + paymentId));
    }
}
