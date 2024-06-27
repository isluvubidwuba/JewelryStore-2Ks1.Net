package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IUserInfoService {

        public List<UserInfo> findAll();

        public UserInfo save(UserInfo userInfo);

        public UserInfo findById(int id);

        public ResponseData insertUserInfo(MultipartFile file, String fullName, String phoneNumber, String email,
                        int roleId,
                        String address);

        public ResponseData updateUserInfo(MultipartFile file, int id, String fullName, String phoneNumber,
                        String email,
                        int roleId, String address);

        public ResponseData listCustomer(int page);

        public ResponseData listSupplier(int page);

        public ResponseData findByCriteriaSupplier(String criteria, String query, int page);

        public ResponseData findByCriteriaCustomer(String criteria, String query, int page);

        public ResponseData getUserInfo(int id);

        public ResponseData getSupplierInfo(int id);

        public ResponseData getCustomerInfo(int id);

        public ResponseData findByPhoneNumber(String phone);

}
