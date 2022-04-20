package com.fittr.server.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fittr.server.model.Users;
import com.fittr.server.model.VoucherPurchaseHistory;
import com.fittr.server.model.Vouchers;
import com.fittr.server.repository.UserRepo;
import com.fittr.server.repository.VoucherPurchaseHistoryRepo;
import com.fittr.server.repository.VoucherRepo;
import com.fittr.server.request.ChangePassword;
import com.fittr.server.request.GetImage;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class UserController {

    @Autowired
    UserRepo userrepo;

    @Autowired
    VoucherRepo voucherrepo;

    @Autowired
    VoucherPurchaseHistoryRepo voucherPurchaseHistoryRepo;

    PasswordEncoder passwordEncoder;
    Logger log = LoggerFactory.logger(this.getClass());


    /**
     * This method get the list of all users already registered
     *
     * @return List of users
     * @author Rakesh Kumar
     */
    @GetMapping(value = "/getallusers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers() {

        Map<String, List<Users>> response = new HashMap<>();

        List<Users> users = userrepo.findAll();
        if (users.isEmpty()) {
            String errorMessage = "No user exist!";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        log.info("Response:"+users);
        response.put("USERS", users);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }

    /**
     * This method return one user
     *
     * @param userId: user id of the user
     * @return user from the db
     * @author Rakesh Kumar
     */
    @GetMapping(value = "/getoneuser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable int userId) {

        Map<String, Users> response = new HashMap<>();

        Optional<Users> user = userrepo.findById(userId);

        if (!user.isPresent()) {
            String errorMessage = "No such user exists!";
            log.info("Response:"+errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        Users userData = user.get();

        log.info("Response:"+userData);
        response.put("VOUCHER", userData);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /**
     * This method register the user in users table
     *
     * @param user details fullName, email, password
     * @return user from the db
     * @throws ConstraintViolationException, Exception
     * @author Rakesh Kumar
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postUser(@RequestBody Users user) {

        Map<String, Integer> response = new HashMap<>();

        //This block will encrypt the provided password
        try {
            passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userrepo.save(user);
            response.put("userId", user.getUserId());
            log.info("Response:"+response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException e) {
            if ((e.getCause()).toString().contains("ConstraintViolationException")) {
                log.error("Response:"+e.getCause());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DataIntegrityViolationException: User already exists!");
            } else {
                log.error("Response:"+e.getCause());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DataIntegrityViolationException: fullName,email,password,roles are null!");
            }
        } catch (Exception e) {
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    /**
     * This method delete the user
     *
     * @param userId - Id of the user
     * @return status of the request
     * @throws IllegalArgumentException - exception ,Exception
     * @author Rakesh Kumar
     */
    @DeleteMapping(value = "/deleteuser/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {

        Map<String, String> response = new HashMap<>();

        Optional<Users> user = userrepo.findById(userId);

        if (!user.isPresent()) {
            String errorMessage = "No such user exists!";
            log.info("Response:"+errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }
        try {
            userrepo.deleteById(userId);
        } catch (IllegalArgumentException e) {
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Illegal input argument");
        } catch (Exception e) {
            response.put("status", "Failure");
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }


        response.put("status", "Success");
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    /**
     * This method compare the provided password with encrypted password in db
     *
     * @param user with username and password
     * @return userId or error message
     * @author Rakesh Kumar
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Users user) {

        Map<String, Integer> response = new HashMap<>();

        if (user.getEmail().isEmpty()) {
            log.info("Response:"+"User field is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User field is null");
        } else if (user.getPassword().isEmpty()) {
            log.info("Response:"+"Password field is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password field is null");
        }

        Optional<Users> userData = userrepo.findByEmail(user.getEmail());

        if (!userData.isPresent()) {
            log.info("Response:"+"User does not exists");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exists");
        }

        //This section will compare the input and stored password in database
        try {
            passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(user.getPassword(), userData.get().getPassword())) {
                response.put("userId", userData.get().getUserId());
                log.info("Response:"+response);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        log.info("Response:"+"Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }


    /**
     * This method update the details of user
     *
     * @param inputuser - New Password
     * @param userId - Id of the user
     * @return userId in case success else message
     * @author Rakesh Kumar
     */
    @PatchMapping(value = "/updateuser/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateuser(@RequestBody Users inputuser, @PathVariable int userId) {

        Map<String, Integer> response = new HashMap<>();

        Optional<Users> user = userrepo.findById(userId);

        if (!user.isPresent()) {
            log.info("Response:"+"User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (user.get().getEmail().isEmpty()) {
            log.info("Response:"+"User field is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User field is null");
        } else if (user.get().getPassword().isEmpty()) {
            log.info("Response:"+"Password field is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password field is null");
        }

        //This block will encode the new password and save in db
        try {
            passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(inputuser.getPassword());
            user.get().setPassword(encodedPassword);
            userrepo.save(user.get());

            response.put("userId", user.get().getUserId());
            log.info("Response:"+response);

            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.info("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    /**
     * This method change the password to newPassword when current password is correct
     *
     * @param inputuser - User json object
     * @param userId - Id of the user
     * @return userId or message
     * @author Rakesh Kumar
     */
    @PatchMapping(value = "/updateuser2/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateuser2(@RequestBody ChangePassword inputuser, @PathVariable int userId) {

        Map<String, Integer> response = new HashMap<>();

        Optional<Users> user = userrepo.findById(userId);


        if (!user.isPresent()) {
            log.info("Response:"+"User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Users userData = user.get();

        if (inputuser.getCurrentPassword().isEmpty()) {
            log.info("Response:"+"Current Password field is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current Password field is null");
        } else if (inputuser.getNewPassword().isEmpty()) {
            log.info("Response:"+"New Password field is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("New Password field is null");
        }

        try {
            passwordEncoder = new BCryptPasswordEncoder();

            if (!passwordEncoder.matches(inputuser.getCurrentPassword(), userData.getPassword())) {
                log.info("Response:"+"Incorrect current password");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incorrect current password");
            }

            //Setting up new password in db
            String encodedPassword = passwordEncoder.encode(inputuser.getNewPassword());
            userData.setPassword(encodedPassword);
            userrepo.save(userData);

           response.put("userId", userData.getUserId());

            log.info("Response:"+response);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (Exception e) {
            log.info("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }


    /**
     * In this method user purchase voucher
     *
     * @param objectNode (voucherId)
     * @param userId - Id of the user
     * @return success message
     * @author Rakesh Kumar
     */
    @PostMapping(value = "/purchasevoucher/{userId}")
    public ResponseEntity<?> purchaseVoucher(@RequestBody ObjectNode objectNode, @PathVariable int userId) {

        int voucherId;
        Users userData;
        Optional<Vouchers> voucher;
        Vouchers voucherData;
        Map<String, String> response = new HashMap<>();

        Optional<Users> user = userrepo.findById(userId);

        if (!user.isPresent()) {
            log.info("Response:"+"User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }




        try {
            voucherId = objectNode.get("voucherId").asInt();
            log.info("Voucher id:"+voucherId);
            if(voucherId==0)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher does not exist");
            voucher = voucherrepo.findById(voucherId);

            voucherData = voucher.get();

            if (voucherData.equals(null)) {
                String errorMessage = "No mentioned voucher exists!";
                log.info("Response:"+errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
            }


            if (voucher.equals(null)||voucher.equals(""))
            {
                log.error("Response:"+"Voucher does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher does not exist");
            }
        } catch(EntityNotFoundException e) {
            log.error("Response:"+"Voucher does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher does not exist");
        }
        catch (Exception e) {
            log.error("Response:"+"Voucher does not exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher does not exist");
        }

        userData = (Users) user.get();

        log.info("Total coins of the user :" + userData.getTotalCoins());
        log.info("Voucher coins:" + voucherData.getVoucherCoins());

        if (userData.getTotalCoins() < voucherData.getVoucherCoins()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not have enough coins!");
        }

        userData.setTotalCoins(userData.getTotalCoins() - voucherData.getVoucherCoins());

        log.info("TotalCoins of the user after purchase:" + userData.getTotalCoins());

        userrepo.save(userData);


        //This section to insert the voucher history into voucherPurchaseHistory table before deletion of voucher
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        Date currentDate = new Date();

        VoucherPurchaseHistory voucherPurchaseHistory = new VoucherPurchaseHistory();
        voucherPurchaseHistory.setVoucherId(voucherData.getVoucherId());
        voucherPurchaseHistory.setPurchaseDate(currentDate); //PurchaseDate
        voucherPurchaseHistory.setUserId(userData.getUserId());
        voucherPurchaseHistory.setVoucherCoins(voucherData.getVoucherCoins());
        voucherPurchaseHistory.setVoucherDesc(voucherData.getVoucherDesc());
        voucherPurchaseHistory.setVoucherTitle(voucherData.getVoucherTitle());
        voucherPurchaseHistory.setVoucherImage(voucherData.getVoucherImage());

        voucherPurchaseHistoryRepo.save(voucherPurchaseHistory);

        voucherrepo.deleteById(voucherId);
        log.info("Voucher deleted from voucher table");
        response.put("status", "Success");
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * This method receives base64 image and store it in database
     *
     * @param image - base64 image string
     * @param userId- Id of the user
     * @return userId if success, else message
     * @author Rakesh Kumar
     */
    @PatchMapping(value = "/uploadimage/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadimage(@RequestBody GetImage image, @PathVariable int userId) {

        String userImage;
        Optional<Users> user = userrepo.findById(userId);

        if (image.equals(null) || image.equals("")) {
            log.info("Response:"+"No image data");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No image data");
        }

        // Checking if issue with image data
        try {
            userImage = image.getImage();
        } catch (Exception e) {
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Input image has error" + e.getMessage());
        }

        if (!user.isPresent()) {
            log.info("Response:"+"User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

//      Saving image in database
        Users userData = user.get();
        userData.setUserImage(userImage);
        try {
            userrepo.save(userData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        Map<String, Integer> response = new HashMap<>();
        response.put("userId", user.get().getUserId());
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
