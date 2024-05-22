package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneType;
import com.ks1dotnet.jewelrystore.repository.IGemStoneTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneTypeService;

@Service
public class GemStoneTypeService implements IGemStoneTypeService {
    @Autowired
    private IGemStoneTypeRepository iGemStoneTypeRepository;

    @Override
    public List<GemStoneTypeDTO> findAll() {
        try {
            List<GemStoneTypeDTO> listDTO = new ArrayList<>();
            for (GemStoneType GemStoneType : iGemStoneTypeRepository.findAll()) {
                listDTO.add(GemStoneType.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStone type find all error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public GemStoneTypeDTO findById(Integer id) {
        try {
            GemStoneType gst = iGemStoneTypeRepository.findById(id).orElse(null);
            if (gst == null)
                return null;
            return gst.getDTO();
        } catch (Exception e) {
            System.out.println("GemStone type find by id error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public GemStoneTypeDTO save(GemStoneTypeDTO t) {
        try {
            iGemStoneTypeRepository.save(new GemStoneType(t));
            return t;
        } catch (Exception e) {
            System.out.println("GemStone type save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public GemStoneTypeDTO saveAndFlush(GemStoneTypeDTO t) {
        try {
            iGemStoneTypeRepository.saveAndFlush(new GemStoneType(t));
            return t;
        } catch (Exception e) {
            System.out.println("GemStone type save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<GemStoneTypeDTO> saveAll(Iterable<GemStoneTypeDTO> t) {
        List<GemStoneType> list = new ArrayList<>();
        for (GemStoneTypeDTO GemStoneTypeDTO : t) {
            list.add(new GemStoneType(GemStoneTypeDTO));
        }
        try {
            List<GemStoneTypeDTO> listDTO = new ArrayList<>();
            for (GemStoneType GemStoneType : iGemStoneTypeRepository.saveAll(list)) {
                listDTO.add(GemStoneType.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStones type saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<GemStoneTypeDTO> saveAllAndFlush(Iterable<GemStoneTypeDTO> t) {
        List<GemStoneType> list = new ArrayList<>();
        for (GemStoneTypeDTO GemStoneTypeDTO : t) {
            list.add(new GemStoneType(GemStoneTypeDTO));
        }
        try {
            List<GemStoneTypeDTO> listDTO = new ArrayList<>();
            for (GemStoneType GemStoneType : iGemStoneTypeRepository.saveAllAndFlush(list)) {
                listDTO.add(GemStoneType.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("GemStones type saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return iGemStoneTypeRepository.existsById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            iGemStoneTypeRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("GemStones type delete by id error: " + e.getMessage());
            return false;
        }
    }

}
