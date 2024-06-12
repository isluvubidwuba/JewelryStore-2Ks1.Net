package com.ks1dotnet.jewelrystore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDTO;
import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Counter;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;
import com.ks1dotnet.jewelrystore.entity.OrderInvoice;
import com.ks1dotnet.jewelrystore.entity.OrderInvoiceDetail;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.entity.VoucherOnInvoice;
import com.ks1dotnet.jewelrystore.entity.VoucherOnInvoiceDetail;
import com.ks1dotnet.jewelrystore.entity.WareHouse;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.repository.IExchangeRatePolicyRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IOrderInvoiceDetailRepository;
import com.ks1dotnet.jewelrystore.repository.IOrderInvoiceRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.repository.IUserInfoRepository;
import com.ks1dotnet.jewelrystore.repository.IVoucherOnInvoiceDetailRepository;
import com.ks1dotnet.jewelrystore.repository.IVoucherOnInvoiceRepository;
import com.ks1dotnet.jewelrystore.repository.IWareHouseRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEarnPointsService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IOrderService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OrderService implements IOrderService {
    @Autowired
    private IOrderInvoiceRepository iOrderInvoiceRepository;
    @Autowired
    private IEarnPointsService iEarnPointsService;
    @Autowired
    private IEmployeeRepository iEmployeeRepository;
    @Autowired
    private IUserInfoRepository iUserInfoRepository;
    @Autowired
    private IProductRepository iProductRepository;
    @Autowired
    private IExchangeRatePolicyRepository iExchangeRatePolicyRepository;
    @Autowired
    private IInvoiceTypeRepository iInvoiceTypeRepository;
    @Autowired
    private IPromotionService iPromotionService;
    @Autowired
    private IVoucherOnInvoiceRepository iVoucherOnInvoiceRepository;
    @Autowired
    private IOrderInvoiceDetailRepository iOrderInvoiceDetailRepository;
    @Autowired
    private IVoucherOnInvoiceDetailRepository iVoucherOnInvoiceDetailRepository;
    @Autowired
    JwtUtilsHelper jwtUtilsHelper;
    @Autowired
    private IWareHouseRepository iWareHouseRepository;

    @Override
    public ResponseData getOrderInvoiceDetail(String barcode, String IdExchangeRate) {// tạo ra response --> chi tiết
                                                                                      // đơn hàng
        try {
            // check sql
            Object[] result = (Object[]) iOrderInvoiceRepository.getProductActualPriceByBarcode(barcode);
            if (result == null || result.length == 0) {
                return new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null);
            }
            // id - name - priceraw - quantity max
            if ((Integer) result[3] == 0) {
                return new ResponseData(HttpStatus.NOT_FOUND, "Out of stock", null);
            }

            OrderInvoiceDetailDTO orderInvoiceDetailDTO = createOrderInvoiceDetailDTO(result, IdExchangeRate);
            return new ResponseData(HttpStatus.OK, "Success", orderInvoiceDetailDTO);
        } catch (Exception e) {
            return new ResponseData(HttpStatus.BAD_REQUEST, "Error get order detail: " + e.getMessage(), null);
        }
    }

    private OrderInvoiceDetailDTO createOrderInvoiceDetailDTO(Object[] result, String IdExchangeRate)
            throws BadRequestException {
        Integer productId = (Integer) result[0];
        double priceRaw = (Double) result[2];
        // giá đã qua tính toán = [giá vàng thời điểm * trọng lượng sản phẩm] + tiền
        // công + tiền đá
        Integer quantityMax = (Integer) result[3];
        // check exist product
        Product product = iProductRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        List<PromotionDTO> listPromotionDTOs = iPromotionService.getAllPromotionByIdProduct(product.getId());
        // check get exchange rate
        ExchangeRatePolicy exchangeRatePolicy = iExchangeRatePolicyRepository.findById(IdExchangeRate)
                .orElseThrow(() -> new BadRequestException("Not found exchange rate"));
        // Giá bán = giá vốn sản phẩm * tỉ lệ áp giá
        priceRaw = priceRaw * exchangeRatePolicy.getRate();
        double totalPrice = priceRaw;
        // giá tổng = giá bán - promotion giảm
        for (PromotionDTO promotionDTO : listPromotionDTOs) {
            totalPrice -= totalPrice * promotionDTO.getValue() / 100;
        }
        // Làm tròn các giá trị
        BigDecimal priceRawRounded = BigDecimal.valueOf(priceRaw).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPriceRounded = BigDecimal.valueOf(totalPrice).setScale(2, RoundingMode.HALF_UP);

        OrderInvoiceDetailDTO orderInvoiceDetailDTO = new OrderInvoiceDetailDTO();
        orderInvoiceDetailDTO.setProductDTO(product.getDTO());
        orderInvoiceDetailDTO.setPrice(priceRawRounded.doubleValue());
        orderInvoiceDetailDTO.setQuantity(quantityMax);
        orderInvoiceDetailDTO.setTotalPrice(totalPriceRounded.doubleValue());
        orderInvoiceDetailDTO.setListPromotion(listPromotionDTOs);

        return orderInvoiceDetailDTO;
    }

    @Override
    public ResponseData insertOrder(OrderInvoiceDTO orderInvoiceRequest) {
        try {
            OrderInvoice orderInvoice = createOrderInvoice(orderInvoiceRequest);
            iOrderInvoiceRepository.save(orderInvoice);

            processPromotions(orderInvoiceRequest, orderInvoice);
            List<OrderInvoiceDetail> listOrderDetailSave = processOrderDetails(orderInvoiceRequest, orderInvoice);

            iOrderInvoiceDetailRepository.saveAll(listOrderDetailSave);
            // Chuyển đổi List sang Set
            orderInvoice.setListOrderInvoiceDetail(listOrderDetailSave);
            processVoucherOnInvoiceDetails(listOrderDetailSave);
            // add point for customer
            int points = calculateEarnedPoints(orderInvoice);
            iEarnPointsService.addPoints(orderInvoice.getUserInfo().getId(), points);

            return new ResponseData(HttpStatus.CREATED, "Order inserted successfully", orderInvoice.gResponseDTO());
        } catch (Exception e) {
            return new ResponseData(HttpStatus.BAD_REQUEST, "Error inserting order: " + e.getMessage(), null);
        }
    }

    private OrderInvoice createOrderInvoice(OrderInvoiceDTO orderInvoiceRequest) throws ResourceNotFoundException {
        UserInfo userInfo = iUserInfoRepository.findById(orderInvoiceRequest.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " +
                        orderInvoiceRequest.getIdUser()));

        String token = orderInvoiceRequest.getIdEmployee();
        String IDEmpl = jwtUtilsHelper.getEmployeeIdFromToken(token);
        Employee employee = iEmployeeRepository.findById(IDEmpl)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + IDEmpl));

        OrderInvoice orderInvoice = new OrderInvoice();
        orderInvoice.setEmployee(employee);
        orderInvoice.setUserInfo(userInfo);
        orderInvoice.setDate(new Date());
        orderInvoice.setTotal_price_raw(orderInvoiceRequest.getTotalPriceRaw());
        orderInvoice.setTotal_price(orderInvoiceRequest.getTotalPrice());
        orderInvoice.setDiscount_price(orderInvoiceRequest.getDiscountPrice());
        orderInvoice.setInvoiceType(iInvoiceTypeRepository.findById(orderInvoiceRequest.getIdInvoiceType())
                .orElseThrow(() -> new ResourceNotFoundException("InvoiceType not found: " +
                        orderInvoiceRequest.getIdInvoiceType())));

        List<VoucherOnInvoice> listVoucherOnInvoices = new ArrayList<>();
        List<PromotionDTO> listDto = iPromotionService.getPromotionsByUserId(userInfo.getId());
        for (PromotionDTO promotionDTO : listDto) {
            listVoucherOnInvoices.add(new VoucherOnInvoice(new Promotion(promotionDTO), orderInvoice));
        }
        orderInvoice.setListVoucherOnInvoice(listVoucherOnInvoices);
        return orderInvoice;
    }

    private void processPromotions(OrderInvoiceDTO orderInvoiceRequest, OrderInvoice orderInvoice) {
        List<PromotionDTO> promotions = iPromotionService.getPromotionsByUserId(orderInvoiceRequest.getIdUser());
        if (promotions.size() == 1) {
            VoucherOnInvoice voucherOnInvoice = new VoucherOnInvoice();
            voucherOnInvoice.setOrderInvoice(orderInvoice);
            voucherOnInvoice.setPromotion(new Promotion(promotions.get(0)));
            iVoucherOnInvoiceRepository.save(voucherOnInvoice);
        }
    }

    private List<OrderInvoiceDetail> processOrderDetails(OrderInvoiceDTO orderInvoiceRequest, OrderInvoice orderInvoice)
            throws BadRequestException {
        List<OrderInvoiceDetail> listOrderDetailSave = new ArrayList<>();
        List<OrderInvoiceDetailDTO> orderInvoiceDetailDTOs = orderInvoiceRequest.getListOrderInvoiceDetail();

        for (OrderInvoiceDetailDTO orderInvoiceDetailDTO : orderInvoiceDetailDTOs) {
            int productId = orderInvoiceDetailDTO.getProductDTO().getId();
            int quantityToReduce = orderInvoiceDetailDTO.getQuantity();
            WareHouse wareHouse = iWareHouseRepository.findByProductId(productId);
            if (wareHouse.getQuantity() < quantityToReduce) {
                throw new BadRequestException("Not enough quantity in stock");
            }

            OrderInvoiceDetail orderInvoiceDetail = new OrderInvoiceDetail();
            orderInvoiceDetail.setOrderInvoice(orderInvoice);
            orderInvoiceDetail.setProduct(new Product(orderInvoiceDetailDTO.getProductDTO()));
            orderInvoiceDetail.setPrice(orderInvoiceDetailDTO.getPrice());
            orderInvoiceDetail
                    .setPriceMaterialAtTime(orderInvoiceDetailDTO.getProductDTO().getMaterialDTO().getPriceAtTime());
            orderInvoiceDetail.setQuantity(orderInvoiceDetailDTO.getQuantity());
            orderInvoiceDetail.setTotal_price(orderInvoiceDetailDTO.getTotalPrice());
            orderInvoiceDetail.setCounter(new Counter(orderInvoiceDetailDTO.getProductDTO().getCounterDTO()));
            List<PromotionDTO> PromotionDTOs = iPromotionService.getAllPromotionByIdProduct(productId);
            List<VoucherOnInvoiceDetail> voucherOnInvoiceDetails = new ArrayList<>();
            for (PromotionDTO p : PromotionDTOs) {
                voucherOnInvoiceDetails.add(new VoucherOnInvoiceDetail(new Promotion(p), orderInvoiceDetail));
            }
            orderInvoiceDetail.setListVoucherOnInvoiceDetail(voucherOnInvoiceDetails);

            listOrderDetailSave.add(orderInvoiceDetail);

            int newQuantity = wareHouse.getQuantity() - quantityToReduce;
            int newTotalSold = wareHouse.getTotal_sold() + quantityToReduce;
            iWareHouseRepository.updateWareHouse(wareHouse.getId(), newQuantity, newTotalSold);
        }
        return listOrderDetailSave;
    }

    private void processVoucherOnInvoiceDetails(List<OrderInvoiceDetail> listOrderDetailSave) {
        for (OrderInvoiceDetail orderInvoiceDetail : listOrderDetailSave) {
            List<PromotionDTO> promotionDTOs = iPromotionService
                    .getAllPromotionByIdProduct(orderInvoiceDetail.getProduct().getId());
            for (PromotionDTO promotionDTO : promotionDTOs) {
                iVoucherOnInvoiceDetailRepository
                        .save(new VoucherOnInvoiceDetail(new Promotion(promotionDTO), orderInvoiceDetail));
            }
        }
    }

    private int calculateEarnedPoints(OrderInvoice orderInvoice) {
        BigDecimal totalPriceRounded = BigDecimal.valueOf(orderInvoice.getTotal_price()).setScale(0,
                RoundingMode.HALF_UP);
        return totalPriceRounded.multiply(BigDecimal.valueOf(0.01)).setScale(0, RoundingMode.HALF_UP).intValue();
    }
}
