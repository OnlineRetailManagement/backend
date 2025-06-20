package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>  {

    @Query(value = "SELECT * FROM product LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Product> findAllProductsPaginated(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT * FROM product where owned_by= :owned_by LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Product> findAllProductsPaginatedForVendor(@Param("limit") int limit, @Param("offset") int offset,@Param("owned_by") Long ownedBy);

    @Query(value = "SELECT count(*) FROM product", nativeQuery = true)
    Integer findTotalCount();

    @Query(value = "SELECT count(*) FROM product where owned_by= :owned_by", nativeQuery = true)
    Integer findTotalCountForVendor(@Param("owned_by") Long ownedBy);
}
