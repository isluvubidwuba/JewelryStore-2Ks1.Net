package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IFileService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IVoucherTypeService;

@Service
public class PromotionService implements IPromotionService {
    @Autowired
    private IPromotionRepository iPromotionRepository;

    @Autowired
    private IFileService iFileService;
    @Autowired
    private IVoucherTypeService iVoucherTypeService;

    @Override
    public List<Promotion> findAll() {
        return iPromotionRepository.findByStatus(true);
    }

    @Override
    public Promotion saveOrUpdatePromotion(Promotion promotion) {
        return iPromotionRepository.save(promotion);
    }

    @Override
    public Promotion findById(int id) {
        return iPromotionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Promotion> searchByName(String name) {
        return iPromotionRepository.findByNameLike("%" + name + "%");
    }

    @Override
    public boolean insertPromotion(MultipartFile file, String name, int idVoucherType, double value,
            boolean status) {
        boolean isInsertSuccess = false;
        boolean isSaveFileSuccess = iFileService.savefile(file);
        if (isSaveFileSuccess) {
            Promotion promotion = new Promotion();
            promotion.setName(name);
            promotion.setVoucherType(iVoucherTypeService.getVoucherById(idVoucherType));
            promotion.setValue(value);
            promotion.setStatus(status);
            promotion.setImage(file.getOriginalFilename());
            iPromotionRepository.save(promotion);
            isInsertSuccess = true;
        }
        return isInsertSuccess;
    }

    @Override
    public List<PromotionDTO> getHomePagePromotion(int page) {
        List<PromotionDTO> promotionDTOs = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 8);
        Page<Promotion> listData = iPromotionRepository.findAll(pageRequest);
        for (Promotion p : listData) {
            PromotionDTO promotionDTO = new PromotionDTO();
            promotionDTO.setImage(p.getImage());
            promotionDTO.setName(p.getName());
            promotionDTO.setStatus(p.isStatus());
            promotionDTO.setValue(p.getValue());
            promotionDTO.setIdVoucherType(p.getVoucherType().getId());
            promotionDTOs.add(promotionDTO);
        }

        return promotionDTOs;
    }

    @Override
    public boolean updatePromotion(MultipartFile file, int id, String name, int idVoucherType, double value,
            boolean status) {
        boolean isUpdateSuccess = false;
        boolean isSaveFileSuccess = iFileService.savefile(file);
        if (isSaveFileSuccess) {
            Promotion promotion = new Promotion();
            promotion.setId(id);
            promotion.setName(name);
            promotion.setVoucherType(iVoucherTypeService.getVoucherById(idVoucherType));
            promotion.setValue(value);
            promotion.setStatus(status);
            promotion.setImage(file.getOriginalFilename());
            iPromotionRepository.save(promotion);
            isUpdateSuccess = true;
        }
        return isUpdateSuccess;
    }
}
