package com.example.OnlineRetailManagement.repository;

import com.example.OnlineRetailManagement.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByProductId(Long productId);
}
