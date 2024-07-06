package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;
import jakarta.servlet.http.Cookie;

@RestController
@RequestMapping("/authentication")
@CrossOrigin("*")

public class AuthenticationController {
    @Autowired
    private IAuthenticationService iAuthenticationService;

    @PostMapping("/signup")
    public ResponseEntity<?> login(@RequestParam String id, @RequestParam String pinCode) {
        ResponseData responseData = iAuthenticationService.login(id, pinCode);
        Map<String, String> dataMap = (Map<String, String>) responseData.getData();

        if (dataMap != null && dataMap.containsKey("rt")) {
            String refreshToken = dataMap.get("rt");
            Cookie newCookie = new Cookie("rt", refreshToken);
            newCookie.setHttpOnly(true);
            newCookie.setPath("/");
            newCookie.setMaxAge(7 * 24 * 60 * 60); // 1 week

            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", String.format("%s=%s; HttpOnly; Path=/; Max-Age=%d",
                    newCookie.getName(), newCookie.getValue(), newCookie.getMaxAge()));

            dataMap.remove("rt");
            responseData.setData(dataMap);

            return new ResponseEntity<>(responseData, headers, responseData.getStatus());
        } else {
            throw new ApplicationException("Refresh token not found in response data",
                    HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseData responseData = iAuthenticationService.logout();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<?> getRefreshToken() {

        ResponseData responseData = iAuthenticationService.refreshToken();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
