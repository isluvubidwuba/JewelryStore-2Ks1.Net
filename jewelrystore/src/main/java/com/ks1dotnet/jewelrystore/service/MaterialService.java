package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.MaterialDTO;
import com.ks1dotnet.jewelrystore.entity.Material;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IMaterialRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialService;

@Service
public class MaterialService implements IMaterialService {

    @Autowired
    private IMaterialRepository iMaterialRepository;

    @Override
    public ResponseData findAll() {
        try {
            List<MaterialDTO> listDTO = new ArrayList<>();
            for (Material Material : iMaterialRepository.findAll()) {
                listDTO.add(Material.getDTO());
            }
            return new ResponseData(HttpStatus.OK, "Find all material successfully", listDTO);
        } catch (Exception e) {
            throw new RunTimeExceptionV1("Find all material error");
        }
    }

    @Override
    public ResponseData findById(Integer id) {
        Material gsc = iMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("material not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find material successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(MaterialDTO t) {
        try {
            if (t.getId() != 0 && iMaterialRepository.existsById(t.getId()))
                throw new BadRequestException("Can not create material that already exsist failed!");
            iMaterialRepository.save(new Material(t));
            return new ResponseData(HttpStatus.CREATED, "Create material successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create material failed");
        }
    }

    @Override
    public ResponseData update(MaterialDTO t) {
        try {
            if (t.getId() == 0 && !iMaterialRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update material that not exsist failed!");
            iMaterialRepository.save(new Material(t));
            return new ResponseData(HttpStatus.OK, "Update material successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Update material failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iMaterialRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "material exists!" : "material not found!",
                exists);
    }

}
