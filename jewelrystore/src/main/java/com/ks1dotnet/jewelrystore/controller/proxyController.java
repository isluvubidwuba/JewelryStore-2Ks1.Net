package com.ks1dotnet.jewelrystore.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.StringHttpMessageConverter;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/proxy")
@CrossOrigin("*")
public class proxyController {

    @GetMapping
    public ResponseEntity<String> getGoldPrices() {
        String url = "https://sjc.com.vn/xml/tygiavang.xml";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        try {
            String response = restTemplate.getForObject(url, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.set(HttpHeaders.CONTENT_TYPE, "application/xml; charset=UTF-8");
            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data");
        }
    }
}