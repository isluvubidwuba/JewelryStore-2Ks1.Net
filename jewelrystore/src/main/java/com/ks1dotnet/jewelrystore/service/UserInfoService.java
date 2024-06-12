package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.UserInfoDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IUserInfoRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IUserInfoService;

@Service
public class UserInfoService implements IUserInfoService {
    @Autowired
    private IUserInfoRepository iUserInfoRepository;
    @Autowired
    private IRoleService iRoleService;

    @Value("${fileUpload.userPath}")
    private String filePath;

    @Value("${firebase.img-url}")
    private String url;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Override
    public List<UserInfo> findAll() {
        return iUserInfoRepository.findAll();
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        return iUserInfoRepository.save(userInfo);
    }

    @Override
    public UserInfo findById(int id) {
        return iUserInfoRepository.findById(id).orElse(null);
    }

    @Override
    public Map<String, Object> getHomePageUser(int page) {
        Map<String, Object> response = new HashMap<>();
        PageRequest pageRequest = PageRequest.of(page, 5);
        List<UserInfoDTO> userInfoDTO = new ArrayList<>();
        Page<UserInfo> listData = iUserInfoRepository.findAll(pageRequest);

        for (UserInfo e : listData) {
            userInfoDTO.add(e.getDTO());
        }
        response.put("employees", userInfoDTO);
        response.put("totalPages", listData.getTotalPages());
        response.put("currentPage", page);
        return response;
    }

