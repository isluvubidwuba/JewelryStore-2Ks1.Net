package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ForMaterialDTO;
import com.ks1dotnet.jewelrystore.dto.MaterialDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForMaterial;
import com.ks1dotnet.jewelrystore.entity.Material;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForMaterialRepository;
import com.ks1dotnet.jewelrystore.repository.IMaterialRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionGenericService;
import jakarta.transaction.Transactional;

@Service
public class ForMaterialService implements IPromotionGenericService<Material> {

    @Autowired
    IForMaterialRepository iForMaterialRepository;

    @Autowired
    IMaterialRepository iMaterialRepository;

    @Autowired
    IPromotionRepository iPromotionRepository;

    @Transactional
    @Override
    public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> materialIds = applyPromotionDTO.getEntityIds();

            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new ApplicationException("Not found", HttpStatus.NOT_FOUND));
            PromotionDTO promotionDTO = promotion.getDTO();
            if (promotionDTO == null) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            } else if (!promotionDTO.getPromotionType().equals("material")) {
                throw new ApplicationException("Not allowed to apply this promotion type",
                        HttpStatus.NOT_ACCEPTABLE);
            }

            List<Material> materials = iMaterialRepository.findAllById(materialIds);
            if (materials.isEmpty()) {
                throw new ApplicationException("No materials found with the given ids",
                        HttpStatus.NOT_FOUND);
            }

            List<ForMaterial> forMaterialsToSave = new ArrayList<>();
            for (Material material : materials) {
                List<ForMaterial> activePromotions = iForMaterialRepository
                        .findActiveMaterialPromotionsByMaterialIdAndInvoiceTypeId(material.getId(),
                                promotion.getInvoiceType().getId());

                for (ForMaterial activePromotion : activePromotions) {
                    activePromotion.setStatus(false);
                    forMaterialsToSave.add(activePromotion);
                }

                ForMaterial existingForMaterial = iForMaterialRepository
                        .findByPromotionIdAndMaterialId(promotionId, material.getId());
                if (existingForMaterial != null) {
                    existingForMaterial.setStatus(true);
                    forMaterialsToSave.add(existingForMaterial);
                } else {
                    forMaterialsToSave.add(new ForMaterial(promotion, material, true));
                }
            }

            iForMaterialRepository.saveAll(forMaterialsToSave);
            List<ForMaterialDTO> forMaterialDTOs = forMaterialsToSave.stream()
                    .map(ForMaterial::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Promotion applied to materials successfully",
                    forMaterialDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForMaterialService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForMaterialService: " + e.getMessage(),
                    "Failed to apply promotion to materials");
        }
    }

    @Override
    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> materialIds = applyPromotionDTO.getEntityIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForMaterial> forMaterials = iForMaterialRepository
                    .findByPromotionIdAndMaterialIds(promotionId, materialIds);

            if (forMaterials.isEmpty()) {
                throw new ApplicationException(
                        "No materials found with the given ids in the promotion",
                        HttpStatus.NOT_FOUND);
            }

            forMaterials.forEach(forMaterial -> forMaterial.setStatus(false));
            iForMaterialRepository.saveAll(forMaterials);

            return new ResponseData(HttpStatus.OK, "Promotion removed from materials successfully",
                    null);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at removePromotion ForMaterialService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at removePromotion ForMaterialService: " + e.getMessage(),
                    "Failed to remove promotion from materials");
        }
    }

    @Override
    public ResponseData checkInOtherActivePromotions(int entityId, int promotionId) {
        try {
            List<ForMaterial> forMaterials = iForMaterialRepository
                    .findActiveMaterialPromotionsByMaterialIdAndInvoiceTypeId(entityId,
                            iPromotionRepository.findById(promotionId)
                                    .orElseThrow(() -> new ApplicationException(
                                            "Promotion not found", HttpStatus.NOT_FOUND))
                                    .getInvoiceType().getId());

            if (forMaterials.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Material is not in any other active promotions", null);
            }

            List<PromotionDTO> otherPromotions =
                    forMaterials.stream().filter(fg -> fg.getPromotion().getId() != promotionId)
                            .map(fg -> fg.getPromotion().getDTO()).collect(Collectors.toList());

            if (otherPromotions.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Material is not in any other active promotions", null);
            } else {
                return new ResponseData(HttpStatus.CONFLICT,
                        "Material is active in other promotions", otherPromotions);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForMaterialService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForMaterialService: " + e.getMessage(),
                    "Failed to check material in other active promotions");
        }
    }

    @Override
    public ResponseData getEntitiesNotInPromotion(int promotionId) {
        try {
            List<Material> materials =
                    iForMaterialRepository.findMaterialsNotInPromotion(promotionId);
            if (materials == null || materials.isEmpty()) {
                throw new ApplicationException(
                        "No materials found not in the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<MaterialDTO> materialDTOs =
                    materials.stream().map(Material::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Materials not in promotion found successfully",
                    materialDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForMaterialService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForMaterialService: " + e.getMessage(),
                    "Failed to get materials not in promotion");
        }
    }

    @Override
    public ResponseData getEntitiesInPromotion(int promotionId) {
        try {
            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new NotFoundException());
            List<ForMaterial> forMaterials =
                    iForMaterialRepository.findByPromotionId(promotion.getId());
            if (forMaterials == null || forMaterials.isEmpty()) {
                throw new ApplicationException(
                        "No materials found for the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForMaterialDTO> forMaterialDTOs =
                    forMaterials.stream().map(ForMaterial::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Materials found successfully", forMaterialDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForMaterialService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForMaterialService: " + e.getMessage(),
                    "Failed to get materials by promotion id");
        }
    }
}
