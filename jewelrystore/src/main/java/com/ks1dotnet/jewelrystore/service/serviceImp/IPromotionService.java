package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IPromotionService {
        public List<Promotion> findAll();

        public List<PromotionDTO> getHomePagePromotion(int page);

        public List<Promotion> searchByName(String name);

        public Promotion saveOrUpdatePromotion(Promotion promotion);

        public ResponseData insertPromotion(MultipartFile file,
                        String name,
                        int idVoucherType,
                        double value,
                        boolean status);

        public PromotionDTO updatePromotion(MultipartFile file, int id,
                        String name,
                        int idVoucherType,
                        double value,
                        boolean status);

        public Promotion findById(int id);

        public Map<String, Object> getHomePagePromotion2(int page);

}
