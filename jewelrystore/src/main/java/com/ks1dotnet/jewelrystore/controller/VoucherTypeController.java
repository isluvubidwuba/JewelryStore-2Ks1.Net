package com.ks1dotnet.jewelrystore.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.VoucherTypeDTO;
import com.ks1dotnet.jewelrystore.entity.VoucherType;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductTypeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IVoucherTypeService;

@RestController
@RequestMapping("voucher")
@CrossOrigin("*")
public class VoucherTypeController {
    @Autowired
    private IVoucherTypeService iVoucherTypeService;
    @Autowired
    private IForProductTypeService iForProductTypeService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        ResponseData ResponseData = new ResponseData();
        List<VoucherType> list = iVoucherTypeService.findAll();
        List<VoucherTypeDTO> listVoucherTypeDTOs = new ArrayList<>();
        for (VoucherType voucherType : list) {
            listVoucherTypeDTOs.add(voucherType.getDTO());
        }
        ResponseData.setData(listVoucherTypeDTOs);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherTypeById(@PathVariable Integer id) {
        try {
            VoucherType voucherType = iVoucherTypeService.getVoucherTypeById(id);
            return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "SUCCESS", voucherType.getDTO()),
                    HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.NOT_FOUND, "VoucherType not found with id: " + id, null),
                    HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching voucher type", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData> createVoucherType(@RequestBody VoucherType voucherType) {
        try {
            if (voucherType.getType() == null || voucherType.getType().trim().isEmpty()) {
                throw new BadRequestException("Invalid input data: Type is null or empty");
            }
            VoucherType createdVoucherType = iVoucherTypeService.createVoucherType(voucherType);
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.CREATED, "VoucherType created successfully",
                            createdVoucherType.getDTO()),
                    HttpStatus.OK);
        } catch (BadRequestException ex) {
            return new ResponseEntity<>(new ResponseData(HttpStatus.BAD_REQUEST, ex.getErrorString(), null),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating voucher type", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> getById(@RequestParam int id, @RequestParam String type) {
        try {
            ResponseData responseData = new ResponseData();
            VoucherTypeDTO VoucherTypeDTO = iVoucherTypeService.updateVoucherType(id, type);
            responseData.setDesc("Update successful");
            responseData.setData(VoucherTypeDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            ResponseData responseData = new ResponseData();
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc(e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        } catch (BadRequestException ex) {
            return new ResponseEntity<>(new ResponseData(HttpStatus.BAD_REQUEST, ex.getErrorString(), null),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating voucher type", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<?> getCategoriesByVoucherTypeId(@PathVariable Integer id) {
        ResponseData responseData = new ResponseData();
        try {
            if (!iVoucherTypeService.existsById(id)) {
                throw new ResourceNotFoundException("VoucherType not found with id: " + id);
            }
            List<ProductCategoryDTO> categories = iVoucherTypeService.getCategoriesByVoucherTypeId(id);
            responseData.setDesc("VoucherType found successfully with id: " + id);
            responseData.setData(categories);
            responseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            responseData.setDesc("VoucherType not found with id: " + id);
            responseData.setStatus(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
            responseData.setDesc("Failed to retrieve categories.");
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/apply-categories")
    public ResponseEntity<ResponseData> applyCategoriesToVoucherType(
            @RequestBody ApplyPromotionDTO applyCategoriesDTO) {
        ResponseData responseData = iForProductTypeService.applyCategoriesToVoucherType(applyCategoriesDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/remove-categories")
    public ResponseEntity<ResponseData> removeCategoriesFromVoucherType(
            @RequestBody ApplyPromotionDTO applyCategoriesDTO) {
        ResponseData responseData = iForProductTypeService.removeCategoriesFromVoucherType(applyCategoriesDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/{voucherTypeId}/categories/not-in")
    public ResponseEntity<ResponseData> getCategoriesNotInVoucherType(@PathVariable int voucherTypeId) {
        ResponseData responseData = iForProductTypeService.getCategoriesNotInVoucherType(voucherTypeId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}