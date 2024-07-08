package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IAuthenticationService {
    public ResponseData login(String idEmp, String pinCode);

    public ResponseData logout();

    public ResponseData refreshToken();

    public ResponseData validateOtp(String otp, String idEmployee);

    public Employee getOtp(String idEmp);
}
