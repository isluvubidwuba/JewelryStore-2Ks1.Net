package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneCategory;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.repository.IGemStoneCategoryRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;

@Service
public class GemStoneCategoryService implements IGemStoneCategoryService {
    @Autowired
    private IGemStoneCategoryRepository iGemStoneCategoryRepository;

    @Override
    public responseData findAll() {
        try {
            List<GemStoneCategoryDTO> listDTO = new ArrayList<>();
            for (GemStoneCategory gemStoneCategory : iGemStoneCategoryRepository.findAll()) {
                listDTO.add(gemStoneCategory.getDTO());
            }
            return new responseData(200, "Find all gem stone sucessfully", listDTO);
        } catch (Exception e) {
            return new responseData(401, "Find all gem stone error: " + e.getMessage(), null);
        }
    }

    @Override
    public responseData findById(Integer id) {
        try {
            GemStoneCategory gsc = iGemStoneCategoryRepository.findById(id).orElse(null);
            if (gsc == null)
                return null;
            return new responseData(200, "Find gem stone category successfully", gsc.getDTO());
        } catch (Exception e) {
            System.out.println("GemStone find by id error: " + e.getMessage());
            return new responseData(0, null, e);
        }
    }

    @Override
    public responseData save(GemStoneCategoryDTO t) {
        try {
            iGemStoneCategoryRepository.save(new GemStoneCategory(t));
            return t;
        } catch (Exception e) {
            System.out.println("GemStone save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public responseData saveAndFlush(GemStoneCategoryDTO t) {
        try {
            iGemStoneCategoryRepository.saveAndFlush(new GemStoneCategory(t));
            return t;
        } catch (Exception e) {
            System.out.println("GemStone save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public responseData saveAll(Iterable<GemStoneCategoryDTO> t) {
        List<GemStoneCategory> list = new ArrayList<>();
        for (GemStoneCategoryDTO gemStoneCategoryDTO : t) {
            list.add(new GemStoneCategory(gemStoneCategoryDTO));
        }
        try {
            List<GemStoneCategoryDTO> listDTO = new ArrayList<>();
            for (GemStoneCategory gemStoneCategory : iGemStoneCategoryRepository.saveAll(list)) {
                listDTO.add(gemStoneCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStones saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public responseData saveAllAndFlush(Iterable<GemStoneCategoryDTO> t) {
        List<GemStoneCategory> list = new ArrayList<>();
        for (GemStoneCategoryDTO gemStoneCategoryDTO : t) {
            list.add(new GemStoneCategory(gemStoneCategoryDTO));
        }
        try {
            List<GemStoneCategoryDTO> listDTO = new ArrayList<>();
            for (GemStoneCategory gemStoneCategory : iGemStoneCategoryRepository
                    .saveAllAndFlush(list)) {
                listDTO.add(gemStoneCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStones saveAllandFLush error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public responseData existsById(Integer id) {
        return iGemStoneCategoryRepository.existsById(id);
    }

    @Override
    public responseData deleteById(Integer id) {
        try {
            iGemStoneCategoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("GemStones delete by id error: " + e.getMessage());
            return false;
        }
    }

}
