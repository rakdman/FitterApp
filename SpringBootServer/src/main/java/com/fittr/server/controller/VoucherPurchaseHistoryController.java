package com.fittr.server.controller;

import com.fittr.server.model.VoucherPurchaseHistory;
import com.fittr.server.repository.VoucherPurchaseHistoryRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class VoucherPurchaseHistoryController {

    @Autowired
    VoucherPurchaseHistoryRepo voucherPurchaseHistoryRepo;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * This method to get the history of purchased vouchers by a user
     * @param userId
     * @return voucher list or message
     * @author Rakesh Kumar
     */
    @GetMapping("/getuservoucherhistory/{userId}")
    public ResponseEntity<?> getVoucher(@PathVariable int userId) {

        Map<String, List<VoucherPurchaseHistory>> response = new HashMap<>();
        List<VoucherPurchaseHistory> voucherPurchaseHistory=null;

        if (userId==0) {
            String errorMessage = "No input user!";
            log.info("Response:"+errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        try {
            voucherPurchaseHistory = voucherPurchaseHistoryRepo.findAllByUserId(userId);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        response.put("VOUCHERS", voucherPurchaseHistory);
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
