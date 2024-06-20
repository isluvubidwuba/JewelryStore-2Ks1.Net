package com.ks1dotnet.jewelrystore.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.Enum.EntityType;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGenericPromotionService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionGenericService;

@Service
public class GenericPromotionService implements IGenericPromotionService {

    private final Map<EntityType, IPromotionGenericService<?>> promotionServices = new HashMap<>();

    public GenericPromotionService(List<IPromotionGenericService<?>> services) {
        for (IPromotionGenericService<?> service : services) {
            EntityType entityType = getEntityType(service);
            promotionServices.put(entityType, service);
        }
    }

    private EntityType getEntityType(IPromotionGenericService<?> service) {
        if (service instanceof ForGemStoneTypeService) {
            return EntityType.GEMSTONE;
        } else if (service instanceof ForCustomerService) {
            return EntityType.CUSTOMER;
        } else if (service instanceof ForProductService) {
            return EntityType.PRODUCT;
        } else if (service instanceof ForProductTypeService) {
            return EntityType.CATEGORY;
        } else if (service instanceof ForMaterialService) { // Thêm dòng này cho ForMaterialService
            return EntityType.MATERIAL;
        } else {
            throw new IllegalArgumentException("Unknown service type: " + service.getClass().getName());
        }
    }

    @Override
    public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
        EntityType entityType = applyPromotionDTO.getEntityType();
        IPromotionGenericService<?> service = promotionServices.get(entityType);
        return service.applyPromotion(applyPromotionDTO);
    }

    @Override
    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
        EntityType entityType = applyPromotionDTO.getEntityType();
        IPromotionGenericService<?> service = promotionServices.get(entityType);
        return service.removePromotion(applyPromotionDTO);
    }

    @Override
    public ResponseData checkEntityInOtherPromotions(EntityType entityType, int entityId, int promotionId) {
        IPromotionGenericService<?> service = promotionServices.get(entityType);
        return service.checkInOtherActivePromotions(entityId, promotionId);
    }

    @Override
    public ResponseData getEntitiesNotInPromotion(EntityType entityType, int promotionId) {
        IPromotionGenericService<?> service = promotionServices.get(entityType);
        return service.getEntitiesNotInPromotion(promotionId);
    }

    @Override
    public ResponseData getEntitiesInPromotion(EntityType entityType, int promotionId) {
        IPromotionGenericService<?> service = promotionServices.get(entityType);
        return service.getEntitiesInPromotion(promotionId);
    }
}
