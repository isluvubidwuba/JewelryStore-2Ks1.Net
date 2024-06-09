package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IPromotionService {

        Map<String, Object> getHomePagePromotion(int page);

        ResponseData insertPromotion(MultipartFile file, String name, double value, boolean status, LocalDate start,
                        LocalDate end, String promotionType); // Thêm promotionType

        PromotionDTO updatePromotion(MultipartFile file, int id, String name, double value, boolean status,
                        LocalDate start, LocalDate end);

        PromotionDTO findById(int id);

        void deletePromotion(int id);

        void deleteExpiredPromotions();

        public List<PromotionDTO> getPromotionsByProductId(int productId);

        public List<PromotionDTO> getPromotionsByProductCategoryId(int productCategoryId);

        List<PromotionDTO> getAllPromotionByIdProduct(int productId);

        public List<PromotionDTO> getPromotionsByUserId(int userId);
}
