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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
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
        ResponseData ResponseData = new ResponseData();
        ResponseData.setData(iPromotionService.findAll());
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @GetMapping("/getHomePagePromotion")
    private ResponseEntity<?> getHomePagePromotion(@RequestParam int page) {
        ResponseData ResponseData = new ResponseData();
        ResponseData.setData(iPromotionService.getHomePagePromotion2(page));
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/update")
    private ResponseEntity<?> update(@RequestParam MultipartFile file, @RequestParam int id,
            @RequestParam String name, @RequestParam int idVoucherType,
            @RequestParam double value, @RequestParam boolean status) {
        ResponseData ResponseData = new ResponseData();
        PromotionDTO promotionDTO = iPromotionService.updatePromotion(file, id, name, idVoucherType, value, status);

        if (promotionDTO != null) {
            ResponseData.setDesc("Update successful");
            ResponseData.setData(promotionDTO);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } else {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Update failed. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getById")
    private ResponseEntity<?> getById(@RequestParam int id) {
        ResponseData ResponseData = new ResponseData();
        Promotion promotion = iPromotionService.findById(id);
        if (promotion != null) {
            ResponseData.setDesc("Find successfull ");
            ResponseData.setData(promotion.getDTO());
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } else {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Find fail. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/delete/{id}")
    private ResponseEntity<?> update(@PathVariable int id) {
        ResponseData ResponseData = new ResponseData();
        try {
            Promotion promotion = iPromotionService.findById(id);
            promotion.setStatus(false);
            Promotion updatedPromotion = iPromotionService.saveOrUpdatePromotion(promotion);
            ResponseData.setDesc("Delete successfull");
            ResponseData.setData(updatedPromotion);
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Delete fail. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @GetMapping("/search/{name}")
    private ResponseEntity<?> search(@PathVariable String name) {
        ResponseData ResponseData = new ResponseData();
        List<Promotion> listPromotions = iPromotionService.searchByName(name);
        ResponseData.setDesc("Search successfull");
        ResponseData.setData(listPromotions);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/create")
    private ResponseEntity<?> create(@RequestParam MultipartFile file,
            @RequestParam String name, @RequestParam int idVoucherType,
            @RequestParam double value, @RequestParam boolean status) {
        ResponseData ResponseData = new ResponseData();
        boolean isSuccess = iPromotionService.insertPromotion(file, name, idVoucherType, value, status);

        if (isSuccess) {
            ResponseData.setDesc("Insert successfull ");
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } else {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Insert fail. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
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