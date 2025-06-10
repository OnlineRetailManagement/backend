package com.example.OnlineRetailManagement.controller;

import com.example.OnlineRetailManagement.DTO.AttachmentRequestDTO;
import com.example.OnlineRetailManagement.DTO.AttachmentResponseDTO;
import com.example.OnlineRetailManagement.entity.*;
import com.example.OnlineRetailManagement.service.*;
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
import java.util.Optional;

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

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

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

    @PostMapping("address")
    public ResponseEntity<?> addAddress(@RequestBody Address address) {
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Address addressResponse = addressService.saveAddress(address);
            generalResponse.setMsg("Address added Successfully !!");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, Long> addressId = new HashMap<>();
            addressId.put("id", addressResponse.getId());
            generalResponse.setData(addressId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Address not added successfully !!");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("address/{id}")
    public ResponseEntity<?> getAddress(@PathVariable("id") Long userId) {
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Address> addressesResponse = addressService.getAdressesByUserId(userId);
            generalResponse.setMsg("Addresses fetched Successfully !!");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, List<Address>> addresses = new HashMap<>();
            addresses.put("addresses", addressesResponse);
            generalResponse.setData(addresses);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Address were not Fetched");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable("id") Long addressId, @RequestBody Address address){
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            address.setId(addressId);
            Address addressResponse =  addressService.saveAddress(address);
            generalResponse.setMsg("Address was updated");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, Address> addressMap = new HashMap<>();
            addressMap.put("updated_address", addressResponse);
            generalResponse.setData(addressMap);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Address were not updated");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("address/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") Long addressId){
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            addressService.deleteAddress(addressId);
            generalResponse.setMsg("Address was Deleted");
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Address was not deleted");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("payment")
    public ResponseEntity<?> addPayment(@RequestBody Payment payment) {
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Payment  paymentResponse = paymentService.addPayment(payment);
            generalResponse.setMsg("Payment added Successfully !!");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, Long> paymentId = new HashMap<>();
            paymentId.put("id", paymentResponse.getId());
            generalResponse.setData(paymentId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Payment not added successfully !!");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("payment/{id}")
    public ResponseEntity<?> getPayment(@PathVariable("id") Long userId) {
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Payment> paymentResponse = paymentService.getPayments(userId);
            generalResponse.setMsg("Payments fetched Successfully !!");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, List<Payment>> addresses = new HashMap<>();
            addresses.put("payments", paymentResponse);
            generalResponse.setData(addresses);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Address were not Fetched");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("payment/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable("id") Long paymentId, @RequestBody Payment payment){
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            payment.setId(paymentId);
            Payment paymentResponse =  paymentService.addPayment(payment);
            generalResponse.setMsg("Payment was updated");
            generalResponse.setCode(HttpStatus.OK.value());
            HashMap<String, Payment> paymentMap = new HashMap<>();
            paymentMap.put("updated_payment", paymentResponse);
            generalResponse.setData(paymentMap);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Payment was not updated");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("payment/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable("id") Long paymentId){
        GeneralResponse generalResponse = new GeneralResponse();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            paymentService.deletePayment(paymentId);
            generalResponse.setMsg("Payment was Deleted");
            generalResponse.setCode(HttpStatus.OK.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        } catch (Exception e) {
            generalResponse.setMsg("Payment was not deleted");
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<GeneralResponse> getSingleUsers(@PathVariable("id") Long userId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        GeneralResponse generalResponse = new GeneralResponse();
        try{

            HashMap<String, Object> data = new HashMap<>();
            Optional<User> user = userService.findById(userId);
            data.put("user", user);
            generalResponse.setData(data);
            Integer statusCode = HttpStatus.OK.value();
            generalResponse.setCode(statusCode);
            generalResponse.setMsg("User with id : "+userId);
            return new ResponseEntity<>(generalResponse, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e);

            generalResponse.setMsg(String.valueOf(e));
            generalResponse.setCode(HttpStatus.BAD_REQUEST.value());

            return new ResponseEntity<>(generalResponse, HttpStatus.BAD_REQUEST);
        }
    }

}
