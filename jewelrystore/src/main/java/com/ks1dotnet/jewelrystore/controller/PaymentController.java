package com.ks1dotnet.jewelrystore.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.PaymentDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseObject;
import com.ks1dotnet.jewelrystore.service.PaymentService;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) {
        String status = request.getParameter("vnp_ResponseCode");
        try {
            System.out.println("VNP Response Code: " + status); // In mã phản hồi VNPAY ra console

            if (status == null || !"00".equals(status)) {
                System.out.println("Payment failed or no response code."); // Thêm log cho trường hợp thanh toán thất
                                                                           // bại
                response.sendRedirect(
                        "http://127.0.0.1:5500/JewelryStore-2Ks1.Net/jewelrystore/src/main/resources/templates/InvoiceDefault.html?paymentSuccess=false");
            } else {
                System.out.println("Payment succeeded."); // Thêm log cho trường hợp thanh toán thành công
                response.sendRedirect(
                        "http://127.0.0.1:5500/JewelryStore-2Ks1.Net/jewelrystore/src/main/resources/templates/InvoiceDefault.html?paymentSuccess=true");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}