package com.ks1dotnet.jewelrystore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.payload.responseData;
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
        responseData responseData = new responseData();
        responseData.setData(iPromotionService.findAll());
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/getHomePagePromotion")
    private ResponseEntity<?> getHomePagePromotion(@RequestParam int page) {
        responseData responseData = new responseData();
        responseData.setData(iPromotionService.getHomePagePromotion2(page));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/update")
    private ResponseEntity<?> update(@RequestParam MultipartFile file, @RequestParam int id,
            @RequestParam String name, @RequestParam int idVoucherType,
            @RequestParam double value, @RequestParam boolean status) {
        responseData responseData = new responseData();
        Map<String, Object> data = new HashMap<>();

        boolean isSuccess = iPromotionService.updatePromotion(file, id, name, idVoucherType, value, status);

        if (isSuccess) {
            data.put("id", id);
            data.put("image", file.getOriginalFilename());
            responseData.setDesc("Update successful");
            responseData.setData(data);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setStatus(500);
            responseData.setDesc("Update failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getById")
    private ResponseEntity<?> getById(@RequestParam int id) {
        responseData responseData = new responseData();
        Promotion promotion = iPromotionService.findById(id);
        if (promotion != null) {
            responseData.setDesc("Find successfull ");
            responseData.setData(promotion.getDTO());
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setStatus(500);
            responseData.setDesc("Find fail. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/delete/{id}")
    private ResponseEntity<?> update(@PathVariable int id) {
        responseData responseData = new responseData();
        try {
            Promotion promotion = iPromotionService.findById(id);
            promotion.setStatus(false);
            Promotion updatedPromotion = iPromotionService.saveOrUpdatePromotion(promotion);
            responseData.setDesc("Delete successfull");
            responseData.setData(updatedPromotion);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("Delete fail. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/search/{name}")
    private ResponseEntity<?> search(@PathVariable String name) {
        responseData responseData = new responseData();
        List<Promotion> listPromotions = iPromotionService.searchByName(name);
        responseData.setDesc("Search successfull");
        responseData.setData(listPromotions);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/create")
    private ResponseEntity<?> create(@RequestParam MultipartFile file,
            @RequestParam String name, @RequestParam int idVoucherType,
            @RequestParam double value, @RequestParam boolean status) {
        responseData responseData = new responseData();
        boolean isSuccess = iPromotionService.insertPromotion(file, name, idVoucherType, value, status);

        if (isSuccess) {
            responseData.setDesc("Insert successfull ");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setStatus(500);
            responseData.setDesc("Insert fail. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        Resource resource = iFileService.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}