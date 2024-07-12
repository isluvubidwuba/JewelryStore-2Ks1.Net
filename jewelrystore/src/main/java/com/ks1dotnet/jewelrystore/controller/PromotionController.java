package com.ks1dotnet.jewelrystore.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForProduct;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;

@RestController
@RequestMapping("${apiURL}/promotion")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class PromotionController {
    @Autowired
    private IPromotionRepository iPromotionRepository;
    @Autowired
    private IPromotionService iPromotionService;
    @Autowired
    private IInvoiceTypeRepository iInvoiceTypeRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        ResponseData responseData = iPromotionService.getAllPromotionDTO();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/valid")
    public ResponseEntity<?> checkExpiredPromotion() {
        ResponseData responseData = iPromotionService.deleteExpiredPromotions();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> create(@RequestParam(required = false) MultipartFile file,
            @RequestParam String name, @RequestParam double value, @RequestParam boolean status,
            @RequestParam String startDate, @RequestParam String endDate,
            @RequestParam String promotionType, @RequestParam int invoiceType) { // Thêm
                                                                                 // invoiceTypeId
                                                                                 // vào đây
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            ResponseData responseData = iPromotionService.insertPromotion(file, name, value, status,
                    start, end, promotionType, invoiceType);
            if (responseData.getStatus() == HttpStatus.OK) {
                return new ResponseEntity<>(responseData, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at create promotionController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at create promotionController: " + e.getMessage(),
                    "Something wrong while create promotion!");
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> update(@RequestParam(required = false) MultipartFile file,
            @RequestParam int id, @RequestParam String name, @RequestParam double value,
            @RequestParam boolean status, @RequestParam String startDate,
            @RequestParam String endDate) { // Thêm invoiceTypeId vào đây
        try {

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO =
                    iPromotionService.updatePromotion(file, id, name, value, status, start, end); // Truyền
                                                                                                  // promotionType
                                                                                                  // và
                                                                                                  // invoiceTypeId
                                                                                                  // vào
            responseData.setStatus(HttpStatus.OK); // đây
            responseData.setDesc("Update successful");
            responseData.setData(promotionDTO);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at update promotionController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at update promotionController: " + e.getMessage(),
                    "Something wrong while update promotion!");
        }
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO = iPromotionService.findById(id);
            if (promotionDTO == null) {
                throw new ApplicationException("Promotion not found with id: " + id,
                        HttpStatus.NOT_FOUND);
            }
            iPromotionService.deletePromotion(id);
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Delete successful");
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at delete promotionController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at delete promotionController: " + e.getMessage(),
                    "Something wrong while delete promotion!");
        }
    }

    @GetMapping("/viewPolicyByInvoiceType/{id}")
    public ResponseEntity<?> viewPolicy(@PathVariable int id) {
        try {
            ResponseData responseData = new ResponseData();
            InvoiceType invoiceType = iInvoiceTypeRepository.findById(id).orElseThrow(
                    () -> new ApplicationException("Not found with invoice type id: " + id,
                            HttpStatus.NOT_FOUND));
            List<PromotionDTO> lPromotionDTOs =
                    iPromotionService.findByInvoiceTypeAndStatusTrue(invoiceType).stream()
                            .map(Promotion::getDTO).collect(Collectors.toList());
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("get policy successful");
            responseData.setData(lPromotionDTOs);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at viewPolicy promotionController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at viewPolicy promotionController: " + e.getMessage(),
                    "Something wrong while view policy!");
        }
    }

    @PostMapping("/getById")
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            ResponseData responseData = new ResponseData();
            PromotionDTO promotionDTO = iPromotionService.findById(id);
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Find successful");
            responseData.setData(promotionDTO);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getById promotionController: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getById promotionController: " + e.getMessage(),
                    "Something wrong while get promotion/policy by id!");
        }
    }

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
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getPromotionsByProductId promotionController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getPromotionsByProductId promotionController: " + e.getMessage(),
                    "Something wrong while get promotion/policy by product id!");
        }

    }

    @GetMapping("/by-user")
    public ResponseEntity<?> getPromotionsByUserId(@RequestParam int userId) {
        try {
            PromotionDTO promotion = iPromotionService.getPromotionsByUserId(userId);
            ResponseData responseData =
                    new ResponseData(HttpStatus.OK, "Get promotion success", promotion);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getPromotionsByUserId promotionController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getPromotionsByUserId promotionController: " + e.getMessage(),
                    "Something wrong while get promotion/policy by user id!");
        }

    }

}
