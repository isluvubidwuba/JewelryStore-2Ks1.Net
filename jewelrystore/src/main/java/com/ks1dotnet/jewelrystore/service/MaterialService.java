package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.MaterialDTO;
import com.ks1dotnet.jewelrystore.entity.Material;
import com.ks1dotnet.jewelrystore.repository.IMaterialRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialService;

@Service
public class MaterialService implements IMaterialService {

    @Autowired
    private IMaterialRepository iMaterialRepository;

    @Override
    public List<MaterialDTO> findAll() {
        try {
            List<MaterialDTO> listDTO = new ArrayList<>();
            for (Material Material : iMaterialRepository.findAll()) {
                listDTO.add(Material.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Material find all error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public MaterialDTO findById(Integer id) {
        try {
            Material gsp = iMaterialRepository.findById(id).orElse(null);
            if (gsp == null)
                return null;
            return gsp.getDTO();
        } catch (Exception e) {
            System.out.println("Material find by id error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public MaterialDTO save(MaterialDTO t) {
        try {
            iMaterialRepository.save(new Material(t));
            return t;
        } catch (Exception e) {
            System.out.println("Material save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public MaterialDTO saveAndFlush(MaterialDTO t) {
        try {
            iMaterialRepository.saveAndFlush(new Material(t));
            return t;
        } catch (Exception e) {
            System.out.println("Material save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<MaterialDTO> saveAll(Iterable<MaterialDTO> t) {
        List<Material> list = new ArrayList<>();
        for (MaterialDTO MaterialDTO : t) {
            list.add(new Material(MaterialDTO));
        }
        try {
            List<MaterialDTO> listDTO = new ArrayList<>();
            for (Material Material : iMaterialRepository.saveAll(list)) {
                listDTO.add(Material.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Material saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<MaterialDTO> saveAllAndFlush(Iterable<MaterialDTO> t) {
        List<Material> list = new ArrayList<>();
        for (MaterialDTO MaterialDTO : t) {
            list.add(new Material(MaterialDTO));
        }
        try {
            List<MaterialDTO> listDTO = new ArrayList<>();
            for (Material Material : iMaterialRepository.saveAllAndFlush(list)) {
                listDTO.add(Material.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Material saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return iMaterialRepository.existsById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            iMaterialRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Material delete by id error: " + e.getMessage());
            return false;
        }
    }

}
