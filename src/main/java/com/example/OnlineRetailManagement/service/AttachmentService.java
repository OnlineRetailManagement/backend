package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    public void saveAttachment(Attachment attachment) {
        attachmentRepository.save(attachment);
    }
}
