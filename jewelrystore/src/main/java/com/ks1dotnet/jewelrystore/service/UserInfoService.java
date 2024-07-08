package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.UserInfoDTO;
import com.ks1dotnet.jewelrystore.entity.EarnPoints;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.ICustomerTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IEarnPointsRepository;
import com.ks1dotnet.jewelrystore.repository.IUserInfoRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IUserInfoService;

@Service
public class UserInfoService implements IUserInfoService {
    @Autowired
    private IUserInfoRepository iUserInfoRepository;

    @Autowired
    private ICustomerTypeRepository iCustomerTypeRepository;

    @Autowired
    private IEarnPointsRepository iEarnPointsRepository;

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
        return iUserInfoRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("NOT FOUND USER WITH THIS ID:" + id,
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseData insertUserInfo(MultipartFile file, String fullName, String phoneNumber,
            String email, int roleId, String address) {
        ResponseData responseData = new ResponseData();
        phoneNumber = phoneNumber.trim();
        email = email.trim();

        String fileName = null;

        // Upload hình ảnh nếu có
        if (file != null && !file.isEmpty()) {
            fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
        } else {
            fileName = "3428f415-1df9-499d-93d3-1ce159b12c06_2024-06-12";
        }

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

        iUserInfoRepository.save(userInfo);
        // If role is 4, initialize and save EarnPoints
        if (roleId == 4) {
            EarnPoints earnPoints = new EarnPoints();
            earnPoints.setPoint(0);
            earnPoints.setCustomerType(iCustomerTypeRepository.findById(1)
                    .orElseThrow(() -> new ApplicationException("Not Found Customer Type ID 1",
                            HttpStatus.NOT_FOUND)));
            earnPoints.setUserInfo(userInfo);

            iEarnPointsRepository.save(earnPoints);
        }

        // Tim user vua duoc insert vao he thong
        responseData.setData(userInfo.getDTO());
        responseData.setStatus(HttpStatus.OK);
        responseData.setDesc("Insert User Information Successful");
        return responseData;

    }

    @Override
    public ResponseData updateUserInfo(MultipartFile file, int id, String fullName,
            String phoneNumber, String email, int roleId, String address) {
        ResponseData responseData = new ResponseData();

        String fileName = null;
        if (file != null && !file.isEmpty()) {
            fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
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
        if (!userInfo.getPhoneNumber().equals(phoneNumber)
                && iUserInfoRepository.existsByPhoneNumber(phoneNumber)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Phone number already exists");
            return responseData;
        }

        userInfo.setFullName(fullName);
        userInfo.setPhoneNumber(phoneNumber);
        userInfo.setEmail(email);
        userInfo.setAddress(address);
        userInfo.setRole(iRoleService.findById(roleId));
        userInfo.setImage(fileName == null ? userInfo.getImage() : fileName); // Set the image name,
                                                                              // default or uploaded
        iUserInfoRepository.save(userInfo);
        responseData.setStatus(HttpStatus.OK);
        responseData.setDesc("Update successful");
        responseData.setData(userInfo.getDTO()); // Assuming UserInfo has a method to convert to DTO
        return responseData;
    }

    @Override
    public ResponseData listCustomer(int page) {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> response = new HashMap<>();
            int pageSize = 5;
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            Page<UserInfo> customersPage = iUserInfoRepository.findCustomersByRoleId(pageRequest);

            Page<UserInfoDTO> customersDTOPage = convertToDTOPage(customersPage);

            response.put("customers", customersDTOPage.getContent());
            response.put("totalPages", customersDTOPage.getTotalPages());
            response.put("currentPage", page);

            responseData.setStatus(HttpStatus.OK);
            responseData.setData(response);
            responseData.setDesc("Fetch successful");
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Fetch failed. Internal Server Error: " + e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData listSupplier(int page) {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> response = new HashMap<>();
            int pageSize = 5;
            PageRequest pageRequest = PageRequest.of(page, pageSize);
            Page<UserInfo> suppliersPage = iUserInfoRepository.findSuppliersByRoleId(pageRequest);

            Page<UserInfoDTO> suppliersDTOPage = convertToDTOPage(suppliersPage);

            response.put("suppliers", suppliersDTOPage.getContent());
            response.put("totalPages", suppliersDTOPage.getTotalPages());
            response.put("currentPage", page);

            responseData.setStatus(HttpStatus.OK);
            responseData.setData(response);
            responseData.setDesc("Fetch successful");
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Fetch failed. Internal Server Error: " + e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData findByCriteriaCustomer(String criteria, String query, int page) {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> response = new HashMap<>();
            PageRequest pageRequest = PageRequest.of(page, 5);
            Page<UserInfo> userInfoPage;

            switch (criteria.toLowerCase()) {
                case "id":
                    Optional<UserInfo> userInfoOpt =
                            iUserInfoRepository.findById(Integer.parseInt(query));
                    if (userInfoOpt.isPresent() && userInfoOpt.get().getRole().getId() == 4) {
                        List<UserInfo> userInfoList = new ArrayList<>();
                        userInfoList.add(userInfoOpt.get());
                        userInfoPage = new PageImpl<>(userInfoList, pageRequest, 1);
                    } else {
                        userInfoPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
                    }
                    break;

                case "name":
                    userInfoPage = iUserInfoRepository
                            .findCustomersByNameContainingIgnoreCase(query, pageRequest);
                    break;

                case "numberphone":
                    userInfoPage = iUserInfoRepository.findCustomersByPhoneNumberContaining(query,
                            pageRequest);
                    break;

                case "email":
                    userInfoPage = iUserInfoRepository
                            .findCustomersByEmailContainingIgnoreCase(query, pageRequest);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Tiêu chí tìm kiếm không hợp lệ: " + criteria);
            }

            Page<UserInfoDTO> userInfoDTOPage = convertToDTOPage(userInfoPage);

            response.put("customers", userInfoDTOPage.getContent());
            response.put("totalPages", userInfoDTOPage.getTotalPages());
            response.put("currentPage", page);

            responseData.setStatus(HttpStatus.OK);
            responseData.setData(response);
            responseData.setDesc("Fetch successful");
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Fetch failed. Internal Server Error: " + e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData findByCriteriaSupplier(String criteria, String query, int page) {
        ResponseData responseData = new ResponseData();
        try {
            Map<String, Object> response = new HashMap<>();
            PageRequest pageRequest = PageRequest.of(page, 5);
            Page<UserInfo> userInfoPage;

            switch (criteria.toLowerCase()) {
                case "id":
                    Optional<UserInfo> userInfoOpt =
                            iUserInfoRepository.findById(Integer.parseInt(query));
                    if (userInfoOpt.isPresent() && userInfoOpt.get().getRole().getId() == 5) {
                        List<UserInfo> userInfoList = new ArrayList<>();
                        userInfoList.add(userInfoOpt.get());
                        userInfoPage = new PageImpl<>(userInfoList, pageRequest, 1);
                    } else {
                        userInfoPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
                    }
                    break;

                case "name":
                    userInfoPage = iUserInfoRepository
                            .findSuppliersByNameContainingIgnoreCase(query, pageRequest);
                    break;

                case "numberphone":
                    userInfoPage = iUserInfoRepository.findSuppliersByPhoneNumberContaining(query,
                            pageRequest);
                    break;

                case "email":
                    userInfoPage = iUserInfoRepository
                            .findSuppliersByEmailContainingIgnoreCase(query, pageRequest);
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Tiêu chí tìm kiếm không hợp lệ: " + criteria);
            }

            Page<UserInfoDTO> userInfoDTOPage = convertToDTOPage(userInfoPage);

            response.put("suppliers", userInfoDTOPage.getContent());
            response.put("totalPages", userInfoDTOPage.getTotalPages());
            response.put("currentPage", page);

            responseData.setStatus(HttpStatus.OK);
            responseData.setData(response);
            responseData.setDesc("Fetch successful");
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Fetch failed. Internal Server Error: " + e.getMessage());
        }
        return responseData;
    }

    @Override
    public ResponseData getUserInfo(int id) {
        UserInfo userInfo = iUserInfoRepository.findById(id).orElse(null);
        UserInfoDTO dto = userInfo.getDTO();
        dto.setImage(url.trim() + filePath.trim() + dto.getImage());
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.OK);
        responseData.setData(dto);
        return responseData;
    }

    private Page<UserInfoDTO> convertToDTOPage(Page<UserInfo> userPage) {
        List<UserInfoDTO> dtoList = userPage.getContent().stream().map(userinfo -> {
            UserInfoDTO dto = userinfo.getDTO();
            dto.setImage(url.trim() + filePath.trim() + dto.getImage());
            return dto;
        }).collect(Collectors.toList());
        return new PageImpl<>(dtoList, userPage.getPageable(), userPage.getTotalElements());
    }

    @Override
    public ResponseData getSupplierInfo(int id) {
        try {
            UserInfo userInfo = iUserInfoRepository.findSupplierById(id);
            ResponseData responseData = new ResponseData();
            responseData.setData(userInfo.getDTO());
            responseData.setStatus(HttpStatus.OK);
            return responseData;
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getSupplierInfo UserInfoService: " + e.getMessage(),
                    "Find supplier error");
        }

    }

