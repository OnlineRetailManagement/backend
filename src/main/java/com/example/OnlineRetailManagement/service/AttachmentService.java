package com.example.OnlineRetailManagement.service;

import com.example.OnlineRetailManagement.DTO.AttachmentRequestDTO;
import com.example.OnlineRetailManagement.DTO.AttachmentResponseDTO;
import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.repository.AttachmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    public void saveAttachment(Attachment attachment) {
        attachmentRepository.save(attachment);
    }

    public AttachmentResponseDTO saveAttachment(AttachmentRequestDTO requestDTO){
        try {
            // Ensure folder exists
            Path uploadPath = Paths.get("../attachments").toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate file name and target path
            String originalFilename = requestDTO.getFile().getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path targetPath = uploadPath.resolve(filename).normalize();
//            Path targetPath = uploadPath.resolve(filename).toAbsolutePath();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }

            // Copy file to folder
            Files.copy(requestDTO.getFile().getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            Attachment newAttachment=Attachment.builder()
                    .attachmentPath(targetPath.toString())
                    .fileName(filename).fileType(extension)
                    .createdAt(LocalDateTime.now())
                    .build();

            Attachment savedAttachment=attachmentRepository.save(newAttachment);

            AttachmentResponseDTO responseDTO= AttachmentResponseDTO.builder()
                    .id(savedAttachment.getId())
                    .path(savedAttachment.getAttachmentPath())
                    .build();

            return responseDTO;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Attachment getAttachmentById(Long id) {
        return attachmentRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Attachment not found with id: " + id));
    }

    public List<Attachment> getAttachmentsByProductId(Long productId) {
        return attachmentRepository.findByProductId(productId);

    }

    public InputStreamResource getAttachmentsOfProductId(String fileName) throws FileNotFoundException {
        File imageFile = new File("../attachments/"+fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(imageFile));

        return resource;
    }

//    private MultipartFile getFileOfName(String fileName) {
//        try {
//            // Adjust path as needed; assuming files are stored in a specific directory
//            String filePath = "C:/your-folder-path/" + fileName;
//            File file = new File(filePath);
//
//            if (!file.exists()) {
//                throw new RuntimeException("File not found: " + filePath);
//            }
//
//            FileInputStream input = new FileInputStream(file);
//            MultipartFile multipartFile = new MockMultipartFile(
//                    file.getName(),                   // original file name
//                    file.getName(),                   // name to be used in request
//                    "application/octet-stream",       // content type (or derive from fileName if needed)
//                    input);
//
//            return multipartFile;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read file: " + fileName, e);
//        }
//    }
}
