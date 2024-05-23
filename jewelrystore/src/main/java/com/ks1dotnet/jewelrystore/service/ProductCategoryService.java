package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductCategoryService;

@Service
public class ProductCategoryService implements IProductCategoryService {
    @Autowired
    private IProductCategoryRepository iProductCategoryRepository;

    @Override
    public ResponseData findAll() {
        try {
            List<ProductCategoryDTO> listDTO = new ArrayList<>();
            for (ProductCategory ProductCategory : iProductCategoryRepository.findAll()) {
                listDTO.add(ProductCategory.getDTO());
            }
            return new ResponseData(HttpStatus.OK, "Find all product category successfully", listDTO);
        } catch (Exception e) {
            throw new RunTimeExceptionV1("Find all product category error");
        }
    }

    @Override
    public ResponseData findById(Integer id) {
        ProductCategory gsc = iProductCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find Product category successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(ProductCategoryDTO t) {
        try {
            if (t.getId() != 0 && iProductCategoryRepository.existsById(t.getId()))
                throw new BadRequestException("Can not create product category that already exsist failed!");
            iProductCategoryRepository.save(new ProductCategory(t));
            return new ResponseData(HttpStatus.CREATED, "Create product category successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create product category failed");
        }
    }

    @Override
    public ResponseData update(ProductCategoryDTO t) {
        try {
            if (t.getId() == 0 && !iProductCategoryRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update product category that not exsist failed!");
            iProductCategoryRepository.save(new ProductCategory(t));
            return new ResponseData(HttpStatus.OK, "Update product category successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Update product category failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iProductCategoryRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Product category exists!" : "Product category not found!",
                exists);
    }

}
