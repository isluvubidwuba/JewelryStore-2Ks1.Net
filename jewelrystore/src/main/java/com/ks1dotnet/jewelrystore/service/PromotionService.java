package com.ks1dotnet.jewelrystore.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForCustomer;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForCustomerRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;

@Service
public class PromotionService implements IPromotionService {
    @Autowired
    private IPromotionRepository iPromotionRepository;

    @Autowired
    private FirebaseStorageService firebaseStorageService;
    @Autowired
    private IForCustomerRepository iForCustomerRepository;

    @Autowired
    private IInvoiceTypeRepository iInvoiceTypeRepository;

    @Value("${fileUpload.promotionPath}")
    private String filePath;

    @Value("${firebase.img-url}")
    private String url;

    @Override
    public ResponseData getAllPromotionDTO() {
        List<PromotionDTO> promotionDTOs = iPromotionRepository.findAll().stream().map(promotion -> {
            PromotionDTO dto = promotion.getDTO();
            dto.setImage(url.trim() + filePath.trim() + dto.getImage());
            return dto;
        }).collect(Collectors.toList());
        return new ResponseData(HttpStatus.OK, "Fetched all exchange rate policies", promotionDTOs);
    }

    @Override
    public ResponseData insertPromotion(MultipartFile file, String name, double value,
            boolean status, LocalDate startDate, LocalDate endDate, String promotionType,
            int invoiceTypeId) { // Thêm invoiceTypeId vào
                                 // đây
        ResponseData responseData = new ResponseData();
        try {
            Promotion promotion = new Promotion();
            promotion.setName(name);
            promotion.setValue(value);
            promotion.setStatus(status);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setLastModified(LocalDate.now());
            promotion.setPromotionType(promotionType);

            // Lấy InvoiceType từ cơ sở dữ liệu và thiết lập vào promotion
            InvoiceType invoiceTypeC = iInvoiceTypeRepository.findById(invoiceTypeId)
                    .orElseThrow(() -> new ApplicationException(
                            "Not found invoice type! Invalid invoice type ID. ",
                            HttpStatus.NOT_FOUND));
            promotion.setInvoiceType(invoiceTypeC);

            String fileName;
            if (file != null && !file.isEmpty()) {
                fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
            } else {
                fileName = "25b59396-a85d-4272-bdde-c547123f7832_2024-06-13";
            }
            promotion.setImage(fileName);
            PromotionDTO promotionDTO = iPromotionRepository.save(promotion).getDTO();
            responseData.setData(promotionDTO);
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Insert successful");
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at insertPromotion PromotionService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at insertPromotion PromotionService: " + e.getMessage(),
                    "Failed to insert promotion");
        }

        return responseData;
    }

    @Override

    public PromotionDTO updatePromotion(MultipartFile file, int id, String name, double value,
            boolean status, LocalDate startDate, LocalDate endDate) {
        try {
            Promotion promotion = iPromotionRepository.findById(id).orElseThrow(
                    () -> new ApplicationException("Promotion not found with id: " + id,
                            HttpStatus.NOT_FOUND));

            promotion.setName(name);
            promotion.setValue(value);
            promotion.setStatus(status);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setLastModified(LocalDate.now());

            // InvoiceType invoiceTypeC = iInvoiceTypeRepository.findById(invoiceTypeId)
            // .orElseThrow(() -> new BadRequestException("Not found invoice type! Invalid
            // invoice type ID. "));
            // promotion.setInvoiceType(invoiceTypeC);

            String fileName;
            if (file != null && !file.isEmpty()) {
                fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
            } else {
                fileName = promotion.getImage();
            }

            promotion.setImage(fileName);
            promotion = iPromotionRepository.save(promotion);
            return promotion.getDTO();
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at updatePromotion PromotionService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at updatePromotion PromotionService: " + e.getMessage(),
                    "Failed to update promotion");
        }
    }

    @Override
    public PromotionDTO findById(int id) {
        // PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(id);
        Promotion promotion = iPromotionRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Not found promotion with id: " + id,
                        HttpStatus.NOT_FOUND));
        PromotionDTO promotionDTO = promotion.getDTO();
        promotionDTO.setImage(url.trim() + filePath.trim() + promotionDTO.getImage());
        return promotionDTO;
    }

    @Override
    public void deletePromotion(int id) {
        try {
            Promotion promotion = iPromotionRepository.findById(id).orElseThrow(
                    () -> new ApplicationException("Promotion not found with id: " + id,
                            HttpStatus.NOT_FOUND));
            promotion.setStatus(false);
            iPromotionRepository.save(promotion);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at deletePromotion PromotionService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at deletePromotion PromotionService: " + e.getMessage(),
                    "Failed to delete promotion");
        }
    }

    @Override
    public ResponseData deleteExpiredPromotions() {
        try {
            List<Promotion> expiredPromotions = iPromotionRepository.findByEndDateBefore(LocalDate.now());
            for (Promotion promotion : expiredPromotions) {
                promotion.setStatus(false);
                iPromotionRepository.save(promotion);
            }

        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at deleteExpiredPromotions PromotionService: " + e.getMessage(),
                    "Failed to delete expired promotions");
        }
        return new ResponseData(HttpStatus.OK, "Valid expired promotion", true);
    }

    @Override
    public List<Promotion> getAllPromotionByProductAndInvoiceType(Product product, int invoiceId) {
        try {
            List<Promotion> promotions = iPromotionRepository.findPromotionsByCriteria(invoiceId,
                    product.getId(), product.getProductCategory().getId());

            return promotions;
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getAllPromotionByProductAndInvoiceType PromotionService: "
                            + e.getMessage(),
                    "Failed get promotion by id product");
        }
    }

    @Override
    public PromotionDTO getPromotionsByUserId(int userId) {
        ForCustomer forCustomers = iForCustomerRepository.findActivePromotionsByUserId(userId);
        PromotionDTO promotionDTO = null;
        if (forCustomers != null) {
            promotionDTO = forCustomers.getPromotion().getDTO();
            promotionDTO.setImage(url.trim() + filePath.trim() + promotionDTO.getImage());
        }
        return promotionDTO;
    }

    @Override
    public List<PromotionDTO> findByInvoiceTypeAndStatusTrue(InvoiceType invoiceType) {

        List<PromotionDTO> promotionDTOs = iPromotionRepository.findByInvoiceTypeAndStatusTrue(invoiceType).stream()
                .map(promotion -> {
                    PromotionDTO dto = promotion.getDTO();
                    dto.setImage(url.trim() + filePath.trim() + dto.getImage());
                    return dto;
                }).collect(Collectors.toList());

        return promotionDTOs;
    }

}
