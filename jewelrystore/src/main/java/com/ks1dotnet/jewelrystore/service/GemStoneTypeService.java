package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseData findAll() {
        try {
            List<GemStoneTypeDTO> listDTO = new ArrayList<>();
            for (GemStoneType GemStoneType : iGemStoneTypeRepository.findAll()) {
                listDTO.add(GemStoneType.getDTO());
            }
            return new ResponseData(HttpStatus.OK, "Find all gem stone type successfully", listDTO);
        } catch (Exception e) {
            throw new RunTimeExceptionV1("Find all gem stone type error");
        }
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
