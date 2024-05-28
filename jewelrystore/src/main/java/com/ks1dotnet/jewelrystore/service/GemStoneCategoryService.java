package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneCategory;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IGemStoneCategoryRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;

@Service
public class GemStoneCategoryService implements IGemStoneCategoryService {
    @Autowired
    private IGemStoneCategoryRepository iGemStoneCategoryRepository;

    @Override
    public ResponseData Page(int page, int size) {
        try {
            Page<GemStoneCategory> p = iGemStoneCategoryRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<GemStoneCategoryDTO> convertToDtoPage(Page<GemStoneCategory> productPage) {
        List<GemStoneCategoryDTO> dtoList = productPage.getContent()
                .stream()
                .map(GemStoneCategory::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        GemStoneCategory gsc = iGemStoneCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gem stone category not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find gem stone category successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(GemStoneCategoryDTO t) {
        try {
            if (t.getId() != 0 && iGemStoneCategoryRepository.existsById(t.getId()))
                throw new BadRequestException("Can not create gem stone category that already exsist failed!");
            iGemStoneCategoryRepository.save(new GemStoneCategory(t));
            return new ResponseData(HttpStatus.CREATED, "Create gem stone category successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create gem stone category failed");
        }
    }

    @Override
    public ResponseData update(GemStoneCategoryDTO t) {
        try {
            if (t.getId() == 0 && !iGemStoneCategoryRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update gem stone category that not exsist failed!");
            iGemStoneCategoryRepository.save(new GemStoneCategory(t));
            return new ResponseData(HttpStatus.OK, "Update gem stone category successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Update gem stone category failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iGemStoneCategoryRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Gem stone category exists!" : "Gem stone category not found!",
                exists);
    }

}
