package com.ks1dotnet.jewelrystore.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.MailService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;
import jakarta.servlet.http.Cookie;

@RestController
@RequestMapping("${apiURL}/authentication")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class AuthenticationController {
    @Autowired
    private IAuthenticationService iAuthenticationService;
    @Value("${domain}")
    private String domain;
    @Autowired
    private MailService mailService;
    @Autowired
    JwtUtilsHelper jwtUtilsHelper;

    @PostMapping("/signup")
    public ResponseEntity<?> login(@RequestBody Employee e) {
        try {
            ResponseData responseData = iAuthenticationService.login(e.getId(), e.getPinCode());
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
        if (otp == null || otp.isEmpty())
            throw new ApplicationException("Otp can not be empty", HttpStatus.BAD_REQUEST);
        ResponseData responseData = iAuthenticationService.validateOtp(otp, idEmployee);
        if (responseData.getStatus() != HttpStatus.OK)
            return new ResponseEntity<>(responseData, responseData.getStatus());
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/sendOtp/{idEmp}")
    public ResponseEntity<?> sendInvoice(@PathVariable String idEmp) {
        try {

            Employee emp = iAuthenticationService.getOtp(idEmp);
            ResponseData response = mailService.sendOtpEmail(emp.getEmail(),
                    emp.getFirstName() + " " + emp.getLastName(), emp.getOtp());
            return new ResponseEntity<>(response, response.getStatus());

        } catch (ApplicationException e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    "Something wrong while sending otp to employee have id: " + idEmp + " !");
        }
    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<?> isAuthenticated(
            @RequestHeader(value = "Authorization", required = false) String at,
            @CookieValue(value = "rt", required = false) String rt) {
        Map<String, String> mapValid = new HashMap<>();
        validateAndAddToken(at, "at", mapValid);
        validateAndAddToken(rt, "rt", mapValid);

        HttpStatus status = mapValid.containsValue("Valid") ? HttpStatus.OK : HttpStatus.FORBIDDEN;
        String message =
                mapValid.containsValue("Valid") ? "User authenticated" : "User unauthenticated";

        ResponseData response =
                new ResponseData(status, message, mapValid.isEmpty() ? null : mapValid);

        return new ResponseEntity<>(response, response.getStatus());
    }

    private void validateAndAddToken(String token, String tokenType, Map<String, String> mapValid) {
        if (token != null) {
            Object tokenObject = jwtUtilsHelper.verifyToken(token);
            if (tokenObject instanceof String) {
                mapValid.put(tokenType, (String) tokenObject);
            } else if (tokenObject != null)
                mapValid.put(tokenType, "Valid");
        } else
            mapValid.put(tokenType, null);
    }

}
