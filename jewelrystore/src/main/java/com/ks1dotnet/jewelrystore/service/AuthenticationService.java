package com.ks1dotnet.jewelrystore.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.Enum.TokenType;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.InvalidatedToken;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IAuthenticationRepository;
import com.ks1dotnet.jewelrystore.repository.IInvalidatedTokenRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;
import com.ks1dotnet.jewelrystore.utils.Utils;
import io.jsonwebtoken.Claims;

@Service
public class AuthenticationService implements IAuthenticationService {
    @Autowired
    private IAuthenticationRepository iAuthenticationRepository;
    @Autowired
    private JwtUtilsHelper jwtUtilsHelper;

    @Autowired
    private IInvalidatedTokenRepository iInvalidatedTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Utils utils;

    @Override
    public ResponseData login(String idEmp, String pinCode) {
        try {

            if (idEmp.isEmpty() || pinCode.isEmpty())
                throw new ApplicationException("SignUp fail. Not found employee",
                        HttpStatus.NOT_FOUND);

            Employee employee = iAuthenticationRepository.findById(idEmp)
                    .orElseThrow(() -> new ApplicationException("SignUp fail. Not found employee",
                            HttpStatus.NOT_FOUND));;
            if (!passwordEncoder.matches(pinCode, employee.getPinCode()))
                throw new ApplicationException("SignUp fail. Pincode Error", HttpStatus.NOT_FOUND);


            String at = jwtUtilsHelper.generateToken(employee.getId(), employee.getRole().getName(),
                    5, TokenType.ACCESS_TOKEN);
            String rt = jwtUtilsHelper.generateToken(employee.getId(), "", 10080,
                    TokenType.REFRESH_TOKEN); // 10080 = 7 days
            Map<String, String> responseDataMap = new HashMap<>();
            responseDataMap.put("at", at);
            responseDataMap.put("rt", rt);
            return new ResponseData(HttpStatus.OK, "SignUp successful", responseDataMap);
        } catch (ApplicationException e) {
            throw new ApplicationException(e.getMessage(), e.getErrorString(), e.getStatus());
        }
    }

    @Override
    public ResponseData logout() {

        try {
            Claims RTTokenClaims = jwtUtilsHelper.getAuthorizationByTokenType("rt");
            InvalidatedToken invalidatedToken =
                    new InvalidatedToken(RTTokenClaims.getId(), RTTokenClaims.getExpiration()
                            .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            iInvalidatedTokenRepository.saveAndFlush(invalidatedToken);
            return new ResponseData(HttpStatus.OK, "Logout successfully!", null);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at logout AuthenticationService: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at logout AuthenticationService: " + e.getMessage(),
                    "Error while logout");
        }
    }

    @Override
    public ResponseData refreshToken() {
        try {
            Claims RTTokenClaims = jwtUtilsHelper.getAuthorizationByTokenType("rt");
            Employee employee = iAuthenticationRepository.findById(RTTokenClaims.getSubject())
                    .orElseThrow(() -> new ApplicationException("SignUp fail. Not found employee",
                            HttpStatus.NOT_FOUND));;
            String at = jwtUtilsHelper.generateToken(employee.getId(), employee.getRole().getName(),
                    5, TokenType.ACCESS_TOKEN);
            Map<String, String> responseDataMap = new HashMap<>();
            responseDataMap.put("at", at);
            return new ResponseData(HttpStatus.OK, "Refresh token successfully", responseDataMap);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at refreshToken AuthenticationService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at refreshToken AuthenticationService: " + e.getMessage(),
                    "Error while refresh access token");
        }
    }

    @Override
    public ResponseData validateOtp(String otp, String idEmployee) {
        Employee em = iAuthenticationRepository.findById(idEmployee).orElse(null);
        if (em == null)
            return new ResponseData(HttpStatus.NOT_FOUND,
                    "Not found employee with id " + idEmployee, null);
        if (em.getOtp().equals(otp) && Duration
                .between(em.getOtpGenerDateTime(), LocalDateTime.now()).getSeconds() > (50 * 60)) {
            return new ResponseData(HttpStatus.REQUEST_TIMEOUT, "OTP code is timeout ", null);
        }
        if (!em.getOtp().equals(otp))
            return new ResponseData(HttpStatus.BAD_REQUEST, "OTP code is not correct ", null);
        String at = jwtUtilsHelper.generateToken(em.getId(), em.getRole().getName(), 5,
                TokenType.ACCESS_TOKEN);
        Map<String, String> responseDataMap = new HashMap<>();
        responseDataMap.put("at", at);
        return new ResponseData(HttpStatus.OK, "OK ", responseDataMap);
    }

    @Override
    public Employee getOtp(String idEmp) {
        Employee emp = iAuthenticationRepository.findById(idEmp)
                .orElseThrow(() -> new ApplicationException("No employee found with id: " + idEmp,
                        HttpStatus.NOT_FOUND));
        String otp = utils.generateOtp();
        emp.setOtp(otp);
        emp.setOtpGenerDateTime(LocalDateTime.now());
        iAuthenticationRepository.save(emp);
        return emp;
    }

}