    @Override
    public ResponseData insertUserInfo(MultipartFile file, String fullName, String phoneNumber, String email,
            int roleId,
            String address) {
        ResponseData responseData = new ResponseData();
        phoneNumber.trim();
        email.trim();

        String fileName = null;

        // Upload hình ảnh nếu có
        if (file != null && !file.isEmpty()) {
            responseData = firebaseStorageService.uploadImage(file, filePath);
            if (responseData.getStatus() == HttpStatus.OK) {
                fileName = (String) responseData.getData();
                responseData.setStatus(HttpStatus.OK);
                responseData.setDesc("Find Image Successfully !!!");
            } else {
                responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                responseData.setDesc("Upload image error ! System Error !");
                return responseData;
            }
        }
        responseData = firebaseStorageService.uploadImage(file, filePath);
        fileName = "31ab6d6b-86cf-443f-9041-3c394b17ac0b_2024-06-10";
        if (responseData.getStatus() == HttpStatus.OK)
            fileName = (String) responseData.getData();


        // Check if email or phone number already exists
        if (iUserInfoRepository.existsByEmail(email)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Email already exists");
            return responseData;
        }

        if (iUserInfoRepository.existsByPhoneNumber(phoneNumber)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Phone number already exists");
            return responseData;
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setFullName(fullName);
        userInfo.setPhoneNumber(phoneNumber);
        userInfo.setEmail(email);
        userInfo.setAddress(address);
        userInfo.setRole(iRoleService.findById(roleId));
        userInfo.setImage(fileName);
        // userInfo.setImage(imageName); // Set the image name, default or uploaded
        iUserInfoRepository.save(userInfo);

        responseData.setStatus(HttpStatus.OK);
        responseData.setDesc("Insert successful");
        return responseData;

    }

    @Override
    public ResponseData updateUserInfo(MultipartFile file, int id, String fullName, String phoneNumber, String email,
            int roleId, String address) {
        ResponseData responseData = new ResponseData();

        String fileName = null;

        // Upload hình ảnh nếu có
        if (file != null && !file.isEmpty()) {
            responseData = firebaseStorageService.uploadImage(file, filePath);
            if (responseData.getStatus() == HttpStatus.OK) {
                fileName = (String) responseData.getData();
            } else {
                responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                responseData.setDesc("Upload image error ! System Error !");
                return responseData;
            }
        }

        // Nếu hình ảnh không được upload, lấy hình ảnh cũ
        if (fileName == null) {
            Optional<UserInfo> existingUserInfoOpt = iUserInfoRepository.findById(id);
            if (existingUserInfoOpt.isPresent()) {
                UserInfo existingUserinfo = existingUserInfoOpt.get();
                fileName = existingUserinfo.getImage();
            } else {
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setDesc("Not found User Information");
                return responseData;
            }
        }

        Optional<UserInfo> existingUser = iUserInfoRepository.findById(id);
        if (!existingUser.isPresent()) {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("User not found");
            return responseData;
        }

        UserInfo userInfo = existingUser.get();

        // Check if email has changed and already exists for other users
        if (!userInfo.getEmail().equals(email) && iUserInfoRepository.existsByEmail(email)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Email already exists");
            return responseData;
        }

        // Check if phone number has changed and already exists for other users
        if (!userInfo.getPhoneNumber().equals(phoneNumber) && iUserInfoRepository.existsByPhoneNumber(phoneNumber)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Phone number already exists");
            return responseData;
        }

        userInfo.setFullName(fullName);
        userInfo.setPhoneNumber(phoneNumber);
        userInfo.setEmail(email);
        userInfo.setAddress(address);
        userInfo.setRole(iRoleService.findById(roleId));
        userInfo.setImage(fileName); // Set the image name, default or uploaded

        iUserInfoRepository.save(userInfo);

        responseData.setStatus(HttpStatus.OK);
        responseData.setDesc("Update successful");
        responseData.setData(userInfo.getDTO()); // Assuming UserInfo has a method to convert to DTO
        return responseData;
    }

    @Override
    public Map<String, Object> listCustomer(int page) {
        Map<String, Object> response = new HashMap<>();
        int pageSize = 5;
        List<UserInfoDTO> userInfoDTOList = new ArrayList<>();
        List<UserInfo> customers = iUserInfoRepository.findCustomersByRoleId();

        int start = page * pageSize;
        int end = Math.min((page + 1) * pageSize, customers.size());
        List<UserInfo> paginatedCustomers = customers.subList(start, end);

        for (UserInfo userInfo : paginatedCustomers) {
            UserInfoDTO dto = userInfo.getDTO();
            userInfoDTOList.add(dto);
        }

        response.put("customers", userInfoDTOList);
        response.put("totalPages", (customers.size() + pageSize - 1) / pageSize); // manually calculate total pages
        response.put("currentPage", page);
        return response;
    }

    @Override
    public Map<String, Object> listSupplier(int page) {
        Map<String, Object> response = new HashMap<>();
        int pageSize = 5;
        List<UserInfoDTO> userInfoDTOList = new ArrayList<>();
        List<UserInfo> supplier = iUserInfoRepository.findSuppliersByRoleId();

        int start = page * pageSize;
        int end = Math.min((page + 1) * pageSize, supplier.size());
        List<UserInfo> paginatedCustomers = supplier.subList(start, end);

        for (UserInfo userInfo : paginatedCustomers) {
            UserInfoDTO dto = userInfo.getDTO();
            userInfoDTOList.add(dto);
        }

        response.put("customers", userInfoDTOList);
        response.put("totalPages", (supplier.size() + pageSize - 1) / pageSize); // manually calculate total pages
        response.put("currentPage", page);
        return response;
    }

    @Override
    public Map<String, Object> findByCriteriaCustomer(String criteria, String query, int page) {
        Map<String, Object> response = new HashMap<>();
        PageRequest pageRequest = PageRequest.of(page, 5);
        Page<UserInfo> userInfoPage;

        switch (criteria.toLowerCase()) {
            case "id":
                Optional<UserInfo> userInfoOpt = iUserInfoRepository.findById(Integer.parseInt(query));
                if (userInfoOpt.isPresent() && userInfoOpt.get().getRole().getId() == 4) {
                    List<UserInfo> userInfoList = Collections.singletonList(userInfoOpt.get());
                    userInfoPage = new PageImpl<>(userInfoList, pageRequest, 1);
                } else {
                    userInfoPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
                }
                break;

            case "name":
                userInfoPage = iUserInfoRepository.findCustomersByNameContainingIgnoreCase(query, pageRequest);
                break;

            case "numberphone":
                userInfoPage = iUserInfoRepository.findCustomersByPhoneNumberContaining(query, pageRequest);
                break;

            case "email":
                userInfoPage = iUserInfoRepository.findCustomersByEmailContainingIgnoreCase(query, pageRequest);
                break;

            default:
                throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }

        List<UserInfoDTO> userInfoDTOList = new ArrayList<>();
        for (UserInfo userInfo : userInfoPage) {
            userInfoDTOList.add(userInfo.getDTO());
        }

        response.put("customers", userInfoDTOList);
        response.put("totalPages", userInfoPage.getTotalPages());
        response.put("currentPage", page);
        return response;
    }

    @Override
    public Map<String, Object> findByCriteriaSupplier(String criteria, String query, int page) {
        Map<String, Object> response = new HashMap<>();
        PageRequest pageRequest = PageRequest.of(page, 5);
        Page<UserInfo> userInfoPage;

        switch (criteria.toLowerCase()) {
            case "id":
                Optional<UserInfo> userInfoOpt = iUserInfoRepository.findById(Integer.parseInt(query));
                if (userInfoOpt.isPresent() && userInfoOpt.get().getRole().getId() == 5) {
                    List<UserInfo> userInfoList = Collections.singletonList(userInfoOpt.get());
                    userInfoPage = new PageImpl<>(userInfoList, pageRequest, 1);
                } else {
                    userInfoPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
                }
                break;

            case "name":
                userInfoPage = iUserInfoRepository.findSuppliersByNameContainingIgnoreCase(query, pageRequest);
                break;

            case "numberphone":
                userInfoPage = iUserInfoRepository.findSuppliersByPhoneNumberContaining(query, pageRequest);
                break;

            case "email":
                userInfoPage = iUserInfoRepository.findSuppliersByEmailContainingIgnoreCase(query, pageRequest);
                break;

            default:
                throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }

        List<UserInfoDTO> userInfoDTOList = new ArrayList<>();
        for (UserInfo userInfo : userInfoPage) {
            userInfoDTOList.add(userInfo.getDTO());
        }

        response.put("suppliers", userInfoDTOList);
        response.put("totalPages", userInfoPage.getTotalPages());
        response.put("currentPage", page);
        return response;
    }

    @Override
    public UserInfo getUserInfo(int id) {
        return iUserInfoRepository.findById(id).orElse(null);
    }

}
