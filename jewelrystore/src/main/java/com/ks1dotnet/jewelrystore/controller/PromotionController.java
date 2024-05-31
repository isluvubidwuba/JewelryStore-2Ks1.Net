package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

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
import com.ks1dotnet.jewelrystore.entity.Promotion;
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

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        try {
            ResponseData responseData = new ResponseData();
            responseData.setData(iPromotionService.findAll());
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/getHomePagePromotion")
    private ResponseEntity<?> getHomePagePromotion(@RequestParam int page) {
        try {
            ResponseData responseData = new ResponseData();
            responseData.setData(iPromotionService.getHomePagePromotion2(page));
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/update")
    private ResponseEntity<?> update(@RequestParam MultipartFile file, @RequestParam int id,
            @RequestParam String name, @RequestParam int idVoucherType,
            @RequestParam double value, @RequestParam boolean status) {
        try {
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO = iPromotionService.updatePromotion(file, id, name, idVoucherType, value, status);

            if (promotionDTO != null) {
                responseData.setDesc("Update successful");
                responseData.setData(promotionDTO);
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            } else {
                responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                responseData.setDesc("Update failed. Internal Server Error");
                return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
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

    // @GetMapping("/delete/{id}")
    // private ResponseEntity<?> delete(@PathVariable int id) {
    // try {
    // ResponseData responseData = new ResponseData();
    // Promotion promotion = iPromotionService.findById(id);
    // promotion.setStatus(false);
    // Promotion updatedPromotion =
    // iPromotionService.saveOrUpdatePromotion(promotion);
    // responseData.setDesc("Delete successful");
    // responseData.setData(updatedPromotion);
    // return new ResponseEntity<>(responseData, HttpStatus.OK);
    // } catch (BadRequestException e) {
    // return handleBadRequestException(e);
    // } catch (Exception e) {
    // return handleException(e);
    // }
    // }

    @GetMapping("/search/{name}")
    private ResponseEntity<?> search(@PathVariable String name) {
        try {
            ResponseData responseData = new ResponseData();
            List<Promotion> listPromotions = iPromotionService.searchByName(name);
            responseData.setDesc("Search successful");
            responseData.setData(listPromotions);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            return handleBadRequestException(e);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/create")
    private ResponseEntity<?> create(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String name,
            @RequestParam int idVoucherType,
            @RequestParam double value,
            @RequestParam boolean status) {
        try {
            ResponseData responseData = iPromotionService.insertPromotion(file, name, idVoucherType, value, status);

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

    private ResponseEntity<ResponseData> handleBadRequestException(BadRequestException e) {
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.BAD_REQUEST);
        responseData.setDesc("Bad request: " + e.getErrorString());
        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ResponseData> handleException(Exception e) {
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        responseData.setDesc("An error occurred: " + e.getMessage());
        return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
