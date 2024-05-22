package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.MaterialOfProductDTO;
import com.ks1dotnet.jewelrystore.entity.MaterialOfProduct;
import com.ks1dotnet.jewelrystore.repository.IMaterialOfProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialOfProductService;

@Service
public class MaterialOfProductService implements IMaterialOfProductService {
    @Autowired
    private IMaterialOfProductRepository iMaterialOfProductRepository;

    @Override
    public List<MaterialOfProductDTO> findAll() {
        try {
            List<MaterialOfProductDTO> listDTO = new ArrayList<>();
            for (MaterialOfProduct MaterialOfProduct : iMaterialOfProductRepository.findAll()) {
                listDTO.add(MaterialOfProduct.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStone of product find all error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public MaterialOfProductDTO findById(Integer id) {
        try {
            MaterialOfProduct gsp = iMaterialOfProductRepository.findById(id).orElse(null);
            if (gsp == null)
                return null;
            return gsp.getDTO();
        } catch (Exception e) {
            System.out.println("Material of product find by id error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public MaterialOfProductDTO save(MaterialOfProductDTO t) {
        try {
            iMaterialOfProductRepository.save(new MaterialOfProduct(t));
            return t;
        } catch (Exception e) {
            System.out.println("Material of product save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public MaterialOfProductDTO saveAndFlush(MaterialOfProductDTO t) {
        try {
            iMaterialOfProductRepository.saveAndFlush(new MaterialOfProduct(t));
            return t;
        } catch (Exception e) {
            System.out.println("Material of product save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<MaterialOfProductDTO> saveAll(Iterable<MaterialOfProductDTO> t) {
        List<MaterialOfProduct> list = new ArrayList<>();
        for (MaterialOfProductDTO MaterialOfProductDTO : t) {
            list.add(new MaterialOfProduct(MaterialOfProductDTO));
        }
        try {
            List<MaterialOfProductDTO> listDTO = new ArrayList<>();
            for (MaterialOfProduct MaterialOfProduct : iMaterialOfProductRepository.saveAll(list)) {
                listDTO.add(MaterialOfProduct.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Material of product saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<MaterialOfProductDTO> saveAllAndFlush(Iterable<MaterialOfProductDTO> t) {
        List<MaterialOfProduct> list = new ArrayList<>();
        for (MaterialOfProductDTO MaterialOfProductDTO : t) {
            list.add(new MaterialOfProduct(MaterialOfProductDTO));
        }
        try {
            List<MaterialOfProductDTO> listDTO = new ArrayList<>();
            for (MaterialOfProduct MaterialOfProduct : iMaterialOfProductRepository.saveAllAndFlush(list)) {
                listDTO.add(MaterialOfProduct.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Material of product saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return iMaterialOfProductRepository.existsById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            iMaterialOfProductRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Material of product delete by id error: " + e.getMessage());
            return false;
        }
    }
}
