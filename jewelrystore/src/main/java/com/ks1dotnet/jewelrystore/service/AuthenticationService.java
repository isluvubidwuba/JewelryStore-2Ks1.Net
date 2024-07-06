package com.ks1dotnet.jewelrystore.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
            String rt = jwtUtilsHelper.generateToken(employee.getId(), employee.getRole().getName(),
                    10080, TokenType.REFRESH_TOKEN); // 10080 = 7 days
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
            var context = SecurityContextHolder.getContext().getAuthentication();
            if (context == null || !context.isAuthenticated()
                    || context.getPrincipal().equals("anonymousUser")) {
                throw new ApplicationException("User not authenticated!", HttpStatus.UNAUTHORIZED);
            }
            Map<String, Claims> claimsMap = (Map<String, Claims>) context.getCredentials();
            Claims RTTokenClaims = claimsMap.get("rt");
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
            var context = SecurityContextHolder.getContext().getAuthentication();
            if (context == null || !context.isAuthenticated()
                    || context.getPrincipal().equals("anonymousUser")) {
                throw new ApplicationException("User not authenticated!", HttpStatus.UNAUTHORIZED);
            }
            Map<String, Claims> claimsMap = (Map<String, Claims>) context.getCredentials();
            Claims RTTokenClaims = claimsMap.get("rt");
            String at = jwtUtilsHelper.generateToken(RTTokenClaims.getSubject(),
                    RTTokenClaims.get("role", String.class), 5, TokenType.ACCESS_TOKEN);
            return new ResponseData(HttpStatus.OK, "Refresh token successfully", at);
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

}
