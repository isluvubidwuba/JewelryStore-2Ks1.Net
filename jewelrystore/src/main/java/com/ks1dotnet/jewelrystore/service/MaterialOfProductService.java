package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.MaterialOfProductDTO;
import com.ks1dotnet.jewelrystore.entity.MaterialOfProduct;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IMaterialOfProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialOfProductService;

@Service
public class MaterialOfProductService implements IMaterialOfProductService {
    @Autowired
    private IMaterialOfProductRepository iMaterialOfProductRepository;

    @Override
    public ResponseData Page(int page, int size) {
        try {
            Page<MaterialOfProduct> p = iMaterialOfProductRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<MaterialOfProductDTO> convertToDtoPage(Page<MaterialOfProduct> productPage) {
        List<MaterialOfProductDTO> dtoList = productPage.getContent()
                .stream()
                .map(MaterialOfProduct::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        MaterialOfProduct gsc = iMaterialOfProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("material of product not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find material of product successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(MaterialOfProductDTO t) {
        try {
            if (t.getId() != 0 && iMaterialOfProductRepository.existsById(t.getId()))
                throw new BadRequestException("Can not create material of product that already exsist failed!");
            iMaterialOfProductRepository.save(new MaterialOfProduct(t));
            return new ResponseData(HttpStatus.CREATED, "Create material of product successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create material of product failed");
        }
    }

    @Override
    public ResponseData update(MaterialOfProductDTO t) {
        try {
            if (t.getId() == 0 && !iMaterialOfProductRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update material of product that not exsist failed!");
            iMaterialOfProductRepository.save(new MaterialOfProduct(t));
            return new ResponseData(HttpStatus.OK, "Update material of product successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Update material of product failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iMaterialOfProductRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "material of product exists!" : "material of product not found!",
                exists);
    }

}
