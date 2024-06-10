package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IUserInfoService {

        public List<UserInfo> findAll();

        public UserInfo save(UserInfo userInfo);

        public UserInfo findById(int id);

        public Map<String, Object> getHomePageUser(int page);

        public ResponseData insertUserInfo(MultipartFile file, String full_name, String phoneNumber, String email,
                        int roleId,
                        String address);

        public ResponseData updateUserInfo(MultipartFile file, int id, String fullName, String phoneNumber,
                        String email,
                        int roleId, String address);

        public Map<String, Object> listCustomer(int page);

        public Map<String, Object> listSupplier(int page);

        public Map<String, Object> findByCriteriaSupplier(String criteria, String query, int page);

        public Map<String, Object> findByCriteriaCustomer(String criteria, String query, int page);

        public UserInfo getUserInfo(int id);

}
