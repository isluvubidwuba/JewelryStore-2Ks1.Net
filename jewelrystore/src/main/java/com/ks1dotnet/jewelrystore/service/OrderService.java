package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.repository.IExchangeRatePolicyRepository;
import com.ks1dotnet.jewelrystore.repository.IGemStoneOfProductRepository;
import com.ks1dotnet.jewelrystore.repository.IOrderInvoiceRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.repository.IUserInfoRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IOrderService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionService;

@Service
public class OrderService implements IOrderService {
    @Autowired
    IOrderInvoiceRepository iOrderInvoiceRepository;

    @Autowired
    IEmployeeRepository iEmployeeRepository;
    @Autowired
    IUserInfoRepository iUserInfoRepository;
    @Autowired
    IProductRepository iProductRepository;
    @Autowired
    IPromotionRepository iPromotionRepository;
    @Autowired
    IExchangeRatePolicyRepository iExchangeRatePolicyRepository;

    @Autowired
    IPromotionService iPromotionService;

    @Override
    public ResponseData getOrderInvoiceDetail(String barcode, String IdExchangeRate) {
        try {
            Object[] result = (Object[]) iOrderInvoiceRepository.getProductActualPriceByBarcode(barcode);
            if (result == null || result.length == 0) {
                return new ResponseData(HttpStatus.NOT_FOUND, "Product not found", null);
            }
            if ((Integer) result[3] == 0) {
                return new ResponseData(HttpStatus.NOT_FOUND, "Out of stock", null);
            }

            // get từ câu lệnh SQL
            Integer productId = (Integer) result[0];
            // giá đã qua tính toán = [giá vàng thời điểm * trọng lượng sản phẩm] + tiền
            // công + tiền đá
            double priceRaw = (Double) result[2];
            Integer quantityMax = (Integer) result[3];
            // find product để gửi đi
            Product product2 = iProductRepository.findById(productId).get();
            // lấy danh sách promotion by id của product
            List<PromotionDTO> listPromotionDTOs = iPromotionService.getAllPromotionByIdProduct(product2.getId());
            // tìm exchange rate cho detail
            ExchangeRatePolicy exchangeRatePolicy = iExchangeRatePolicyRepository.findById(IdExchangeRate)
                    .orElseThrow(() -> new BadRequestException("Not found exchange rate"));
            // tạo đối tượng gửi về
            OrderInvoiceDetailDTO orderInvoiceDetailDTO = new OrderInvoiceDetailDTO();
            // Giá bán = giá vốn sản phẩm * tỉ lệ áp giá
            priceRaw = priceRaw * exchangeRatePolicy.getRate();
            // giá tổng = giá bán - promotion giảm
            double totalPrice = priceRaw;
            for (PromotionDTO promotionDTO : listPromotionDTOs) {
                totalPrice -= totalPrice * promotionDTO.getValue() / 100;
            }

            orderInvoiceDetailDTO.setProductDTO(product2.getDTO());
            orderInvoiceDetailDTO.setPrice(priceRaw);
            orderInvoiceDetailDTO.setQuantity(quantityMax);
            orderInvoiceDetailDTO.setTotalPrice(totalPrice);
            orderInvoiceDetailDTO.setListPromotion(listPromotionDTOs);

            return new ResponseData(HttpStatus.OK, "Success", orderInvoiceDetailDTO);
        } catch (Exception e) {
            return new ResponseData(HttpStatus.BAD_REQUEST, "Error get order detail: " + e.getMessage(), null);
        }
    }
    // @Override
    // @Transactional
    // public ResponseData insertOrder(OrderRequest orderRequest) {
    // UserInfo userInfo = iUserInfoRepository.findById(orderRequest.getUserId())
    // .orElseThrow(() -> new ResourceNotFoundException("User not found: " +
    // orderRequest.getUserId()));
    // Employee employee =
    // iEmployeeRepository.findById(orderRequest.getEmployeeId())
    // .orElseThrow(
    // () -> new ResourceNotFoundException("Employee not found: " +
    // orderRequest.getEmployeeId()));

    // OrderInvoice orderInvoice = new OrderInvoice();
    // orderInvoice.setEmployee(employee);
    // orderInvoice.setUserInfo(userInfo);
    // // Thiết lập ngày hiện tại
    // orderInvoice.setDate(new Date());
    // iOrderInvoiceRepository.save(orderInvoice);
    // // Xử lý chi tiết đơn hàng
    // List<OrderInvoiceDetail> orderDetails = new ArrayList<>();
    // for (Map.Entry<Integer, Integer> entry :
    // orderRequest.getProducts().entrySet()) {
    // Integer productId = entry.getKey();
    // Integer quantity = entry.getValue();

    // Product product = iProductRepository.findById(productId)
    // .orElseThrow(() -> new ResourceNotFoundException("Product not found: " +
    // productId));

    // OrderInvoiceDetail detail = new OrderInvoiceDetail();
    // detail.setOrderInvoice(orderInvoice);
    // detail.setProduct(product);
    // // Thiết lập các giá trị chi tiết khác (nếu cần)
    // detail.setPrice(product.getPrice());
    // detail.setQuantity(quantity);
    // // duyệt mảng để lấy %
    // List<PromotionDTO> promotions =
    // iPromotionService.getAllPromotionByIdProduct(product.getId());

    // detail.setTotal_price(product.getPrice() * quantity);

    // orderDetails.add(detail);
    // }

    // return null;

    // }

}
