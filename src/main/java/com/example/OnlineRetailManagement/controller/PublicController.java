package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.DTO.AttachmentRequestDTO;
import com.example.OnlineRetailManagement.DTO.AttachmentResponseDTO;
import com.example.OnlineRetailManagement.entity.Attachment;
import com.example.OnlineRetailManagement.entity.GeneralResponse;
import com.example.OnlineRetailManagement.entity.User;
import com.example.OnlineRetailManagement.service.AttachmentService;
import com.example.OnlineRetailManagement.service.UserDetailsServiceImpl;
import com.example.OnlineRetailManagement.service.UserService;
import com.example.OnlineRetailManagement.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/public")
public class PublicController {

    private static final String BASE_DIRECTORY = "../uploads";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("/health-check")
    public GeneralResponse healthCheck() {
        GeneralResponse generalResponse = new GeneralResponse();
        Integer statusCode = HttpStatus.OK.value();
        generalResponse.setCode(statusCode);
        generalResponse.setMsg("ALLES GUTE!");
        return generalResponse;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        log.info("/signup");
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            User savedUser = (User) userService.saveNewUser(user);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            User userDetail = userService.findByEmail(userDetails.getUsername());
            userDetail.setPassword(null);
            HashMap<String, Object> data = new HashMap<>();
            data.put("jwt", jwt);
            data.put("email", userDetails.getUsername());
            data.put("role", userDetails.getAuthorities());
            data.put("accountNonExpired", userDetails.isAccountNonExpired());
            data.put("accountNonLocked", userDetails.isAccountNonLocked());
            data.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            data.put("enabled", userDetails.isEnabled());
            data.put("user", userDetail);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Signup Successful!");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg("Bad Credentials");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            User userDetail = userService.findByEmail(userDetails.getUsername());
            userDetail.setPassword(null);
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            HashMap<String, Object> data = new HashMap<>();
            data.put("jwt", jwt);
            data.put("email", userDetails.getUsername());
            data.put("role", userDetails.getAuthorities());
            data.put("accountNonExpired", userDetails.isAccountNonExpired());
            data.put("accountNonLocked", userDetails.isAccountNonLocked());
            data.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            data.put("enabled", userDetails.isEnabled());
            data.put("user", userDetail);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("Login Successful!");
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("profile/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @RequestBody User updatedUser) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email =  authentication.getName();
            User currentUser = userService.findByEmail(email);
            if(currentUser.getRole().equals("ROLE_USER") || currentUser.getRole().equals("ROLE_VENDOR")){
                if(currentUser.getId() == userId){
                    updatedUser.setId(userId);
                    userService.saveNewUser(updatedUser);
                    generalResponse.setMsg("User Updated");
                }
                else{
                    generalResponse.setMsg("User is not authorized to update other users");
                }
            }
            else{
                updatedUser.setId(userId);
                userService.saveNewUser(updatedUser);
                generalResponse.setMsg("User Updated");
            }
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestBody Attachment attachment) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            if (attachment.getContent() == null || attachment.getFileName() == null || attachment.getFileType() == null || attachment.getCategory() == null) {
                generalResponse.setMsg("Missing required fields");
                generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
            }

            byte[] fileData = Base64.getDecoder().decode(attachment.getContent());

            String baseDir = "uploads";
            String category = attachment.getCategory();
            String fileNameWithExt = attachment.getFileName() + "." + attachment.getFileType();

            Path uploadPath = Paths.get(baseDir, category, fileNameWithExt)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(uploadPath.getParent());

            try (FileOutputStream fos = new FileOutputStream(uploadPath.toFile())) {
                fos.write(fileData);
            }
            attachment.setAttachmentPath(String.valueOf(uploadPath.toFile()));
            attachment.setContent(null);
            attachmentService.saveAttachment(attachment);

            generalResponse.setMsg("File uploaded successfully");
            HashMap<String, Object> data = new HashMap<>();
            data.put("attachment", attachment);
            generalResponse.setData(data);
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/attachments")
    public ResponseEntity<?> uploadAttachments(@RequestParam("file") MultipartFile file, @RequestParam("user_id") String userId) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            if (file==null || userId==null) {
                generalResponse.setMsg("Missing required fields");
                generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
            }

            AttachmentResponseDTO responseDTO=attachmentService.saveAttachment(AttachmentRequestDTO.builder().file(file).userId(userId).build());

            generalResponse.setMsg("File uploaded successfully");
            HashMap<String, Object> data = new HashMap<>();
            data.put("attachment", responseDTO);
            generalResponse.setData(data);
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/attachments")
    public ResponseEntity<?> getAttachments(@RequestParam("user_id") Long userId,@RequestParam("product_id") Long productId) {
        GeneralResponse generalResponse = new GeneralResponse();
        try{
            if (userId==null || productId==null) {
                generalResponse.setMsg("Missing required fields");
                generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
            }
            List<Attachment> attachments=attachmentService.getAttachmentsByProductId(productId);

            generalResponse.setMsg("File uploaded successfully");
            HashMap<String, Object> data = new HashMap<>();
            data.put("attachment", attachments);
            generalResponse.setData(data);
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
