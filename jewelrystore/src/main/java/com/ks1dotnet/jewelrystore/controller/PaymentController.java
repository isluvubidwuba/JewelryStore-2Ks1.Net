package com.ks1dotnet.jewelrystore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.PaymentDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.payload.ResponseObject;
import com.ks1dotnet.jewelrystore.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<ResponseData> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        System.out.println("check call back thực hiện : " + status);

        if (status == null) {
            System.out.println("No response code.");
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, "No response code.", null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }

        String message;
        HttpStatus httpStatus;

        switch (status) {
            case "00":
                message = "Giao dịch thành công.";
                httpStatus = HttpStatus.OK;
                break;
            case "07":
                message = "Giao dịch bị nghi ngờ.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "09":
                message = "Thẻ/Tài khoản chưa đăng ký dịch vụ InternetBanking.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "10":
                message = "Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "11":
                message = "Đã hết hạn chờ thanh toán.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "12":
                message = "Thẻ/Tài khoản của khách hàng bị khóa.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "13":
                message = "Khách hàng nhập sai mật khẩu xác thực giao dịch (OTP).";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "24":
                message = "Khách hàng hủy giao dịch.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "51":
                message = "Tài khoản không đủ số dư.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "65":
                message = "Tài khoản vượt quá hạn mức giao dịch trong ngày.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "75":
                message = "Ngân hàng thanh toán đang bảo trì.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            case "79":
                message = "KH nhập sai mật khẩu thanh toán quá số lần quy định.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
            default:
                message = "Unknown response code.";
                httpStatus = HttpStatus.BAD_REQUEST;
                break;
        }

        System.out.println(message);
        ResponseData responseData = new ResponseData(httpStatus, message, status);
        return new ResponseEntity<>(responseData, httpStatus);
    }

}