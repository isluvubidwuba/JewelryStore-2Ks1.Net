package com.ks1dotnet.jewelrystore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.CounterDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeRevenueDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.dto.RevenueDTO;
import com.ks1dotnet.jewelrystore.entity.Counter;
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
import com.ks1dotnet.jewelrystore.payload.request.InvoiceRequest;
import com.ks1dotnet.jewelrystore.repository.ICounterRepository;
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
        private ICounterRepository iCounterRepository;

        @Autowired
        private IProductRepository iProductRepository;
        @Autowired
        private IInventoryRepository iInventoryRepository;
        @Autowired
        private IVoucherOnInvoiceDetailRepository iVoucherOnInvoiceDetailRepository;
        @Autowired
        private IVoucherOnInvoiceRepository iVoucherOnInvoiceRepository;
        @Value("${fileUpload.userPath}")
        private String filePath;

        @Value("${fileUpload.promotionPath}")
        private String promotionPath;

        @Value("${fileUpload.productPath}")
        private String productPath;

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

                        if (product.getInventory().getQuantity() <= 0 && invoiceTypeId == 1) {
                                throw new BadRequestException("Product is out of stock.");
                        } else if (product.getInventory().getQuantity() < quantity && invoiceTypeId == 1) {
                                throw new BadRequestException("Product is not enough to sell.");
                        }
                        if (!product.isStatus() && invoiceTypeId == 1) {
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
                        productDTO.setImgPath(url.trim() + productPath.trim() + productDTO.getImgPath());

                        invoiceDetailDTO.setProductDTO(productDTO);
                        invoiceDetailDTO.setPrice(
                                        ((priceBefore + product.getFee()) * invoiceType.getRate()) * quantity);
                        invoiceDetailDTO.setQuantity(quantity);
                        invoiceDetailDTO.setTotalPrice(finalPrice * quantity);

                        invoiceDetailDTO.setListPromotion(
                                        promotions.stream()
                                                        .map(promotion -> {
                                                                PromotionDTO dto = promotion.getDTO();
                                                                String imageUrl = url.trim() + promotionPath.trim()
                                                                                + promotion.getImage();
                                                                dto.setImage(imageUrl);
                                                                return dto;
                                                        })
                                                        .collect(Collectors.toList()));

                        if (invoiceType.getId() == 1) {
                                invoiceDetailDTO.setAvailableReturnQuantity(quantity);
                        } else if (invoiceType.getId() == 3) {
                                invoiceDetailDTO.setAvailableReturnQuantity(0);
                        }
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
                                                        "NOT FOUND EMPLOYEE WITH THIS ID:" + idEmp));
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
                        if (invoiceTypeId == 1) {
                                iEarnPointsService.addPoints(userId, convertDoubleToInt(totalPrice / 100000));
                        }
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
                                invoiceDetail.setAvailableReturnQuantity(detailDTO.getAvailableReturnQuantity());
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

        // private Map<String, Integer> convertBarcodeQuantityMap(Map<String, String>
        // barcodeQuantityMap) {
        // Map<String, Integer> convertedMap = new HashMap<>();
        // for (Map.Entry<String, String> entry : barcodeQuantityMap.entrySet()) {
        // convertedMap.put(entry.getKey(), Integer.parseInt(entry.getValue()));
        // }
        // return convertedMap;
        // }

        @Override
        public int convertDoubleToInt(double input) {
                try {
                        BigDecimal bigDecimal = new BigDecimal(input).setScale(0, RoundingMode.HALF_UP);
                        return bigDecimal.intValue();
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error converting double to int: ", e.getMessage());
                }
        }

        @Override
        public int createImportInvoice(InvoiceRequest request, Map<String, Double> barcodePriceMap) {
                try {
                        Counter counter = iCounterRepository.findById(1).get();
                        Invoice invoice = new Invoice();
                        UserInfo user = userInfoService.findById(request.getUserId());
                        invoice.setUserInfo(user);
                        if (request.getInvoiceTypeId() != 2) {
                                throw new BadRequestException("ERROR with method create import error invoice type");
                        }
                        if (user.getRole().getId() != 5) {
                                throw new BadRequestException(
                                                "ERROR with method create import error user not is SUPPLIER type");
                        }
                        invoice.setInvoiceType(invoiceTypeService.findById(2)); // Đặt loại hóa đơn là 2 cho import
                        invoice.setDate(new Date());
                        invoice.setEmployee(iEmployeeRepository.findById(request.getEmployeeId())
                                        .orElseThrow(() -> new BadRequestException("NOT FOUND EMPLOYEE WITH THIS TOKEN:"
                                                        + request.getEmployeeId())));
                        invoice.setPayment(request.getPayment());
                        invoice.setNote(request.getNote());
                        invoiceRepository.save(invoice);

                        double totalPrice = 0;

                        for (Map.Entry<String, String> entry : request.getBarcodeQuantityMap().entrySet()) {
                                String barcode = entry.getKey();
                                int quantity = Integer.parseInt(entry.getValue());
                                double price = barcodePriceMap.get(barcode);
                                System.out.println("quanttiy: " + quantity);
                                Product product = iProductRepository.findByBarCode(barcode);
                                if (product == null) {
                                        throw new BadRequestException("Product not found for barcode: " + barcode);
                                }

                                Inventory inventory = product.getInventory();
                                inventory.setQuantity(inventory.getQuantity() + quantity);
                                System.out.println("quanttiy: " + inventory.getQuantity() + quantity);

                                inventory.setTotal_import(inventory.getTotal_import() + quantity);
                                iInventoryRepository.save(inventory);

                                InvoiceDetail invoiceDetail = new InvoiceDetail();
                                invoiceDetail.setInvoice(invoice);
                                invoiceDetail.setProduct(product);
                                invoiceDetail.setQuantity(quantity);
                                invoiceDetail.setPrice(price);
                                invoiceDetail.setCounter(counter);
                                invoiceDetail.setPriceMaterialAtTime(product.getMaterial().getPriceAtTime());
                                invoiceDetail.setTotalPrice(price * quantity);
                                invoiceDetailRepository.save(invoiceDetail);

                                totalPrice += price * quantity;
                        }

                        invoice.setTotalPrice(totalPrice);
                        invoice.setTotalPriceRaw(totalPrice);
                        invoiceRepository.save(invoice);

                        return invoice.getId();
                } catch (Exception e) {
                        throw new RuntimeException("Error creating import invoice: " + e.getMessage(), e);
                }
        }

        @Override
        @Transactional
        public void cancelInvoice(int invoiceId, String note) {
                try {
                        Invoice invoice = invoiceRepository.findById(invoiceId)
                                        .orElseThrow(() -> new BadRequestException(
                                                        "Invoice not found with id: " + invoiceId));

                        if (!invoice.isStatus()) {
                                throw new BadRequestException("Invoice is already canceled.");
                        }

                        List<InvoiceDetail> invoiceDetails = invoice.getListOrderInvoiceDetail();
                        for (InvoiceDetail detail : invoiceDetails) {
                                Product product = detail.getProduct();
                                Inventory inventory = product.getInventory();

                                if (invoice.getInvoiceType().getId() == 1) {
                                        inventory.setQuantity(inventory.getQuantity() + detail.getQuantity());
                                        inventory.setTotal_sold(inventory.getTotal_sold() - detail.getQuantity());
                                } else if (invoice.getInvoiceType().getId() == 3) {
                                        inventory.setQuantity(inventory.getQuantity() - detail.getQuantity());
                                        inventory.setTotal_sold(inventory.getTotal_sold() + detail.getQuantity());
                                }

                                iInventoryRepository.save(inventory);
                        }

                        invoice.setStatus(false);
                        invoice.setNote(note);
                        invoiceRepository.save(invoice);
                } catch (BadRequestException e) {
                        throw e;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error canceling invoice: ", e.getMessage());
                }
        }

        @Override
        public List<InvoiceDTO> getAllInvoices() {
                try {
                        List<Invoice> invoices = invoiceRepository.findAll();
                        return invoices.stream()
                                        .map(Invoice::getDTO)
                                        .collect(Collectors.toList());
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error retrieving all invoices: ", e.getMessage());
                }
        }

        @Override
        public List<InvoiceDTO> getInvoicesByEmployeeId(String employeeId) {
                try {
                        List<Invoice> invoices = invoiceRepository.findByEmployeeId(employeeId);
                        return invoices.stream()
                                        .map(Invoice::getDTO)
                                        .collect(Collectors.toList());
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error retrieving invoices by employee ID: ", e.getMessage());
                }
        }

        @Override
        public List<InvoiceDTO> getInvoicesByDateRange(Date startDate, Date endDate) {
                try {
                        List<Invoice> invoices = invoiceRepository.findByDateBetween(startDate, endDate);
                        return invoices.stream()
                                        .map(Invoice::getDTO)
                                        .collect(Collectors.toList());
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error retrieving invoices by date range: ", e.getMessage());
                }
        }

        @Override
        public List<InvoiceDTO> getInvoicesByUserId(int userId) {
                try {
                        List<Invoice> invoices = invoiceRepository.findByUserInfoId(userId);
                        return invoices.stream()
                                        .map(Invoice::getDTO)
                                        .collect(Collectors.toList());
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error retrieving invoices by user ID: ", e.getMessage());
                }
        }

        @Override
        public List<RevenueDTO<CounterDTO>> calculateRevenueByCounter(String period, int year, Integer quarterOrMonth) {
                try {
                        List<Invoice> invoices;
                        if (period.equalsIgnoreCase("month")) {
                                invoices = invoiceRepository.findByMonthAndYear(year, quarterOrMonth);
                        } else if (period.equalsIgnoreCase("quarter")) {
                                invoices = invoiceRepository.findByQuarterAndYear(year, quarterOrMonth);
                        } else if (period.equalsIgnoreCase("year")) {
                                invoices = invoiceRepository.findByYear(year);
                        } else {
                                throw new BadRequestException(
                                                "Invalid period specified. Use 'month', 'quarter', or 'year'.");
                        }

                        // Tính doanh thu cho từng quầy
                        Map<Integer, Double> revenueByCounter = new HashMap<>();
                        for (Invoice invoice : invoices) {
                                if (invoice.isStatus() && invoice.getInvoiceType().getId() == 1) {
                                        for (InvoiceDetail detail : invoice.getListOrderInvoiceDetail()) {
                                                Counter counter = detail.getCounter();
                                                if (counter.getId() != 1) { // Bỏ qua quầy có id = 0
                                                        revenueByCounter.put(counter.getId(),
                                                                        revenueByCounter.getOrDefault(counter.getId(),
                                                                                        0.0)
                                                                                        + detail.getTotalPrice()
                                                                                                        * detail.getQuantity());
                                                }
                                        }
                                }
                        }

                        // Lấy tất cả các quầy từ repository
                        List<Counter> counters = iCounterRepository.findAll();
                        List<RevenueDTO<CounterDTO>> revenueDTOs = new ArrayList<>();

                        for (Counter counter : counters) {
                                if (counter.getId() != 1) { // Bỏ qua quầy có id = 0
                                        double revenue = revenueByCounter.getOrDefault(counter.getId(), 0.0);
                                        revenueDTOs.add(new RevenueDTO<>(counter.getDTO(), revenue));
                                }
                        }

                        return revenueDTOs;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating revenue by counter: ", e.getMessage());
                }
        }

        @Override
        public EmployeeRevenueDTO calculateRevenueByEmployeeID(String period, int year, Integer month,
                        String employeeId) {
                try {
                        List<Invoice> invoices;
                        if (period.equalsIgnoreCase("month")) {
                                invoices = invoiceRepository.findByEmployeeIdAndMonthAndYear(employeeId, year, month);
                        } else if (period.equalsIgnoreCase("year")) {
                                invoices = invoiceRepository.findByEmployeeIdAndYear(employeeId, year);
                        } else {
                                throw new BadRequestException("Invalid period specified. Use 'month' or 'year'.");
                        }

                        Employee employee = iEmployeeRepository.findById(employeeId)
                                        .orElseThrow(() -> new BadRequestException("Not found with id employee"));

                        double totalRevenue = invoices.stream()
                                        .filter(invoice -> invoice.isStatus() && invoice.getInvoiceType().getId() == 1)
                                        .mapToDouble(Invoice::getTotalPriceRaw)
                                        .sum();

                        double totalRevenueAfterDiscount = invoices.stream()
                                        .filter(invoice -> invoice.isStatus() && invoice.getInvoiceType().getId() == 1)
                                        .mapToDouble(Invoice::getTotalPrice)
                                        .sum();

                        int totalInvoices = (int) invoices.stream()
                                        .filter(invoice -> invoice.isStatus() && invoice.getInvoiceType().getId() == 1)
                                        .count();

                        double averageRevenue = totalInvoices > 0 ? totalRevenueAfterDiscount / totalInvoices : 0;
                        employee.setImage(url.trim() + filePath.trim() + employee.getImage());
                        EmployeeRevenueDTO revenueDTO = new EmployeeRevenueDTO();
                        revenueDTO.setEmployee(employee.getDTO());
                        revenueDTO.setTotalRevenue(totalRevenue);
                        revenueDTO.setTotalRevenueAfterDiscount(totalRevenueAfterDiscount);
                        revenueDTO.setTotalInvoices(totalInvoices);
                        revenueDTO.setAverageRevenue(averageRevenue);

                        return revenueDTO;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating revenue by employee: ", e.getMessage());
                }
        }

        @Override
        public List<RevenueDTO<EmployeeDTO>> calculateRevenueByEmployee(String period, int year, Integer month) {
                try {
                        List<Invoice> invoices;
                        if (period.equalsIgnoreCase("month")) {
                                invoices = invoiceRepository.findByMonthAndYear(year, month);
                        } else if (period.equalsIgnoreCase("year")) {
                                invoices = invoiceRepository.findByYear(year);
                        } else {
                                throw new BadRequestException("Invalid period specified. Use 'month' or 'year'.");
                        }

                        Map<EmployeeDTO, Double> revenueByEmployee = new HashMap<>();

                        for (Invoice invoice : invoices) {
                                if (invoice.isStatus() && invoice.getInvoiceType().getId() == 1) {
                                        Employee employee = invoice.getEmployee();
                                        double totalPrice = invoice.getTotalPrice();
                                        revenueByEmployee.put(employee.getDTO(),
                                                        revenueByEmployee.getOrDefault(employee.getDTO(), 0.0)
                                                                        + totalPrice);
                                }
                        }

                        List<RevenueDTO<EmployeeDTO>> revenueDTOList = revenueByEmployee.entrySet().stream()
                                        .map(entry -> new RevenueDTO<>(entry.getKey(), entry.getValue()))
                                        .collect(Collectors.toList());

                        return revenueDTOList;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating revenue by employee: ", e.getMessage());
                }
        }

        @Override
        public Page<InvoiceDTO> getInvoices(int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                Page<Invoice> invoices = invoiceRepository.findAll(pageable);
                return invoices.map(Invoice::getDTO);
        }

        @Override
        public Page<InvoiceDTO> getInvoicesByEmployeeId(String employeeId, int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
                Page<Invoice> invoices = invoiceRepository
                                .findByEmployeeIdAndInvoiceTypeIdAndStatusOrderByTotalPriceDesc(employeeId, 1, true,
                                                pageable);
                return invoices.map(Invoice::getDTO);
        }

        @Override
        public List<RevenueDTO<EmployeeDTO>> getTop5EmployeesByRevenue(String period, int year, Integer month) {
                try {
                        List<Invoice> invoices;
                        if (period.equalsIgnoreCase("month")) {
                                invoices = invoiceRepository.findByMonthAndYear(year, month);
                        } else if (period.equalsIgnoreCase("year")) {
                                invoices = invoiceRepository.findByYear(year);
                        } else {
                                throw new BadRequestException("Invalid period specified. Use 'month' or 'year'.");
                        }

                        Map<EmployeeDTO, Double> revenueByEmployee = new HashMap<>();
                        for (Invoice invoice : invoices) {
                                if (invoice.isStatus() && invoice.getInvoiceType().getId() == 1) {
                                        Employee employee = invoice.getEmployee();
                                        double totalPrice = invoice.getTotalPrice();
                                        EmployeeDTO employeeDTO = employee.getDTO();
                                        employeeDTO.setImage(url.trim() + filePath.trim() + employeeDTO.getImage());
                                        revenueByEmployee.put(employeeDTO,
                                                        revenueByEmployee.getOrDefault(employeeDTO, 0.0) + totalPrice);
                                }
                        }

                        List<RevenueDTO<EmployeeDTO>> top5Employees = revenueByEmployee.entrySet().stream()
                                        .sorted(Map.Entry.<EmployeeDTO, Double>comparingByValue().reversed())
                                        .limit(5)
                                        .map(entry -> new RevenueDTO<>(entry.getKey(), entry.getValue()))
                                        .collect(Collectors.toList());

                        return top5Employees;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating top 5 employees by revenue: ", e.getMessage());
                }
        }

        @Override
        public List<RevenueDTO<ProductDTO>> getTop5ProductsByRevenue(String period, int year, Integer month) {
                try {
                        List<Invoice> invoices;
                        if (period.equalsIgnoreCase("month")) {
                                invoices = invoiceRepository.findByMonthAndYear(year, month);
                        } else if (period.equalsIgnoreCase("year")) {
                                invoices = invoiceRepository.findByYear(year);
                        } else {
                                throw new BadRequestException("Invalid period specified. Use 'month' or 'year'.");
                        }

                        Map<ProductDTO, Double> revenueByProduct = new HashMap<>();

                        for (Invoice invoice : invoices) {
                                if (invoice.isStatus() && invoice.getInvoiceType().getId() == 1) {
                                        for (InvoiceDetail detail : invoice.getListOrderInvoiceDetail()) {
                                                Product product = detail.getProduct();
                                                ProductDTO productDTO = product.getDTO();
                                                double totalPrice = detail.getTotalPrice();
                                                revenueByProduct.put(productDTO,
                                                                revenueByProduct.getOrDefault(productDTO, 0.0)
                                                                                + totalPrice);
                                        }
                                }
                        }

                        List<RevenueDTO<ProductDTO>> top5Products = revenueByProduct.entrySet().stream()
                                        .sorted(Map.Entry.<ProductDTO, Double>comparingByValue().reversed())
                                        .limit(5)
                                        .map(entry -> new RevenueDTO<>(entry.getKey(), entry.getValue()))
                                        .collect(Collectors.toList());

                        return top5Products;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating top 5 products by revenue: ", e.getMessage());
                }
        }

        @Override
        @Transactional
        public int createBuybackInvoice(HashMap<Integer, Integer> barcodeQuantity, Integer invoiceTypeId,
                        Integer userId, String employeeId, String payment, String note) {
                validateBuybackDetails(barcodeQuantity);
                HashMap<String, Integer> barcodeQuantityMap = new HashMap<>();
                for (Map.Entry<Integer, Integer> entry : barcodeQuantity.entrySet()) {
                        InvoiceDetail detail = invoiceDetailRepository.findById(entry.getKey())
                                        .orElseThrow(() -> new BadRequestException(
                                                        "Invoice detail not found with ID: " + entry.getKey()));
                        String barcode = detail.getProduct().getBarCode();
                        barcodeQuantityMap.put(barcode, barcodeQuantityMap.getOrDefault(barcode, 0) + entry.getValue());
                }
                // Tạo hóa đơn từ chi tiết hóa đơn
                int invoiceId = createInvoiceFromDetails(barcodeQuantityMap, invoiceTypeId,
                                userId, employeeId, payment, note);

                // Cập nhật lại số lượng có thể bán lại của chi tiết hóa đơn gốc
                for (Map.Entry<Integer, Integer> entry : barcodeQuantity.entrySet()) {
                        InvoiceDetail detail = invoiceDetailRepository.findById(entry.getKey())
                                        .orElseThrow(() -> new BadRequestException(
                                                        "Invoice detail not found with ID: " + entry.getKey()));
                        detail.setAvailableReturnQuantity(detail.getAvailableReturnQuantity() - entry.getValue());
                        invoiceDetailRepository.save(detail);
                }

                return invoiceId;

        }

        @Override
        public void validateBuybackDetails(Map<Integer, Integer> barcodeQuantityMap) {
                // Duyệt qua tất cả các mục trong idDetailQuantityMap
                for (Map.Entry<Integer, Integer> entry : barcodeQuantityMap.entrySet()) {
                        // Tìm chi tiết hóa đơn theo ID
                        InvoiceDetail detail = invoiceDetailRepository.findById(entry.getKey())
                                        .orElseThrow(() -> new BadRequestException(
                                                        "Invoice detail not found with ID: " + entry.getKey()));

                        // Kiểm tra nếu số lượng yêu cầu vượt quá số lượng có thể bán lại trong chi tiết
                        // hóa đơn
                        if (detail.getAvailableReturnQuantity() < entry.getValue()) {
                                throw new BadRequestException(
                                                "Not enough quantity available for buyback for detail ID: "
                                                                + entry.getKey());
                        }
                }

        }

        @Override
        public double calculateStoreRevenue(String period, int year, Integer month) {
                try {
                        List<Invoice> invoices;
                        if (period.equalsIgnoreCase("month")) {
                                invoices = invoiceRepository.findByMonthAndYear(year, month);
                        } else if (period.equalsIgnoreCase("year")) {
                                invoices = invoiceRepository.findByYear(year);
                        } else {
                                throw new BadRequestException("Invalid period specified. Use 'month' or 'year'.");
                        }

                        double totalRevenue = 0.0;

                        for (Invoice invoice : invoices) {
                                if (invoice.isStatus()) {
                                        if (invoice.getInvoiceType().getId() == 1) { // Sell
                                                totalRevenue += invoice.getTotalPrice();
                                        } else if (invoice.getInvoiceType().getId() == 3) { // Buy Back
                                                totalRevenue -= invoice.getTotalPrice();
                                        }
                                }
                        }

                        return totalRevenue;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating store revenue: ", e.getMessage());
                }
        }

        @Override
        public Map<String, Object> calculateRevenueAndInvoiceCount(String period) {
                try {
                        List<Invoice> invoices = new ArrayList<>();
                        Date now = new Date();
                        Date startDate = null;

                        switch (period.toUpperCase()) {
                                case "TODAY":
                                        startDate = getStartOfDay(now);
                                        break;
                                case "YESTERDAY":
                                        startDate = getStartOfDay(addDays(now, -1));
                                        now = getEndOfDay(addDays(now, -1));
                                        break;
                                case "LAST_WEEK":
                                        startDate = getStartOfDay(addDays(now, -7));
                                        break;
                                case "LAST_MONTH":
                                        startDate = getStartOfDay(addMonths(now, -1));
                                        break;
                                case "LAST_90_DAYS":
                                        startDate = getStartOfDay(addDays(now, -90));
                                        break;
                                default:
                                        throw new BadRequestException("Invalid period specified.");
                        }

                        invoices = invoiceRepository.findByDateBetween(startDate, now);

                        double totalRevenue = invoices.stream()
                                        .filter(invoice -> invoice.getInvoiceType().getId() == 1 && invoice.isStatus())
                                        .mapToDouble(Invoice::getTotalPrice)
                                        .sum();

                        long invoiceCount = invoices.stream()
                                        .filter(invoice -> invoice.getInvoiceType().getId() == 1 && invoice.isStatus())
                                        .count();

                        Map<String, Object> result = new HashMap<>();
                        result.put("totalRevenue", totalRevenue);
                        result.put("invoiceCount", invoiceCount);

                        return result;
                } catch (Exception e) {
                        throw new RunTimeExceptionV1("Error calculating revenue and invoice count: ", e.getMessage());
                }
        }

        private Date getStartOfDay(Date date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
        }

        private Date getEndOfDay(Date date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                return cal.getTime();
        }

        private Date addDays(Date date, int days) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, days);
                return cal.getTime();
        }

        private Date addMonths(Date date, int months) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MONTH, months);
                return cal.getTime();
        }

        @Override
        public Page<InvoiceDTO> getInvoicesByEmployeeId2(String employeeId, int page, int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")); // Giới hạn 5 kết
                                                                                                    // quả
                Page<Invoice> invoices = invoiceRepository
                                .findByEmployeeIdAndInvoiceTypeIdAndStatus(employeeId, 1, true, pageable);
                return invoices.map(Invoice::getDTO);
        }

}
