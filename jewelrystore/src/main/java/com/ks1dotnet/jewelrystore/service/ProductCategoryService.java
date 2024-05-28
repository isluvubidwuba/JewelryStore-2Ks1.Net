package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    public ResponseData Page(int page, int size) {
        try {
            Page<ProductCategory> p = iProductCategoryRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products category successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<ProductCategoryDTO> convertToDtoPage(Page<ProductCategory> productPage) {
        List<ProductCategoryDTO> dtoList = productPage.getContent()
                .stream()
                .map(ProductCategory::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
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
