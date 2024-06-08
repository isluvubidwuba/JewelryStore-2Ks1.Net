package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.UserInfoDTO;
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

        boolean isSaveFileSuccess = true;
        String imageName;
        // Check if a file is provided
        // if (file != null && !file.isEmpty()) {
        // try {
        // isSaveFileSuccess = iFileService.savefile(file);
        // if (isSaveFileSuccess) {
        // imageName = file.getOriginalFilename();
        // } else {
        // responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        // responseData.setDesc("File save failed");
        // return responseData;
        // }
        // } catch (Exception e) {
        // responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        // responseData.setDesc("File save failed: " + e.getMessage());
        // return responseData;
        // }
        // } else {
        // imageName = "default_image.png";
        // }

        UserInfo userInfo = new UserInfo();
        userInfo.setFullName(fullName);
        userInfo.setPhoneNumber(phoneNumber);
        userInfo.setEmail(email);
        userInfo.setAddress(address);
        userInfo.setRole(iRoleService.findById(roleId));
        // userInfo.setImage(imageName); // Set the image name, default or uploaded
        iUserInfoRepository.save(userInfo);

        responseData.setStatus(HttpStatus.OK);
        responseData.setDesc("Insert successful");
        return responseData;

    }

    @Override
    public UserInfoDTO updateUserInfo(MultipartFile file, int id, String fullName, String phoneNumber, String email,
            int roleId, String address) {
        // boolean isSaveFileSuccess = iFileService.savefile(file);
        Optional<UserInfo> userInfo = iUserInfoRepository.findById(id);
        System.out.println(userInfo);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        if (userInfo.isPresent()) {
            UserInfo userInfo1 = new UserInfo();
            userInfo1.setId(id);
            userInfo1.setFullName(fullName);
            userInfo1.setPhoneNumber(phoneNumber);
            userInfo1.setEmail(email);
            userInfo1.setRole(iRoleService.findById(roleId));
            userInfo1.setAddress(address);

            // if (isSaveFileSuccess) {
            // userInfo1.setImage(file.getOriginalFilename());
            // } else {
            // userInfo1.setImage(userInfo.get().getImage());
            // }
            // iUserInfoRepository.save(userInfo1);
            // userInfoDTO = userInfo1.getDTO();
        }
        return userInfoDTO;
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
