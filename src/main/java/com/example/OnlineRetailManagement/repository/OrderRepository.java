package com.example.OnlineRetailManagement.repository;


import com.example.OnlineRetailManagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    @Query(value = "SELECT o.id, o.cart_id, o.user_id, o.product_id, o.quantity ,o.checkout_date, o.order_status, o.payment_id, o.address_id from orders as o inner join product as p on o.product_id = p.id where p.owned_by = :userId;", nativeQuery = true)
    List<Order> findByVendorId(@Param("userId") Long userId);

    @Query(value = "SELECT count(*) FROM orders", nativeQuery = true)
    Integer findTotalCountOrders();

    @Query(value = "SELECT SUM(o.quantity * p.actual_price) AS total_revenue FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id;", nativeQuery = true)
    Integer getTotalRevenue();

    @Query(value = "SELECT SUM(o.quantity * p.discounted_price) AS total_revenue FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id;", nativeQuery = true)
    Integer getTotalRevenueDiscounted();

    @Query(value = "SELECT count(*) FROM orders where order_status = 'Processing';", nativeQuery = true)
    Integer getCountProcessing();

    @Query(value = "SELECT count(*) FROM orders where order_status = 'Confirmed';", nativeQuery = true)
    Integer getCountConfirmed();

    @Query(value = "SELECT count(*) FROM orders where order_status = 'Shipped';", nativeQuery = true)
    Integer getCountShipped();

    @Query(value = "SELECT count(*) FROM orders where order_status = 'In Transit';", nativeQuery = true)
    Integer getCountInTransit();

    @Query(value = "SELECT count(*) FROM orders where order_status = 'Out for Delivery';", nativeQuery = true)
    Integer getCountOutForDelivery();

    @Query(value = "SELECT count(*) FROM orders where order_status = 'Delivered';", nativeQuery = true)
    Integer getCountInDelivered();

    @Query(value = "SELECT count(*) FROM cart", nativeQuery = true)
    Integer findTotalCartCount();

    @Query(value = "SELECT count(distinct o.user_id) as total_users FROM orders as o inner join product as p on o.product_id = p.id where owned_by = :vendorId;", nativeQuery = true)
    Integer getCustomerCount(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT SUM(o.quantity * p.actual_price) AS total_revenue FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where p.owned_by = :vendorId;", nativeQuery = true)
    Integer getTotalRevenueVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT SUM(o.quantity * p.discounted_price) AS total_revenue FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where p.owned_by = :vendorId;", nativeQuery = true)
    Integer getTotalRevenueDiscountedVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where o.order_status = 'Processing' and p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountProcessingVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where o.order_status = 'Confirmed' and p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountConfirmedVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where o.order_status = 'Shipped' and p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountShippedVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where o.order_status = 'In Transit' and p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountInTransitVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where o.order_status = 'Out for Delivery' and p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountOutForDeliveryVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where o.order_status = 'Delivered' and p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountInDeliveredVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM cart AS o INNER JOIN product AS p ON o.product_id = p.id where p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountCartVendor(@Param("vendorId") Long vendorId);

    @Query(value = "SELECT count(*) as total_count FROM orders AS o INNER JOIN product AS p ON o.product_id = p.id where p.owned_by = :vendorId;", nativeQuery = true)
    Integer getCountOrderVendor(@Param("vendorId") Long vendorId);

}
