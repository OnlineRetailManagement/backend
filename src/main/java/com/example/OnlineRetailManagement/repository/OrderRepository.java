package com.example.OnlineRetailManagement.repository;


import com.example.OnlineRetailManagement.entity.Order;
import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query(value = "SELECT o.id, o.cart_id, o.user_id, o.product_id, o.quantity ,o.checkout_date, o.order_status, o.payment_id, o.address_id from orders as o inner join product as p on o.product_id = p.id where p.owned_by = :userId and o.order_status != 'Delivered';", nativeQuery = true)
    List<Order> findByVendorId(@Param("userId") Long userId);

    @Query(value = "SELECT count(*) FROM order", nativeQuery = true)
    Integer findTotalCountOrders();

}
