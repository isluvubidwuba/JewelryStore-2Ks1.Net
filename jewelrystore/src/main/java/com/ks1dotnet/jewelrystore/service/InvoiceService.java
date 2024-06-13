package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.entity.InvoiceDetail;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.repository.IOrderInvoiceDetailRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IUserInfoService;

@Service
public class InvoiceService implements IInvoiceService {

        @Autowired
        private IPromotionService promotionService;

        @Autowired
        private IInvoiceTypeService invoiceTypeService;

        @Autowired
        private IUserInfoService userInfoService;

        @Autowired
        private IInvoiceRepository invoiceRepository;

        @Autowired
        private IOrderInvoiceDetailRepository invoiceDetailRepository;
        @Autowired
        private IProductRepository iProductRepository;

        public InvoiceDetailDTO createInvoiceDetail(String barcode, Integer invoiceTypeId, Integer quantity) {

                Product product = iProductRepository.findByBarCode(barcode);
                if (product == null) {
                        throw new BadRequestException("Product not found.");
                }

                if (product.getInventory().getQuantity() <= 0) {
                        throw new BadRequestException("Product is out of stock.");
                }
                if (!product.isStatus()) {
                        throw new BadRequestException("Product is not sold.");
                }

                InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
                // UserInfo userInfo = userInfoService.findById(userId);
                List<Promotion> promotions = promotionService.getAllPromotionByProductAndInvoiceType(product,
                                invoiceType.getId());

                return calculateInvoiceDetail(product, quantity, promotions, invoiceType);
        }

        private InvoiceDetailDTO calculateInvoiceDetail(Product product, int quantity, List<Promotion> promotions,
                        InvoiceType invoiceType) {
                double priceBefore = 0;
                List<Promotion> promotionForGemstone = promotions.stream()
                                .filter(promotion -> promotion.getPromotionType().equals("gemstone"))
                                .collect(Collectors.toList());

                Promotion promotionForMaterial = promotions.stream()
                                .filter(promotion -> promotion.getPromotionType().equals("material"))
                                .findFirst()
                                .orElse(null);

                Promotion promotionForProduct = promotions.stream()
                                .filter(promotion -> promotion.getPromotionType().equals("product"))
                                .findFirst()
                                .orElse(null);

                Promotion promotionForProductType = promotions.stream()
                                .filter(promotion -> promotion.getPromotionType().equals("category"))
                                .findFirst()
                                .orElse(null);

                double materialPrice = product.getMaterial().getPriceAtTime() * product.getWeight();
                priceBefore += materialPrice;
                if (promotionForMaterial != null) {
                        materialPrice = materialPrice - (materialPrice * promotionForMaterial.getValue() / 100);
                }

                Map<Integer, Double> gemstoneValues = product.getListGemStoneOfProduct().stream()
                                .collect(Collectors.groupingBy(
                                                gemstone -> gemstone.getGemstoneType().getId(),
                                                Collectors.summingDouble(gemstone -> gemstone.getPrice()
                                                                * gemstone.getQuantity())));

                double gemstonePrice = gemstoneValues.values().stream().mapToDouble(Double::doubleValue).sum();
                priceBefore += gemstonePrice;
                double gemstoneDiscount = applyGemstonePromotions(gemstoneValues, promotionForGemstone);
                double finalGemstonePrice = gemstonePrice - gemstoneDiscount;

                double productBasePrice = materialPrice + finalGemstonePrice;

                if (promotionForProductType != null) {
                        productBasePrice = productBasePrice
                                        - (productBasePrice * promotionForProductType.getValue() / 100);
                }

                if (promotionForProduct != null) {
                        productBasePrice = productBasePrice - (productBasePrice * promotionForProduct.getValue() / 100);
                }

                double finalPrice = productBasePrice + product.getFee();
                finalPrice *= invoiceType.getRate();

                InvoiceDetailDTO invoiceDetailDTO = new InvoiceDetailDTO();
                invoiceDetailDTO.setProductDTO(product.getDTO());
                invoiceDetailDTO.setPrice(priceBefore + product.getFee());
                invoiceDetailDTO.setQuantity(quantity);
                invoiceDetailDTO.setTotalPrice(finalPrice);
                invoiceDetailDTO.setListPromotion(
                                promotions.stream().map(Promotion::getDTO).collect(Collectors.toList()));

                return invoiceDetailDTO;
        }

        private double applyGemstonePromotions(Map<Integer, Double> gemstoneValues,
                        List<Promotion> promotionForGemstone) {
                double totalDiscount = 0;

                for (Map.Entry<Integer, Double> entry : gemstoneValues.entrySet()) {
                        Integer gemstoneTypeId = entry.getKey();
                        Double totalValue = entry.getValue();

                        for (Promotion promotion : promotionForGemstone) {
                                if (promotion.getListForGemStoneTypes().stream()
                                                .anyMatch(fgt -> fgt.getGemstoneType().getId().equals(gemstoneTypeId)
                                                                && fgt.isStatus())) {
                                        double discount = totalValue * promotion.getValue() / 100;
                                        totalDiscount += discount;
                                }
                        }
                }

                return totalDiscount;
        }

        public Invoice createInvoiceFromDetails(HashMap<String, Integer> barcodeQuantityMap,
                        Integer invoiceTypeId, Integer userId) {
                UserInfo userInfo = userInfoService.findById(userId);
                InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
                Invoice invoice = new Invoice();
                invoice.setUserInfo(userInfo);
                invoice.setInvoiceType(invoiceType);
                invoice.setDate(new Date());

                List<InvoiceDetailDTO> invoiceDetails = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : barcodeQuantityMap.entrySet()) {
                        String barcode = entry.getKey();
                        Integer quantity = entry.getValue();
                        invoiceDetails.add(createInvoiceDetail(barcode, invoiceTypeId, quantity));
                }

                double totalPriceRaw = invoiceDetails.stream().mapToDouble(InvoiceDetailDTO::getPrice).sum();
                double totalPrice = invoiceDetails.stream().mapToDouble(InvoiceDetailDTO::getTotalPrice).sum();
                double discountPrice = totalPriceRaw - totalPrice;

                invoice.setTotalPriceRaw(totalPriceRaw);
                invoice.setTotalPrice(totalPrice);
                invoice.setDiscountPrice(discountPrice);

                saveInvoice(invoice, invoiceDetails);

                return invoice;
        }

        private void saveInvoice(Invoice invoice, List<InvoiceDetailDTO> invoiceDetails) {
                List<InvoiceDetail> invoiceDetailEntities = new ArrayList<>();

                for (InvoiceDetailDTO detailDTO : invoiceDetails) {
                        Product product = new Product(detailDTO.getProductDTO());

                        InvoiceDetail invoiceDetail = new InvoiceDetail();
                        invoiceDetail.setProduct(product);
                        invoiceDetail.setQuantity(detailDTO.getQuantity());
                        invoiceDetail.setPrice(detailDTO.getPrice());
                        invoiceDetail.setPriceMaterialAtTime(
                                        product.getMaterial().getPriceAtTime() * product.getWeight());
                        invoiceDetail.setTotalPrice(detailDTO.getTotalPrice());
                        invoiceDetail.setInvoice(invoice);

                        invoiceDetailEntities.add(invoiceDetail);
                }

                invoiceDetailRepository.saveAll(invoiceDetailEntities);
                invoiceRepository.save(invoice);
        }
}
