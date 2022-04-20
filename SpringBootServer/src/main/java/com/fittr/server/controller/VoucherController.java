package com.fittr.server.controller;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fittr.server.request.GetImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.fittr.server.model.Vouchers;
import com.fittr.server.repository.VoucherRepo;
import org.springframework.http.ResponseEntity;


import javax.servlet.http.HttpServletRequest;

@RestController
public class VoucherController {

    @Autowired
    VoucherRepo voucherrepo;

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * This method return all vouchers
     * @return vouchers
     * @throws JsonProcessingException
     * @author Rakesh Kumar
     */
    @GetMapping(value = "/getallvouchers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllVouchers() throws JsonProcessingException {
        List<Vouchers> vouchers = voucherrepo.findAll();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<Vouchers>> response = new HashMap<>();
        response.put("VOUCHERS", vouchers);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * This method provide one voucher detail
     *
     * @param voucherId
     * @return voucher detail or message
     * @throws IOException
     * @author Rakesh Kumar
     *
     */
    @GetMapping(value="/getonevoucher/{voucherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getVoucher(@PathVariable int voucherId) throws IOException {

        Map<String, Vouchers> response = new HashMap<>();
        Optional<Vouchers> voucher = voucherrepo.findById(voucherId);

        if (!voucher.isPresent()) {
            String errorMessage = "No such voucher exists!";
            log.info("Response:"+errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        Vouchers voucherData = voucher.get();
        response.put("VOUCHER", voucherData);
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


    /**
     * This method create new vochers
     *
     * @param voucher
     * @return voucherId or message
     *@author Rakesh Kumar
     *
     */
    @PostMapping(value = "/createvoucher", consumes = {"application/json"})
    public ResponseEntity<?> postVoucher(@RequestBody Vouchers voucher) {
        try {
            voucherrepo.save(voucher);
            log.info("Response:"+"Voucher created!");
        } catch (DataIntegrityViolationException e) {
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Response:"+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(voucher);
    }

    /**
     * This method delete voucher
     *
     * @param voucherId
     * @return message
     * @author Rakesh Kumar
     */
    @DeleteMapping(value="/deletevoucher/{voucherId}")
    public String deleteVoucher(@PathVariable int voucherId) {

        if (voucherId==0) {
            log.info("Response:"+voucherId);
            return "Voucher id not provided";
        }

        try {
            voucherrepo.deleteById(voucherId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Response:"+"Voucher not found!");
            return "Voucher not found!";
        }
        log.info("Response:"+"Success");
        return "Success";
    }


    /**
     * This method is to upload base64 image for the voucher
     * @param image
     * @param voucherId
     * @return voucher id in case of success else message
     * @throws Exception
     * @author Rakesh Kumar
     *
     */
    @PatchMapping("/uploadvoucherimage/{voucherId}")
    public ResponseEntity<?> uploadvoucherimage(@RequestBody GetImage image, @PathVariable int voucherId) throws Exception {

        Optional<Vouchers> voucher = voucherrepo.findById(voucherId);

        if (!voucher.isPresent()) {
            String errorMessage = "No such voucher exists!";
            log.info("Response:"+errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        String userImage = image.getImage();

        if (userImage.equals(null)||userImage.equals("")) {
            String errorMessage = "No image data provided!";
            log.info("Response:"+errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
        }

        Vouchers voucherData = voucher.get();

        try {
            voucherData.setVoucherImage(userImage);
            voucherrepo.save(voucherData);
            log.info("Response:"+"File uploaded successfully to db!");

        } catch (Exception e) {
            log.error("Response:"+e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        Map<String, Integer> response = new HashMap<>();
        response.put("status", voucherData.getVoucherId());
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.CREATED).body (voucherData.getVoucherId());
    }


    //This endpoint to

    /**
     * This method to upload the voucher records from a CSV input file provided in input/voucherFile.csv
     * Usage:
     *  User is put the records in the mentioned file at mentioned path on server and call the endpoint.
     *  In case of success or error it will show the message on the api response.
     *  In case of data error user has to correct the input data in the file and call the endpoint again.
     *
     *
     * @return message
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     * @throws JobParametersInvalidException
     * @author Rakesh Kumar
     */
    @GetMapping(value="/loadvoucherfile")
    public ResponseEntity<?> loadvoucherfile() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

        Map<String, JobParameter> maps = new HashMap<>();
        Map<String, String> response = new HashMap<>();

        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);

        log.info("\nUsage:\n" +
                "The CSV file should be available on server at relative path: server/input/voucherFile.csv\n"+
                "The content of the file should be \"voucherTitle\",\"voucherDesc\",\"voucherCoins\" \n"+
                "The first line should be heading as mentioned above.");

        JobExecution jobExecution = jobLauncher.run(job, parameters);

        log.info("Response:"+"Voucher Upload batch job Started");

        while (jobExecution.isRunning()) {
            log.info("...");
        }
        if (jobExecution.getStatus() != null && !jobExecution.getStatus().toString().equals("COMPLETED")) {
            log.error("Job failed, please correct the input data and re-run the job.");
            jobExecution.getFailureExceptions();
        }

        log.info("Response:"+"Voucher Upload batch job Completed!");

        response.put("jobStatus", jobExecution.getStatus().toString());
        log.info("Response:"+response);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

}
