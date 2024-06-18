package com.ks1dotnet.jewelrystore.dto;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

public abstract class PaymentDTO {
    @Builder
    @Getter
    public static class VNPayResponse {
        public HttpStatus code;
        public String message;
        public String paymentUrl;
    }
}