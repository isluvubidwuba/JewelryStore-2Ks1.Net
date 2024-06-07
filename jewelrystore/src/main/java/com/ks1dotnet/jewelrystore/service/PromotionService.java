package com.ks1dotnet.jewelrystore.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IFileService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;

@Service
public class PromotionService implements IPromotionService {
    @Autowired
    private IPromotionRepository iPromotionRepository;

    @Autowired
    private IFileService iFileService;

    @Override
    public Map<String, Object> getHomePagePromotion(int page) {
        try {
            Map<String, Object> response = new HashMap<>();
            PageRequest pageRequest = PageRequest.of(page, 2);
            Page<PromotionDTO> listData = iPromotionRepository.findAllPromotions(pageRequest);

            response.put("promotions", listData.getContent());
            response.put("totalPages", listData.getTotalPages());
            response.put("currentPage", page);

            return response;
        } catch (Exception e) {
            throw new BadRequestException("Failed to get home page promotions with pagination", e.getMessage());
        }
    }

    @Override
    public ResponseData insertPromotion(MultipartFile file, String name, double value, boolean status,
            LocalDate startDate, LocalDate endDate, String promotionType) {
        ResponseData responseData = new ResponseData();
        try {
            Promotion promotion = new Promotion();
            promotion.setName(name);
            promotion.setValue(value);
            promotion.setStatus(status);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setLastModified();
            promotion.setPromotionType(promotionType);

            if (file != null && !file.isEmpty()) {
                boolean isSaveFileSuccess = iFileService.savefile(file);
                if (isSaveFileSuccess) {
                    promotion.setImage(file.getOriginalFilename());
                } else {
                    responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                    responseData.setDesc("File upload failed.");
                    return responseData;
                }
            } else {
                promotion.setImage("default_image.jpg");
            }

            PromotionDTO promotionDTO = iPromotionRepository.save(promotion).getDTO();
            responseData.setData(promotionDTO);
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Insert successful");
        } catch (Exception e) {
            throw new BadRequestException("Failed to insert promotion", e.getMessage());
        }

        return responseData;
    }

    @Override
    public PromotionDTO updatePromotion(MultipartFile file, int id, String name, double value, boolean status,
            LocalDate startDate, LocalDate endDate) {
        try {
            Promotion promotion = iPromotionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));

            promotion.setName(name);
            promotion.setValue(value);
            promotion.setStatus(status);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setLastModified();
            // promotion.setPromotionType(promotionType);

            if (file != null && !file.isEmpty()) {
                boolean isSaveFileSuccess = iFileService.savefile(file);
                if (isSaveFileSuccess) {
                    promotion.setImage(file.getOriginalFilename());
                }
            }

            promotion = iPromotionRepository.save(promotion);
            return promotion.getDTO();
        } catch (Exception e) {
            throw new BadRequestException("Failed to update promotion", e.getMessage());
        }
    }

    @Override
    public PromotionDTO findById(int id) {
        PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(id);
        if (promotionDTO == null) {
            throw new ResourceNotFoundException("Promotion not found with id: " + id);
        }
        return promotionDTO;
    }

    @Override
    public void deletePromotion(int id) {
        try {
            Promotion promotion = iPromotionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
            promotion.setStatus(false);
            iPromotionRepository.save(promotion);
        } catch (Exception e) {
            throw new BadRequestException("Failed to delete promotion", e.getMessage());
        }
    }

    @Override
    public void deleteExpiredPromotions() {
        try {
            List<Promotion> expiredPromotions = iPromotionRepository.findByEndDateBefore(LocalDate.now());
            for (Promotion promotion : expiredPromotions) {
                promotion.setStatus(false);
                iPromotionRepository.save(promotion);
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to delete expired promotions", e.getMessage());
        }
    }

}
