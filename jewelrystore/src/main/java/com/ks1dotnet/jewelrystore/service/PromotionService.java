package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
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
        try {
            return iPromotionRepository.findAll();
        } catch (Exception e) {
            throw new BadRequestException("Failed to fetch all promotions", e.getMessage());
        }
    }

    @Override
    public Promotion saveOrUpdatePromotion(Promotion promotion) {
        try {
            return iPromotionRepository.save(promotion);
        } catch (Exception e) {
            throw new BadRequestException("Failed to save or update promotion", e.getMessage());
        }
    }

    @Override
    public Promotion findById(int id) {
        try {
            return iPromotionRepository.findById(id).orElse(null);
        } catch (Exception e) {
            throw new BadRequestException("Failed to find promotion by id", e.getMessage());
        }
    }

    @Override
    public List<Promotion> searchByName(String name) {
        try {
            return iPromotionRepository.findByNameLike("%" + name + "%");
        } catch (Exception e) {
            throw new BadRequestException("Failed to search promotions by name", e.getMessage());
        }
    }

    @Override
    public ResponseData insertPromotion(MultipartFile file, String name, int idVoucherType, double value,
            boolean status) {
        ResponseData responseData = new ResponseData();
        PromotionDTO promotionDTO = new PromotionDTO();
        try {
            Promotion promotion = new Promotion();
            promotion.setName(name);
            promotion.setVoucherType(iVoucherTypeService.getVoucherById(idVoucherType));
            promotion.setValue(value);
            promotion.setStatus(status);

            // Check if the file is provided and save it
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
                // Use default image if no file is provided
                promotion.setImage("default_image.jpg");
            }

            promotionDTO = iPromotionRepository.save(promotion).getDTO();
            responseData.setData(promotionDTO);
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Insert successful");

        } catch (Exception e) {
            System.out.println(" Failed to insert promotion " + e.getMessage());
            throw new BadRequestException("Failed to insert promotion", e.getMessage());
        }

        return responseData;
    }

    @Override
    public List<PromotionDTO> getHomePagePromotion(int page) {
        try {
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
        } catch (Exception e) {
            throw new BadRequestException("Failed to get home page promotions", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getHomePagePromotion2(int page) {
        try {
            Map<String, Object> response = new HashMap<>();
            List<PromotionDTO> promotionDTOs = new ArrayList<>();
            PageRequest pageRequest = PageRequest.of(page, 8);
            Page<Promotion> listData = iPromotionRepository.findAll(pageRequest);

            for (Promotion p : listData) {
                promotionDTOs.add(p.getDTO());
            }

            response.put("promotions", promotionDTOs);
            response.put("totalPages", listData.getTotalPages());
            response.put("currentPage", page);

            return response;
        } catch (Exception e) {
            throw new BadRequestException("Failed to get home page promotions with pagination", e.getMessage());
        }
    }

    @Override
    public PromotionDTO updatePromotion(MultipartFile file, int id, String name, int idVoucherType, double value,
            boolean status) {
        try {
            Optional<Promotion> promotionOptional = iPromotionRepository.findById(id);
            PromotionDTO promotionDTO = new PromotionDTO();

            if (promotionOptional.isPresent()) {
                Promotion promotion = promotionOptional.get();
                promotion.setName(name);
                promotion.setVoucherType(iVoucherTypeService.getVoucherById(idVoucherType));
                promotion.setValue(value);
                promotion.setStatus(status);

                // Check if the file is provided and save it
                if (file != null && !file.isEmpty()) {
                    boolean isSaveFileSuccess = iFileService.savefile(file);
                    if (isSaveFileSuccess) {
                        promotion.setImage(file.getOriginalFilename());
                    }
                } else {
                    // Use the existing image if no new file is provided
                    promotion.setImage(promotion.getImage());
                }

                iPromotionRepository.save(promotion);
                promotionDTO = promotion.getDTO();
            }

            return promotionDTO;
        } catch (Exception e) {
            throw new BadRequestException("Failed to update promotion", e.getMessage());
        }
    }

}
