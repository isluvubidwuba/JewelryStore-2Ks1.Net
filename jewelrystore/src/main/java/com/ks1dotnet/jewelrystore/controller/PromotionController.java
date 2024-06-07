package com.ks1dotnet.jewelrystore.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IFileService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;

@RestController
@RequestMapping("/promotion")
@CrossOrigin("*")
public class PromotionController {

    @Autowired
    private IPromotionService iPromotionService;
    @Autowired
    private IFileService iFileService;

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String name,
            @RequestParam double value,
            @RequestParam boolean status,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String promotionType) { // Thêm promotionType vào đây
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            ResponseData responseData = iPromotionService.insertPromotion(file, name, value, status, start, end,
                    promotionType);
            if (responseData.getStatus() == HttpStatus.OK) {
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(
            @RequestParam MultipartFile file,
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam double value,
            @RequestParam boolean status,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO = iPromotionService.updatePromotion(file, id, name, value, status, start, end);
            responseData.setDesc("Update successful");
            responseData.setData(promotionDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO = iPromotionService.findById(id);
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + id);
            }
            iPromotionService.deletePromotion(id);
            responseData.setDesc("Delete successful");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            ResponseData responseData = new ResponseData();
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc(e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData();
            responseData.setStatus(HttpStatus.BAD_REQUEST);
            responseData.setDesc(e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData();
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getById")
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO = iPromotionService.findById(id);
            responseData.setDesc("Find successful");
            responseData.setData(promotionDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            ResponseData responseData = new ResponseData();
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc(e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/getHomePagePromotion")
    public ResponseEntity<?> getHomePagePromotion(@RequestParam int page) {
        iPromotionService.deleteExpiredPromotions();
        try {
            ResponseData responseData = new ResponseData();
            responseData.setData(iPromotionService.getHomePagePromotion(page));
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        try {
            Resource resource = iFileService.loadFile(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.BAD_REQUEST);
        responseData.setDesc(e.getMessage());
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> handleException(Exception e) {
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        responseData.setDesc("An unexpected error occurred: " + e.getMessage());
        return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
