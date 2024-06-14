package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IPromotionService {
        public ResponseData getAllPromotionDTO();

        // Map<String, Object> getHomePagePromotion(int page);

        ResponseData insertPromotion(MultipartFile file, String name, double value, boolean status, LocalDate start,
                        LocalDate end, String promotionType, int invoiceTypeId);

        PromotionDTO updatePromotion(MultipartFile file, int id, String name, double value, boolean status,
                        LocalDate start, LocalDate end);

        PromotionDTO findById(int id);

        void deletePromotion(int id);

        void deleteExpiredPromotions();

        public List<Promotion> getAllPromotionByProductAndInvoiceType(Product product, int invoiceId);

        public PromotionDTO getPromotionsByUserId(int userId);

        public List<Promotion> findByInvoiceTypeAndStatusTrue(InvoiceType invoiceType);

}
