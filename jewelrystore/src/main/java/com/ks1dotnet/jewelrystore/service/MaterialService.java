package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    public ResponseData Page(int page, int size) {
        try {
            Page<Material> p = iMaterialRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<MaterialDTO> convertToDtoPage(Page<Material> productPage) {
        List<MaterialDTO> dtoList = productPage.getContent()
                .stream()
                .map(Material::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
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

    @Override
    public ResponseData getGoldPirce(List<MaterialDTO> t) {
        try {
            List<Material> list = new ArrayList<>();
            for (MaterialDTO material : t) {
                Material m = iMaterialRepository.getById(material.getId());
                m.setPriceAtTime(material.getPriceAtTime());
                m.setLastModified(material.getLastModified());
                list.add(m);
            }
            iMaterialRepository.saveAll(list);
            return new ResponseData(HttpStatus.OK, "Get gold price successfully", null);
        } catch (Exception e) {
            throw new RunTimeExceptionV1("Get gold price failed ", e.getMessage());
        }
    }

}
