package com.ks1dotnet.jewelrystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.CustomerTypeDTO;
import com.ks1dotnet.jewelrystore.dto.ForCustomerDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.CustomerType;
import com.ks1dotnet.jewelrystore.entity.ForCustomer;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.ICustomerTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IForCustomerRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionGenericService;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForCustomerService implements IPromotionGenericService<CustomerType> {

    @Autowired
    IForCustomerRepository iForCustomerRepository;

    @Autowired
    ICustomerTypeRepository iCustomerTypeRepository;

    @Autowired
    IPromotionRepository iPromotionRepository;

    @Transactional
    @Override
    public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> customerTypeIds = applyPromotionDTO.getEntityIds();

            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new ApplicationException("Not found", HttpStatus.NOT_FOUND));
            PromotionDTO promotionDTO = promotion.getDTO();
            if (promotionDTO == null) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            } else if (!promotionDTO.getPromotionType().equals("customer")) {
                throw new ApplicationException("Not allowed to apply this promotion type",
                        HttpStatus.NOT_ACCEPTABLE);
            }

            List<CustomerType> customerTypes = iCustomerTypeRepository.findAllById(customerTypeIds);
            if (customerTypes.isEmpty()) {
                throw new ApplicationException("No customer types found with the given ids",
                        HttpStatus.NOT_FOUND);
            }

            List<ForCustomer> forCustomersToSave = new ArrayList<>();
            for (CustomerType customerType : customerTypes) {
                List<ForCustomer> activePromotions = iForCustomerRepository
                        .findActiveCustomerTypePromotionsByCustomerTypeIdAndInvoiceTypeId(
                                customerType.getId(), promotion.getInvoiceType().getId());

                for (ForCustomer activePromotion : activePromotions) {
                    activePromotion.setStatus(false);
                    forCustomersToSave.add(activePromotion);
                }

                ForCustomer existingForCustomer = iForCustomerRepository
                        .findByPromotionIdAndCustomerTypeId(promotionId, customerType.getId());
                if (existingForCustomer != null) {
                    existingForCustomer.setStatus(true);
                    forCustomersToSave.add(existingForCustomer);
                } else {
                    forCustomersToSave.add(new ForCustomer(promotion, customerType, true));
                }
            }

            iForCustomerRepository.saveAll(forCustomersToSave);
            List<ForCustomerDTO> forCustomerDTOs = forCustomersToSave.stream()
                    .map(ForCustomer::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK,
                    "Promotion applied to customer types successfully", forCustomerDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForCustomerService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForCustomerService: " + e.getMessage(),
                    "Failed to apply promotion to customer types");
        }
    }

    @Override
    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> customerTypeIds = applyPromotionDTO.getEntityIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForCustomer> forCustomers = iForCustomerRepository
                    .findByPromotionIdAndCustomerTypeIds(promotionId, customerTypeIds);

            if (forCustomers.isEmpty()) {
                throw new ApplicationException(
                        "No customer types found with the given ids in the promotion",
                        HttpStatus.NOT_FOUND);
            }

            forCustomers.forEach(forCustomer -> forCustomer.setStatus(false));
            iForCustomerRepository.saveAll(forCustomers);

            return new ResponseData(HttpStatus.OK,
                    "Promotion removed from customer types successfully", null);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at removePromotion ForCustomerService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at removePromotion ForCustomerService: " + e.getMessage(),
                    "Failed to remove promotion from customer types");
        }
    }

    @Override
    public ResponseData checkInOtherActivePromotions(int entityId, int promotionId) {
        try {
            List<ForCustomer> forCustomers = iForCustomerRepository
                    .findActiveCustomerTypePromotionsByCustomerTypeIdAndInvoiceTypeId(entityId,
                            iPromotionRepository.findById(promotionId)
                                    .orElseThrow(() -> new ApplicationException(
                                            "Promotion not found", HttpStatus.NOT_FOUND))
                                    .getInvoiceType().getId());

            if (forCustomers.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Customer type is not in any other active promotions", null);
            }

            List<PromotionDTO> otherPromotions =
                    forCustomers.stream().filter(fg -> fg.getPromotion().getId() != promotionId)
                            .map(fg -> fg.getPromotion().getDTO()).collect(Collectors.toList());

            if (otherPromotions.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Customer type is not in any other active promotions", null);
            } else {
                return new ResponseData(HttpStatus.CONFLICT,
                        "Customer type is active in other promotions", otherPromotions);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForCustomerService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForCustomerService: " + e.getMessage(),
                    "Failed to check customer type in other active promotions");
        }
    }

    @Override
    public ResponseData getEntitiesNotInPromotion(int promotionId) {
        try {
            List<CustomerType> customerTypes =
                    iForCustomerRepository.findCustomerTypesNotInPromotion(promotionId);
            if (customerTypes == null || customerTypes.isEmpty()) {
                throw new ApplicationException(
                        "No customer types found not in the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<CustomerTypeDTO> customerTypeDTOs =
                    customerTypes.stream().map(CustomerType::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK,
                    "Customer types not in promotion found successfully", customerTypeDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForCustomerService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForCustomerService: " + e.getMessage(),
                    "Failed to get customer types not in promotion");
        }
    }

    @Override
    public ResponseData getEntitiesInPromotion(int promotionId) {
        try {
            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new NotFoundException());
            List<ForCustomer> forCustomers =
                    iForCustomerRepository.findByPromotionId(promotion.getId());
            if (forCustomers == null || forCustomers.isEmpty()) {
                throw new ApplicationException(
                        "No customer types found for the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForCustomerDTO> forCustomerDTOs =
                    forCustomers.stream().map(ForCustomer::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Customer types found successfully",
                    forCustomerDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForCustomerService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForCustomerService: " + e.getMessage(),
                    "Failed to get customer types by promotion id");
        }
    }
}
