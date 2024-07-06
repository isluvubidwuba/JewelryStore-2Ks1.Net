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
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
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
            return new ResponseData(HttpStatus.OK, "Find all products successfully",
                    convertToDtoPage(p));

        } catch (Exception e) {
            throw new ApplicationException("Error at Page MaterialService: " + e.getMessage(),
                    "Find all material error");
        }
    }

    private Page<MaterialDTO> convertToDtoPage(Page<Material> productPage) {
        List<MaterialDTO> dtoList = productPage.getContent().stream().map(Material::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        Material gsc = iMaterialRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Material not found with id: " + id,
                        HttpStatus.NOT_FOUND));
        return new ResponseData(HttpStatus.OK, "Find material successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(MaterialDTO t) {
        try {
            if (t.getId() != 0 && iMaterialRepository.existsById(t.getId()))
                throw new ApplicationException(
                        "Can not create material that already exsist failed!",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            iMaterialRepository.save(new Material(t));
            return new ResponseData(HttpStatus.CREATED, "Create material successfully", t);
        } catch (Exception e) {
            throw new ApplicationException("Error at insert MaterialService: " + e.getMessage(),
                    "Create material failed");
        }
    }

    @Override
    public ResponseData update(MaterialDTO t) {
        try {
            if (t.getId() == 0 && !iMaterialRepository.existsById(t.getId()))
                throw new ApplicationException("Can not update material that not exsist failed!",
                        HttpStatus.INTERNAL_SERVER_ERROR);
            iMaterialRepository.save(new Material(t));
            return new ResponseData(HttpStatus.OK, "Update material successfully", t);
        } catch (Exception e) {
            throw new ApplicationException("Error at update MaterialService: " + e.getMessage(),
                    "Update material failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iMaterialRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "material exists!" : "material not found!", exists);
    }

    @Override
    public ResponseData getGoldPirce(List<MaterialDTO> t) {
        try {
            List<Material> list = new ArrayList<>();
            for (MaterialDTO material : t) {
                Material m = iMaterialRepository.findById(material.getId())
                        .orElseThrow(() -> new ApplicationException(
                                "Can not find material with id: " + material.getId(),
                                HttpStatus.BAD_REQUEST));
                m.setPriceAtTime(material.getPriceAtTime());
                m.setLastModified(material.getLastModified());
                list.add(m);
            }
            iMaterialRepository.saveAll(list);
            return new ResponseData(HttpStatus.OK, "Get gold price successfully", null);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getGoldPirce MaterialService: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getGoldPirce MaterialService: " + e.getMessage(),
                    "Get gold price failed ");
        }
    }

}
