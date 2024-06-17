package com.ks1dotnet.jewelrystore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.Inventory;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.entity.InvoiceDetail;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.entity.VoucherOnInvoice;
import com.ks1dotnet.jewelrystore.entity.VoucherOnInvoiceDetail;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.repository.IInventoryRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.repository.IOrderInvoiceDetailRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.repository.IVoucherOnInvoiceDetailRepository;
import com.ks1dotnet.jewelrystore.repository.IVoucherOnInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEarnPointsService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IUserInfoService;

import jakarta.transaction.Transactional;

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
        private IEarnPointsService iEarnPointsService;

        @Autowired
        private IOrderInvoiceDetailRepository invoiceDetailRepository;

        @Autowired
        private IProductRepository iProductRepository;

        @Autowired
        private IVoucherOnInvoiceDetailRepository iVoucherOnInvoiceDetailRepository;
        @Autowired
        private IVoucherOnInvoiceRepository iVoucherOnInvoiceRepository;

        @Autowired
        private IInventoryRepository iInventoryRepository;

        @Value("${fileUpload.productPath}")
        private String filePath;

        @Value("${firebase.img-url}")
        private String url;

        @Autowired
        private IEmployeeRepository iEmployeeRepository;

        @Override
        public InvoiceDetailDTO createInvoiceDetail(String barcode, Integer invoiceTypeId, Integer quantity) {
                try {
                        Product product = iProductRepository.findByBarCode(barcode);

                        if (product == null) {
                                throw new BadRequestException("Product not found.");
                        }

                        if (product.getInventory().getQuantity() <= 0) {
                                throw new BadRequestException("Product is out of stock.");
                        } else if (product.getInventory().getQuantity() < quantity) {
                                throw new BadRequestException("Product is not enough to sell.");
                        }
                        if (!product.isStatus()) {
                                throw new BadRequestException("Product is not sold.");
                        }

                        InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
                        List<Promotion> promotions = promotionService.getAllPromotionByProductAndInvoiceType(product,
                                        invoiceType.getId());

                        return calculateInvoiceDetail(product, quantity, promotions, invoiceType);
                } catch (BadRequestException e) {
                        throw e; // Re-throwing the exception to be handled globally
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error creating invoice detail: ", e.getMessage());
                }
        }

        @Override
        public InvoiceDetailDTO calculateInvoiceDetail(Product product, int quantity, List<Promotion> promotions,
                        InvoiceType invoiceType) {
                try {
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
                        Double priceAtTime = product.getMaterial().getPriceAtTime();
                        float weight = product.getWeight();
                        double materialPrice = priceAtTime * weight;

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
                                productBasePrice = productBasePrice
                                                - (productBasePrice * promotionForProduct.getValue() / 100);
                        }

                        double finalPrice = productBasePrice + product.getFee();
                        finalPrice *= invoiceType.getRate();
                        InvoiceDetailDTO invoiceDetailDTO = new InvoiceDetailDTO();
                        ProductDTO productDTO = product.getDTO();

                        productDTO.setImgPath(url.trim() + filePath.trim() + productDTO.getImgPath());

                        invoiceDetailDTO.setProductDTO(productDTO);
                        invoiceDetailDTO.setPrice(
                                        ((priceBefore + product.getFee()) * invoiceType.getRate()) * quantity);
                        invoiceDetailDTO.setQuantity(quantity);
                        invoiceDetailDTO.setTotalPrice(finalPrice * quantity);
                        invoiceDetailDTO.setListPromotion(
                                        promotions.stream().map(Promotion::getDTO).collect(Collectors.toList()));

                        return invoiceDetailDTO;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating invoice detail: ", e.getMessage());
                }
        }

        @Override
        public double applyGemstonePromotions(Map<Integer, Double> gemstoneValues,
                        List<Promotion> promotionForGemstone) {
                try {
                        double totalDiscount = 0;

                        for (Map.Entry<Integer, Double> entry : gemstoneValues.entrySet()) {
                                Integer gemstoneTypeId = entry.getKey();
                                Double totalValue = entry.getValue();
                                for (Promotion promotion : promotionForGemstone) {
                                        if (promotion.getListForGemStoneTypes().stream()
                                                        .anyMatch(fgt -> fgt.getGemstoneType().getId()
                                                                        .equals(gemstoneTypeId) && fgt.isStatus())) {
                                                double discount = totalValue * promotion.getValue() / 100;
                                                totalDiscount += discount;
                                        }
                                }
                        }

                        return totalDiscount;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error applying gemstone promotions: ", e.getMessage());
                }
        }

        @Transactional
        @Override
        public int createInvoiceFromDetails(HashMap<String, Integer> barcodeQuantityMap, Integer invoiceTypeId,
                        Integer userId, String idEmp, String payment, String note) {
                try {
                        UserInfo userInfo = userInfoService.findById(userId);
                        InvoiceType invoiceType = invoiceTypeService.findById(invoiceTypeId);
                        Invoice invoice = new Invoice();
                        Employee employee = iEmployeeRepository.findById(idEmp)
                                        .orElseThrow(() -> new BadRequestException(
                                                        "NOT FOUND EMPLOYEE WITH THIS TOKEN:" + idEmp));
                        invoice.setUserInfo(userInfo);
                        invoice.setInvoiceType(invoiceType);
                        invoice.setDate(new Date());
                        invoice.setEmployee(employee);
                        invoice.setPayment(payment);
                        invoice.setNote(note);
                        List<InvoiceDetailDTO> invoiceDetails = new ArrayList<>();
                        for (Map.Entry<String, Integer> entry : barcodeQuantityMap.entrySet()) {
                                String barcode = entry.getKey();
                                Integer quantity = entry.getValue();
                                invoiceDetails.add(createInvoiceDetail(barcode, invoiceTypeId, quantity));
                        }
                        // Lưu Invoice trước để có ID
                        // tìm ra được voucher for user
                        // policy cho invoice type
                        //

                        PromotionDTO promotionDTO = promotionService.getPromotionsByUserId(userId);
                        invoiceRepository.save(invoice);
                        double totalPriceRaw = invoiceDetails.stream().mapToDouble(InvoiceDetailDTO::getPrice).sum();
                        // giảm trên hóa đơn
                        double totalPrice = invoiceDetails.stream().mapToDouble(InvoiceDetailDTO::getTotalPrice).sum();
                        // chỉ trường hợp invoice type là 1 mới có policy dành cho user
                        if (promotionDTO != null && invoice.getInvoiceType().getId() == 1) {
                                totalPrice = totalPrice - totalPrice * promotionDTO.getValue() / 100;
                                iVoucherOnInvoiceRepository
                                                .save(new VoucherOnInvoice(new Promotion(promotionDTO), invoice));
                        }

                        double discountPrice = totalPriceRaw - totalPrice;
                        invoice.setTotalPriceRaw(totalPriceRaw);
                        invoice.setTotalPrice(totalPrice);
                        invoice.setDiscountPrice(discountPrice);
                        // service add earn point bằng tổng tiêu / 100000
                        iEarnPointsService.addPoints(userId, convertDoubleToInt(totalPrice / 100000));

                        saveInvoice(invoice, invoiceDetails);
                        return invoice.getId();
                } catch (BadRequestException e) {
                        throw e; // Re-throwing the exception to be handled globally
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error creating invoice from details: ", e.getMessage());
                }
        }

        @Override
        public void saveInvoice(Invoice invoice, List<InvoiceDetailDTO> invoiceDetails) {
                try {
                        for (InvoiceDetailDTO detailDTO : invoiceDetails) {
                                Product product = iProductRepository
                                                .findByBarCode(detailDTO.getProductDTO().getBarCode());
                                InvoiceDetail invoiceDetail = new InvoiceDetail();
                                invoiceDetail.setProduct(product);
                                invoiceDetail.setQuantity(detailDTO.getQuantity());
                                invoiceDetail.setPrice(detailDTO.getPrice());
                                invoiceDetail.setPriceMaterialAtTime(
                                                product.getMaterial().getPriceAtTime());
                                invoiceDetail.setTotalPrice(detailDTO.getTotalPrice());
                                invoiceDetail.setInvoice(invoice);
                                invoiceDetail.setCounter(product.getCounter());

                                invoiceDetailRepository.save(invoiceDetail);

                                // save promotion
                                List<Promotion> promotions = promotionService.getAllPromotionByProductAndInvoiceType(
                                                product, invoice.getInvoiceType().getId());
                                List<VoucherOnInvoiceDetail> lVoucherOnInvoiceDetails = promotions.stream()
                                                .map(p -> new VoucherOnInvoiceDetail(p, invoiceDetail))
                                                .collect(Collectors.toList());
                                if (!lVoucherOnInvoiceDetails.isEmpty()) {
                                        iVoucherOnInvoiceDetailRepository.saveAll(lVoucherOnInvoiceDetails);
                                }

                                // change quantity
                                Inventory inventory = product.getInventory();
                                if (invoice.getInvoiceType().getId() == 1) {
                                        inventory.setQuantity(inventory.getQuantity() - detailDTO.getQuantity());
                                        inventory.setTotal_sold(inventory.getTotal_sold() + detailDTO.getQuantity());
                                } else if (invoice.getInvoiceType().getId() == 3) {
                                        inventory.setQuantity(inventory.getQuantity() + detailDTO.getQuantity());
                                        inventory.setTotal_sold(inventory.getTotal_sold() - detailDTO.getQuantity());
                                }
                        }

                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error saving invoice: ", e.getMessage());
                }
        }

        @Override
        public int convertDoubleToInt(double input) {
                try {
                        BigDecimal bigDecimal = new BigDecimal(input).setScale(0, RoundingMode.HALF_UP);
                        return bigDecimal.intValue();
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error converting double to int: ", e.getMessage());
                }
        }
}
