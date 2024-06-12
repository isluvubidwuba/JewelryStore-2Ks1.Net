package com.ks1dotnet.jewelrystore.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForProduct;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.FirebaseStorageService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;

@RestController
@RequestMapping("/promotion")
@CrossOrigin("*")
public class PromotionController {
    @Autowired
    private IPromotionRepository iPromotionRepository;
    @Autowired
    private IPromotionService iPromotionService;

    @Value("${fileUpload.promotionPath}")
    private String filePath;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        ResponseData responseData = iPromotionService.getAllPromotionDTO();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String name,
            @RequestParam double value,
            @RequestParam boolean status,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String promotionType,
            @RequestParam int invoiceType) { // Thêm invoiceTypeId vào đây
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            String fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
            ResponseData responseData = iPromotionService.insertPromotion(fileName, name, value, status, start, end,
                    promotionType, invoiceType); // Truyền invoiceTypeId vào đây
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
            @RequestParam(required = false) MultipartFile file,
            @RequestParam int id,
            @RequestParam String name,
            @RequestParam double value,
            @RequestParam boolean status,
            @RequestParam String startDate,
            @RequestParam String endDate) { // Thêm invoiceTypeId vào đây
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            ResponseData responseData = new ResponseData();
            String fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
            PromotionDTO promotionDTO = iPromotionService.updatePromotion(fileName, id, name, value, status, start, end,
                    invoiceTypeId); // Truyền promotionType và invoiceTypeId vào đây
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

    // @GetMapping("/getHomePagePromotion")
    // public ResponseEntity<?> getHomePagePromotion(@RequestParam int page) {
    // iPromotionService.deleteExpiredPromotions();
    // try {
    // ResponseData responseData = new ResponseData();
    // responseData.setData(iPromotionService.getHomePagePromotion(page));
    // return new ResponseEntity<>(responseData, HttpStatus.OK);
    // } catch (BadRequestException e) {
    // return handleBadRequestException(e);
    // } catch (Exception e) {
    // return handleException(e);
    // }
    // }

    // @GetMapping("/files/{filename:.+}")
    // @ResponseBody
    // public ResponseEntity<?> getFile(@PathVariable String filename) {
    // try {
    // Resource resource = iFileService.loadFile(filename);
    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
    // resource.getFilename() + "\"")
    // .body(resource);
    // } catch (BadRequestException e) {
    // return handleBadRequestException(e);
    // } catch (Exception e) {
    // return handleException(e);
    // }
    // }

    @GetMapping("/all-promotion-on-product")
    public ResponseEntity<?> getPromotionsByProductId(@RequestParam int productId) {
        try {
            // List<PromotionDTO> lPromotionDTOs =
            // iPromotionService.getAllPromotionByIdProduct(productId);
            Promotion list = iPromotionRepository.findById(45).get();
            List<ProductDTO> list2 = new ArrayList<>();
            List<ForProduct> list3 = list.getListForProduct();
            for (ForProduct fp : list3) {
                if (fp.isStatus()) {
                    list2.add(fp.getProduct().getDTO());
                }
            }
            return new ResponseEntity<>(new ResponseData(null, "Get list promotion success", list2),
                    HttpStatus.OK);
        } catch (Exception e) {
            return handleBadRequestException(e);
        }

    }

    @GetMapping("/by-user")
    public ResponseEntity<?> getPromotionsByUserId(@RequestParam int userId) {
        try {
            List<PromotionDTO> promotions = iPromotionService.getPromotionsByUserId(userId);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Get list promotion success", promotions);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            return handleBadRequestException(e);
        }

    }

    private ResponseEntity<?> handleBadRequestException(Exception e) {
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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        ResponseData response = firebaseStorageService.uploadImage(file, filePath);
        return new ResponseEntity<>(response, response.getStatus());

    }
}