    @Override
    public ResponseData getCustomerInfo(int id) {
        try {
            UserInfo userInfo = iUserInfoRepository.findCustomerById(id);
            ResponseData responseData = new ResponseData();
            responseData.setData(userInfo.getDTO());
            responseData.setStatus(HttpStatus.OK);
            return responseData;
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getCustomerInfo UserInfoService: " + e.getMessage(),
                    "Find Customer error");
        }
    }

    @Override
    public ResponseData findByPhoneNumber(String phone) {
        try {
            Optional<UserInfo> optionalUserInfo = iUserInfoRepository.findByPhoneNumber(phone);
            if (optionalUserInfo.isPresent()) {
                UserInfo userInfo = optionalUserInfo.get();
                ResponseData responseData = new ResponseData();
                responseData.setData(userInfo.getDTO());
                responseData.setStatus(HttpStatus.OK);
                responseData.setDesc("Found Customer In The System");
                return responseData;
            } else {
                ResponseData responseData = new ResponseData();
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setDesc("Not Found Customer In The System !!!");
                return responseData;
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at findByPhoneNumber UserInfoService: " + e.getMessage(),
                    "An error occurred while finding the customer");
        }
    }

    @Override
    public ResponseData findByEmail(String mail) {
        try {
            Optional<UserInfo> optionalUserInfo = iUserInfoRepository.findByEmail(mail);
            if (optionalUserInfo.isPresent()) {
                UserInfo userInfo = optionalUserInfo.get();
                ResponseData responseData = new ResponseData();
                responseData.setData(userInfo.getDTO());
                responseData.setStatus(HttpStatus.OK);
                responseData.setDesc("Found Customer In The System");
                return responseData;
            } else {
                ResponseData responseData = new ResponseData();
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setDesc("Not Found Customer In The System !!!");
                return responseData;
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at findByEmail UserInfoService: " + e.getMessage(),
                    "An error occurred while finding the customer");
        }
    }

    @Override
    public ResponseData findByPhoneSupplier(String phone) {
        try {
            Optional<UserInfo> optionalUserInfo =
                    iUserInfoRepository.findSupplierByPhoneNumber(phone);
            if (optionalUserInfo.isPresent()) {
                UserInfo userInfo = optionalUserInfo.get();
                ResponseData responseData = new ResponseData();
                responseData.setData(userInfo.getDTO());
                responseData.setStatus(HttpStatus.OK);
                responseData.setDesc("Found Customer In The System");
                return responseData;
            } else {
                ResponseData responseData = new ResponseData();
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setDesc("Not Found Customer In The System !!!");
                return responseData;
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at findByPhoneSupplier UserInfoService: " + e.getMessage(),
                    "An error occurred while finding the customer");
        }
    }

    @Override
    public ResponseData findByEmailSupplier(String mail) {
        try {
            Optional<UserInfo> optionalUserInfo = iUserInfoRepository.findSupplierByEmail(mail);
            if (optionalUserInfo.isPresent()) {
                UserInfo userInfo = optionalUserInfo.get();
                ResponseData responseData = new ResponseData();
                responseData.setData(userInfo.getDTO());
                responseData.setStatus(HttpStatus.OK);
                responseData.setDesc("Found Customer In The System");
                return responseData;
            } else {
                ResponseData responseData = new ResponseData();
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setDesc("Not Found Customer In The System !!!");
                return responseData;
            }
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at findByEmailSupplier UserInfoService: " + e.getMessage(),
                    "An error occurred while finding the customer");
        }
    }

}
