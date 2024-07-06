package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ForGemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForGemStoneType;
import com.ks1dotnet.jewelrystore.entity.GemStoneType;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForGemStoneTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IGemStoneTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionGenericService;
import jakarta.transaction.Transactional;

@Service
public class ForGemStoneTypeService implements IPromotionGenericService<GemStoneType> {

        @Autowired
        IForGemStoneTypeRepository iForGemStoneTypeRepository;

        @Autowired
        IGemStoneTypeRepository iGemStoneTypeRepository;

        @Autowired
        IPromotionRepository iPromotionRepository;

        @Transactional
        @Override
        public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
                try {
                        int promotionId = applyPromotionDTO.getPromotionId();
                        List<Integer> gemstoneTypeIds = applyPromotionDTO.getEntityIds();

                        Promotion promotion = iPromotionRepository.findById(promotionId)
                                        .orElseThrow(() -> new ApplicationException("Not found",
                                                        HttpStatus.NOT_FOUND));
                        PromotionDTO promotionDTO = promotion.getDTO();
                        if (promotionDTO == null) {
                                throw new ApplicationException(
                                                "Promotion not found with id: " + promotionId,
                                                HttpStatus.NOT_FOUND);
                        } else if (!promotionDTO.getPromotionType().equals("gemstone")) {
                                throw new ApplicationException(
                                                "Not allowed to apply this promotion type",
                                                HttpStatus.NOT_ACCEPTABLE);
                        }

                        List<GemStoneType> gemstoneTypes =
                                        iGemStoneTypeRepository.findAllById(gemstoneTypeIds);
                        if (gemstoneTypes.isEmpty()) {
                                throw new ApplicationException(
                                                "No gemstone types found with the given ids",
                                                HttpStatus.NOT_FOUND);
                        }

                        List<ForGemStoneType> forGemStoneTypesToSave = new ArrayList<>();
                        for (GemStoneType gemstoneType : gemstoneTypes) {
                                List<ForGemStoneType> activePromotions = iForGemStoneTypeRepository
                                                .findActiveGemStoneTypePromotionsByGemStoneTypeIdAndInvoiceTypeId(
                                                                gemstoneType.getId(),
                                                                promotion.getInvoiceType().getId());

                                for (ForGemStoneType activePromotion : activePromotions) {
                                        activePromotion.setStatus(false);
                                        forGemStoneTypesToSave.add(activePromotion);
                                }

                                ForGemStoneType existingForGemStoneType = iForGemStoneTypeRepository
                                                .findByPromotionIdAndGemStoneTypeId(promotionId,
                                                                gemstoneType.getId());
                                if (existingForGemStoneType != null) {
                                        existingForGemStoneType.setStatus(true);
                                        forGemStoneTypesToSave.add(existingForGemStoneType);
                                } else {
                                        forGemStoneTypesToSave.add(new ForGemStoneType(promotion,
                                                        gemstoneType, true));
                                }
                        }

                        iForGemStoneTypeRepository.saveAll(forGemStoneTypesToSave);
                        List<ForGemStoneTypeDTO> forGemStoneTypeDTOs =
                                        forGemStoneTypesToSave.stream().map(ForGemStoneType::getDTO)
                                                        .collect(Collectors.toList());

                        return new ResponseData(HttpStatus.OK,
                                        "Promotion applied to gemstone types successfully",
                                        forGemStoneTypeDTOs);
                } catch (ApplicationException e) {
                        throw new ApplicationException(
                                        "Error at applyPromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        e.getErrorString(), e.getStatus());
                } catch (Exception e) {
                        throw new ApplicationException(
                                        "Error at applyPromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        "Failed to apply promotion to gemstone types");
                }
        }

        @Override
        public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
                try {
                        int promotionId = applyPromotionDTO.getPromotionId();
                        List<Integer> gemstoneTypeIds = applyPromotionDTO.getEntityIds();

                        if (!iPromotionRepository.existsById(promotionId)) {
                                throw new ApplicationException(
                                                "Promotion not found with id: " + promotionId,
                                                HttpStatus.NOT_FOUND);
                        }

                        List<ForGemStoneType> forGemStoneTypes = iForGemStoneTypeRepository
                                        .findByPromotionIdAndGemStoneTypeIds(promotionId,
                                                        gemstoneTypeIds);

                        if (forGemStoneTypes.isEmpty()) {
                                throw new ApplicationException(
                                                "No gemstone types found with the given ids in the promotion",
                                                HttpStatus.NOT_FOUND);
                        }

                        forGemStoneTypes.forEach(
                                        forGemStoneType -> forGemStoneType.setStatus(false));
                        iForGemStoneTypeRepository.saveAll(forGemStoneTypes);

                        return new ResponseData(HttpStatus.OK,
                                        "Promotion removed from gemstone types successfully", null);
                } catch (ApplicationException e) {
                        throw new ApplicationException(
                                        "Error at removePromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        e.getErrorString(), e.getStatus());
                } catch (Exception e) {
                        throw new ApplicationException(
                                        "Error at removePromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        "Failed to remove promotion from gemstone types");
                }
        }

        @Override
        public ResponseData checkInOtherActivePromotions(int entityId, int promotionId) {
                try {
                        List<ForGemStoneType> forGemStoneTypes = iForGemStoneTypeRepository
                                        .findActiveGemStoneTypePromotionsByGemStoneTypeIdAndInvoiceTypeId(
                                                        entityId,
                                                        iPromotionRepository.findById(promotionId)
                                                                        .orElseThrow(() -> new ApplicationException(
                                                                                        "Promotion not found",
                                                                                        HttpStatus.NOT_FOUND))
                                                                        .getInvoiceType().getId());

                        if (forGemStoneTypes.isEmpty()) {
                                return new ResponseData(HttpStatus.OK,
                                                "Gemstone type is not in any other active promotions",
                                                null);
                        }

                        List<PromotionDTO> otherPromotions = forGemStoneTypes.stream()
                                        .filter(fg -> fg.getPromotion().getId() != promotionId)
                                        .map(fg -> fg.getPromotion().getDTO())
                                        .collect(Collectors.toList());

                        if (otherPromotions.isEmpty()) {
                                return new ResponseData(HttpStatus.OK,
                                                "Gemstone type is not in any other active promotions",
                                                null);
                        } else {
                                return new ResponseData(HttpStatus.CONFLICT,
                                                "Gemstone type is active in other promotions",
                                                otherPromotions);
                        }
                } catch (ApplicationException e) {
                        throw new ApplicationException(
                                        "Error at checkInOtherActivePromotions ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        e.getErrorString(), e.getStatus());
                } catch (Exception e) {
                        throw new ApplicationException(
                                        "Error at checkInOtherActivePromotions ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        "Failed to check gemstone type in other active promotions");
                }
        }

        @Override
        public ResponseData getEntitiesNotInPromotion(int promotionId) {
                try {
                        List<GemStoneType> gemstoneTypes = iForGemStoneTypeRepository
                                        .findGemStoneTypesNotInPromotion(promotionId);
                        if (gemstoneTypes == null || gemstoneTypes.isEmpty()) {
                                throw new ApplicationException(
                                                "No gemstone types found not in the given promotion id: "
                                                                + promotionId,
                                                HttpStatus.NOT_FOUND);
                        }

                        List<GemStoneTypeDTO> gemstoneTypeDTOs = gemstoneTypes.stream()
                                        .map(GemStoneType::getDTO).collect(Collectors.toList());

                        return new ResponseData(HttpStatus.OK,
                                        "Gemstone types not in promotion found successfully",
                                        gemstoneTypeDTOs);
                } catch (ApplicationException e) {
                        throw new ApplicationException(
                                        "Error at getEntitiesNotInPromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        e.getErrorString(), e.getStatus());
                } catch (Exception e) {
                        throw new ApplicationException(
                                        "Error at getEntitiesNotInPromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        "Failed to get gemstone types not in promotion");
                }
        }

        @Override
        public ResponseData getEntitiesInPromotion(int promotionId) {
                try {
                        Promotion promotion = iPromotionRepository.findById(promotionId)
                                        .orElseThrow(() -> new NotFoundException());
                        List<ForGemStoneType> forGemStoneTypes = iForGemStoneTypeRepository
                                        .findByPromotionId(promotion.getId());
                        if (forGemStoneTypes == null || forGemStoneTypes.isEmpty()) {
                                throw new ApplicationException(
                                                "No gemstone types found for the given promotion id: "
                                                                + promotionId,
                                                HttpStatus.NOT_FOUND);
                        }

                        List<ForGemStoneTypeDTO> forGemStoneTypeDTOs = forGemStoneTypes.stream()
                                        .map(ForGemStoneType::getDTO).collect(Collectors.toList());

                        return new ResponseData(HttpStatus.OK, "Gemstone types found successfully",
                                        forGemStoneTypeDTOs);
                } catch (ApplicationException e) {
                        throw new ApplicationException(
                                        "Error at getEntitiesInPromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        e.getErrorString(), e.getStatus());
                } catch (Exception e) {
                        throw new ApplicationException(
                                        "Error at getEntitiesInPromotion ForGemStoneStypeService: "
                                                        + e.getMessage(),
                                        "Failed to get gemstone types by promotion id");
                }
        }
}
