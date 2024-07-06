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
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
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
            return new ResponseData(HttpStatus.OK, "Find all products successfully",
                    convertToDtoPage(p));

        } catch (Exception e) {
            throw new ApplicationException("Error at page GemStoneTypeService: " + e.getMessage(),
                    "Find all product error");
        }
    }

    private Page<GemStoneTypeDTO> convertToDtoPage(Page<GemStoneType> productPage) {
        List<GemStoneTypeDTO> dtoList = productPage.getContent().stream().map(GemStoneType::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        GemStoneType gsc = iGemStoneTypeRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Gem stone type not found with id" + id,
                        HttpStatus.NOT_FOUND));
        return new ResponseData(HttpStatus.OK, "Find gem stone type successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(GemStoneTypeDTO t) {
        try {
            if (t.getId() != 0 && iGemStoneTypeRepository.existsById(t.getId()))
                throw new ApplicationException(
                        "Can not create gem stone type that already exsist failed!",
                        HttpStatus.BAD_REQUEST);
            iGemStoneTypeRepository.save(new GemStoneType(t));
            return new ResponseData(HttpStatus.CREATED, "Create gem stone type successfully", t);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at findById GemStoneTypeService: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at findById GemStoneTypeService: " + e.getMessage(),
                    "Create gem stone type failed");
        }
    }

    @Override
    public ResponseData update(GemStoneTypeDTO t) {
        try {
            if (t.getId() == 0 && !iGemStoneTypeRepository.existsById(t.getId()))
                throw new ApplicationException(
                        "Can not update gem stone type that not exsist failed!",
                        HttpStatus.BAD_REQUEST);
            iGemStoneTypeRepository.save(new GemStoneType(t));
            return new ResponseData(HttpStatus.OK, "Update gem stone type successfully", t);
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at update GemStoneTypeService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at update GemStoneTypeService: " + e.getMessage(),
                    "Update gem stone type failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iGemStoneTypeRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Gem stone type exists!" : "Gem stone type not found!", exists);
    }

}
