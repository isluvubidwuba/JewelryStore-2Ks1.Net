package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.Enum.TokenType;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;
import jakarta.servlet.http.Cookie;

@RestController
@RequestMapping("${apiURL}/authentication")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class AuthenticationController {
    @Autowired
    private IAuthenticationService iAuthenticationService;
    @Value("${domain}")
    private String domain;

    @PostMapping("/signup")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> login(@RequestParam String id, @RequestParam String pinCode) {
        try {
            ResponseData responseData = iAuthenticationService.login(id, pinCode);
            Map<String, String> dataMap = (Map<String, String>) responseData.getData();

            if (dataMap != null && dataMap.containsKey("rt")) {
                String refreshToken = dataMap.get("rt");
                Cookie newCookie = new Cookie("rt", refreshToken);
                newCookie.setHttpOnly(true);
                newCookie.setPath("/");
                newCookie.setSecure(true);
                newCookie.setMaxAge(7 * 24 * 60 * 60); // 1 week
                String cookieHeaderValue =
                        String.format("%s=%s; HttpOnly; Path=/; Max-Age=%d; SameSite=none; Secure",
                                newCookie.getName(), newCookie.getValue(), newCookie.getMaxAge());

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, cookieHeaderValue);
                dataMap.remove("rt");
                responseData.setData(dataMap);

                return new ResponseEntity<>(responseData, headers, responseData.getStatus());
            } else {
                throw new ApplicationException("Refresh token not found in response data",
                        HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            throw new ApplicationException(
                    "Error at login AuthenticationController: " + ex.getMessage(), "Login failed",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('REFRESH_TOKEN')")
    public ResponseEntity<?> logout() {
        ResponseData responseData = iAuthenticationService.logout();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/refreshToken")
    @PreAuthorize("hasAuthority('REFRESH_TOKEN')")
    public ResponseEntity<?> getRefreshToken() {

        ResponseData responseData = iAuthenticationService.refreshToken();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/context")
    public ResponseEntity<?> getContext() {
        ResponseData responseData = new ResponseData(HttpStatus.OK, "Context",
                SecurityContextHolder.getContext().getAuthentication());
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<?> validateOtp(@RequestParam("otp") String otp,
            @RequestParam String idEmployee) {
        if (!idEmployee.startsWith("SE"))
            throw new ApplicationException("No employee found " + idEmployee, HttpStatus.NOT_FOUND);
        if (otp.isEmpty())
            throw new ApplicationException("Otp can not be empty", HttpStatus.BAD_REQUEST);
        ResponseData responseData = iAuthenticationService.validateOtp(otp, idEmployee);
        if (responseData.getStatus() != HttpStatus.OK)
            return new ResponseEntity<>(responseData, responseData.getStatus());
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
