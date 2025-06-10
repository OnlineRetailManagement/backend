package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    @Query(value = "SELECT * FROM user LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findAllUsersPaginated(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT * FROM user WHERE role = 'ROLE_VENDOR' LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findAllUsersPaginatedVendors(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT count(*) FROM user", nativeQuery = true)
    Integer findTotalCount();

    @Query(value = "SELECT count(*) FROM user where role = 'ROLE_VENDOR'", nativeQuery = true)
    Integer findTotalCountVendors();

    @Query(value = "SELECT count(*) FROM user where role = 'ROLE_USER'", nativeQuery = true)
    Integer findTotalCountUsers();

}