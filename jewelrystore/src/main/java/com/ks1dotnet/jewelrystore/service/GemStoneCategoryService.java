package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneCategory;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IGemStoneCategoryRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;

@Service
public class GemStoneCategoryService implements IGemStoneCategoryService {
    @Autowired
    private IGemStoneCategoryRepository iGemStoneCategoryRepository;

    @Override
    public ResponseData findAll() {
        try {
            List<GemStoneCategoryDTO> listDTO = new ArrayList<>();
            for (GemStoneCategory gemStoneCategory : iGemStoneCategoryRepository.findAll()) {
                listDTO.add(gemStoneCategory.getDTO());
            }
            return new ResponseData(HttpStatus.OK, "Find all gem stone category successfully", listDTO);
        } catch (Exception e) {
            throw new RuntimeException("Find all gem stone categories error");
        }
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
