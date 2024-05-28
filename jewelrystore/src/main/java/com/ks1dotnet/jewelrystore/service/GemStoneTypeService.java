package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneType;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IGemStoneTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneTypeService;

@Service
public class GemStoneTypeService implements IGemStoneTypeService {
    @Autowired
    private IGemStoneTypeRepository iGemStoneTypeRepository;

    @Override
    public ResponseData Page(int page, int size) {
        try {
            Page<GemStoneType> p = iGemStoneTypeRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<GemStoneTypeDTO> convertToDtoPage(Page<GemStoneType> productPage) {
        List<GemStoneTypeDTO> dtoList = productPage.getContent()
                .stream()
                .map(GemStoneType::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        GemStoneType gsc = iGemStoneTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gem stone type not found with id" + id));
        return new ResponseData(HttpStatus.OK, "Find gem stone type successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(GemStoneTypeDTO t) {
        try {
            if (t.getId() != 0 && iGemStoneTypeRepository.existsById(t.getId()))
                throw new BadRequestException("Can not create gem stone type that already exsist failed!");
            iGemStoneTypeRepository.save(new GemStoneType(t));
            return new ResponseData(HttpStatus.CREATED, "Create gem stone type successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create gem stone type failed");
        }
    }

    @Override
    public ResponseData update(GemStoneTypeDTO t) {
        try {
            if (t.getId() == 0 && !iGemStoneTypeRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update gem stone type that not exsist failed!");
            iGemStoneTypeRepository.save(new GemStoneType(t));
            return new ResponseData(HttpStatus.OK, "Update gem stone type successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Update gem stone type failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iGemStoneTypeRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Gem stone type exists!" : "Gem stone type not found!",
                exists);
    }

}
