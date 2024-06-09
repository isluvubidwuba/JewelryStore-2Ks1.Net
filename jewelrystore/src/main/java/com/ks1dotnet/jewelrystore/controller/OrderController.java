package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.OrderService;

@RestController
@RequestMapping("/order")
@CrossOrigin("*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/invoice-detail")
    public ResponseEntity<ResponseData> getOrderInvoiceDetail(@RequestParam String barcode,
            @RequestParam String exchangeRateId) {
        ResponseData responseData = orderService.getOrderInvoiceDetail(barcode, exchangeRateId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData> createOrder(@RequestBody OrderInvoiceDTO orderInvoiceDTO) {
        ResponseData responseData = orderService.insertOrder(orderInvoiceDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }
}
