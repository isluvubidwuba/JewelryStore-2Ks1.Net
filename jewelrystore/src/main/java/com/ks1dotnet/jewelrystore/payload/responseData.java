package com.ks1dotnet.jewelrystore.payload;

import lombok.*;


@Data
public class responseData {
    private int status = 200;
    private String desc;
    private Object data;
}
