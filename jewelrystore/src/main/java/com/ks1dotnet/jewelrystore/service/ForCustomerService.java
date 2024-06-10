package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ForCustomerDTO;
import com.ks1dotnet.jewelrystore.dto.CustomerTypeDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForCustomer;
import com.ks1dotnet.jewelrystore.entity.CustomerType;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForCustomerRepository;
import com.ks1dotnet.jewelrystore.repository.ICustomerTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForCustomerService;

import jakarta.transaction.Transactional;

@Service
public class ForCustomerService implements IForCustomerService {

    @Autowired
    IForCustomerRepository iForCustomerRepository;

    @Autowired
    ICustomerTypeRepository iCustomerTypeRepository;

    @Autowired
    IPromotionRepository iPromotionRepository;

    @Override
    @Transactional
    public ResponseData getCustomerTypesByPromotionId(int promotionId) {
        try {
            List<ForCustomer> forCustomers = iForCustomerRepository.findCustomerTypesByPromotionId(promotionId);
            if (forCustomers == null || forCustomers.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No customer types found for the given promotion id: " + promotionId);
            }

            PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(promotionId);
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            }
            if (!promotionDTO.getPromotionType().equals("customer")) {
                throw new BadRequestException("Not allow");
            }

            List<ForCustomerDTO> forCustomerDTOs = forCustomers.stream().map(fc -> new ForCustomerDTO(
                    fc.getId(),
                    fc.getPromotion().getDTO(),
                    fc.getCustomerType().getDTO(), fc.isStatus())).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Customer types found successfully", forCustomerDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get customer types by promotion id", e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseData applyPromotionToCustomerTypes(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> customerTypeIds = applyPromotionDTO.getProductIds();

            PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(promotionId);
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            } else if (!promotionDTO.getPromotionType().equals("customer")) {
                throw new BadRequestException("Not allow to apply this promotion type");
            }

            List<CustomerType> customerTypes = iCustomerTypeRepository.findAllById(customerTypeIds);
            if (customerTypes.isEmpty()) {
                throw new BadRequestException("No customer types found with the given ids");
            }

            List<ForCustomer> forCustomersToSave = new ArrayList<>();
            for (CustomerType customerType : customerTypes) {
                // Kiểm tra các khuyến mãi khác mà loại khách hàng đang tham gia
                ResponseData checkResponse = checkCustomerTypeInOtherActivePromotions(customerType.getId(),
                        promotionId);
                if (checkResponse.getStatus() == HttpStatus.CONFLICT) {
                    List<PromotionDTO> otherPromotions = (List<PromotionDTO>) checkResponse.getData();
                    for (PromotionDTO otherPromotion : otherPromotions) {
                        // Tắt trạng thái của loại khách hàng trong các khuyến mãi khác
                        ApplyPromotionDTO removeDTO = new ApplyPromotionDTO();
                        removeDTO.setPromotionId(otherPromotion.getId());
                        removeDTO.setProductIds(Collections.singletonList(customerType.getId()));
                        removePromotionFromCustomerTypes(removeDTO);
                    }
                }

                // Thêm hoặc cập nhật trạng thái khuyến mãi hiện tại cho loại khách hàng
                ForCustomer existingForCustomer = iForCustomerRepository
                        .findByPromotionIdAndCustomerTypeId(promotionId, customerType.getId());
                if (existingForCustomer != null) {
                    // Update status to true if it exists
                    existingForCustomer.setStatus(true);
                    forCustomersToSave.add(existingForCustomer);
                } else {
                    // Create new record if it does not exist
                    forCustomersToSave.add(new ForCustomer(new Promotion(promotionDTO), customerType, true));
                }
            }

            iForCustomerRepository.saveAll(forCustomersToSave);
            List<ForCustomerDTO> forCustomerDTOs = forCustomersToSave.stream()
                    .map(fc -> new ForCustomerDTO(fc.getId(), fc.getPromotion().getDTO(),
                            fc.getCustomerType().getDTO(), fc.isStatus()))
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Promotion applied to customer types successfully", forCustomerDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply promotion to customer types", e.getMessage());
        }
    }

    @Override
    public ResponseData removePromotionFromCustomerTypes(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> customerTypeIds = applyPromotionDTO.getProductIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            }

            List<ForCustomer> forCustomers = iForCustomerRepository
                    .findByPromotionIdAndCustomerTypeIds(promotionId, customerTypeIds);

            if (forCustomers.isEmpty()) {
                throw new BadRequestException("No customer types found with the given ids in the promotion");
            }

            forCustomers.forEach(forCustomer -> forCustomer.setStatus(false));
            iForCustomerRepository.saveAll(forCustomers);

            return new ResponseData(HttpStatus.OK, "Promotion removed from customer types successfully", null);
        } catch (Exception e) {
            throw new BadRequestException("Failed to remove promotion from customer types", e.getMessage());
        }
    }

    @Override
    public ResponseData checkCustomerTypeInOtherActivePromotions(int customerTypeId, int currentPromotionId) {
        List<ForCustomer> forCustomers = iForCustomerRepository
                .findActiveCustomerPromotionsByCustomerTypeId(customerTypeId);

        if (forCustomers.isEmpty()) {
            return new ResponseData(HttpStatus.OK, "Customer type is not in any other active promotions", null);
        }

        List<PromotionDTO> otherPromotions = new ArrayList<>();
        for (ForCustomer forCustomer : forCustomers) {
            if (forCustomer.getPromotion().getId() != currentPromotionId) {
                otherPromotions.add(forCustomer.getPromotion().getDTO());
            }
        }

        if (otherPromotions.isEmpty()) {
            return new ResponseData(HttpStatus.OK, "Customer type is not in any other active promotions", null);
        } else {
            return new ResponseData(HttpStatus.CONFLICT, "Customer type is active in other promotions",
                    otherPromotions);
        }
    }

    @Override
    public ResponseData getCustomerTypesNotInPromotion(int promotionId) {
        try {
            PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(promotionId);
            if (!promotionDTO.getPromotionType().equals("customer")) {
                throw new BadRequestException(" Not allow");
            }
            List<CustomerType> customerTypes = iForCustomerRepository.findCustomerTypesNotInPromotion(promotionId);
            List<CustomerTypeDTO> customerTypeDTOs = customerTypes.stream().map(CustomerType::getDTO)
                    .collect(Collectors.toList());
            return new ResponseData(HttpStatus.OK, "Customer types not in promotion found successfully",
                    customerTypeDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get customer types not in promotion", e.getMessage());
        }
    }
}
