package com.ks1dotnet.jewelrystore.payload;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseData {
    private HttpStatus status;
    private String desc;
    private Object data;
}
