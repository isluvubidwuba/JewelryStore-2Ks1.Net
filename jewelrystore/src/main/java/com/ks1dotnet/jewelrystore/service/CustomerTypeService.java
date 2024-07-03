package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.CustomerTypeDTO;
import com.ks1dotnet.jewelrystore.dto.EarnPointsDTO;
import com.ks1dotnet.jewelrystore.dto.RoleDTO;
import com.ks1dotnet.jewelrystore.dto.UserInfoDTO;
import com.ks1dotnet.jewelrystore.entity.CustomerType;
import com.ks1dotnet.jewelrystore.entity.EarnPoints;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.ICustomerTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IEarnPointsRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.ICustomerTypeService;

import jakarta.transaction.Transactional;

@Service
public class CustomerTypeService implements ICustomerTypeService {

    @Autowired
    private ICustomerTypeRepository iCustomerTypeRepository;

    @Autowired
    private IEarnPointsRepository iEarnPointsRepository;

    @Transactional
    @Override
    public ResponseData updatePointCondition(Integer id, String type, Integer pointCondition) {
        try {
            // Tìm và kiểm tra xem CustomerType có tồn tại hay không
            CustomerType customerType = iCustomerTypeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Can Not Found ID :" + id));

            customerType.setType(type);
            customerType.setPointCondition(pointCondition);
            iCustomerTypeRepository.save(customerType);
            // Cập nhật danh sách EarnPoints
            List<EarnPointsDTO> updatedEarnPoints = updateAllEarnPoints();
            return new ResponseData(HttpStatus.OK, "Point condition updated and EarnPoints updated successfully",
                    updatedEarnPoints);
        } catch (ResourceNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return new ResponseData(HttpStatus.NOT_FOUND, "Error: " + e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", null);
        }
    }

    @Override
    @Transactional
    public ResponseData addCustomerType(String type, Integer pointCondition) {
        CustomerType customerType = new CustomerType();
        customerType.setType(type);
        customerType.setPointCondition(pointCondition);
        iCustomerTypeRepository.save(customerType);
        List<EarnPointsDTO> updatedEarnPoints = updateAllEarnPoints();
        return new ResponseData(HttpStatus.OK, "CustomerType added and EarnPoints updated successfully",
                updatedEarnPoints);
    }

    @Override
    @Transactional
    public ResponseData deleteCustomerTypeAndUpdateRanks(Integer customerTypeId) {
        Optional<CustomerType> customerTypeOptional = iCustomerTypeRepository.findById(customerTypeId);
        if (customerTypeOptional.isPresent()) {
            iCustomerTypeRepository.deleteById(customerTypeId);
            List<EarnPointsDTO> updatedEarnPoints = updateAllEarnPoints();
            return new ResponseData(HttpStatus.OK, "CustomerType deleted and EarnPoints updated successfully",
                    updatedEarnPoints);
        } else {
            return new ResponseData(HttpStatus.NOT_FOUND, "CustomerType not found", null);
        }
    }

    @Transactional
    public List<EarnPointsDTO> updateAllEarnPoints() {
        try {
            List<EarnPoints> earnPointsList = iEarnPointsRepository.findAll();
            List<CustomerTypeDTO> customerTypeDTOList = iCustomerTypeRepository.findAll().stream()
                    .map(ct -> new CustomerTypeDTO(ct.getId(), ct.getType(), ct.getPointCondition()))
                    .collect(Collectors.toList());

            List<EarnPointsDTO> updatedEarnPointsDTOList = new ArrayList<>();

            for (EarnPoints earnPoints : earnPointsList) {
                UserInfo userInfo = earnPoints.getUserInfo();
                if (userInfo.getRole().getName().equals("CUSTOMER")) { // Đảm bảo tên role là "CUSTOMER"
                    Integer totalPoints = earnPoints.getPoint();

                    // In ra tổng điểm của từng EarnPoints
                    System.out.println("User: " + userInfo.getFullName() + ", Total Points: " + totalPoints);

                    CustomerTypeDTO newCustomerTypeDTO = customerTypeDTOList.stream()
                            .filter(ct -> totalPoints >= ct.getPointCondition())
                            .max((ct1, ct2) -> Integer.compare(ct1.getPointCondition(), ct2.getPointCondition()))
                            .orElse(null);

                    // In ra loại khách hàng mới được tìm thấy
                    if (newCustomerTypeDTO != null) {
                        System.out.println("New CustomerType for user " + userInfo.getFullName() + ": "
                                + newCustomerTypeDTO.getType());
                    }

                    if (newCustomerTypeDTO != null
                            && !newCustomerTypeDTO.getId().equals(earnPoints.getCustomerType().getId())) {
                        // Chuyển đổi DTO thành entity để lưu vào cơ sở dữ liệu
                        CustomerType newCustomerType = new CustomerType();
                        newCustomerType.setId(newCustomerTypeDTO.getId());
                        newCustomerType.setType(newCustomerTypeDTO.getType());
                        newCustomerType.setPointCondition(newCustomerTypeDTO.getPointCondition());

                        earnPoints.setCustomerType(newCustomerType);
                        EarnPoints savedEarnPoints = iEarnPointsRepository.save(earnPoints); // Lưu và lấy kết quả trả
                                                                                             // về

                        // Chuyển đổi từ entity sang DTO để trả về
                        EarnPointsDTO earnPointsDTO = new EarnPointsDTO(
                                savedEarnPoints.getId(),
                                savedEarnPoints.getPoint(),
                                new UserInfoDTO(
                                        savedEarnPoints.getUserInfo().getId(),
                                        savedEarnPoints.getUserInfo().getFullName(),
                                        savedEarnPoints.getUserInfo().getPhoneNumber(),
                                        savedEarnPoints.getUserInfo().getEmail(),
                                        savedEarnPoints.getUserInfo().getAddress(),
                                        new RoleDTO(
                                                savedEarnPoints.getUserInfo().getRole().getId(),
                                                savedEarnPoints.getUserInfo().getRole().getName()),
                                        savedEarnPoints.getUserInfo().getImage()),
                                new CustomerTypeDTO(
                                        savedEarnPoints.getCustomerType().getId(),
                                        savedEarnPoints.getCustomerType().getType(),
                                        savedEarnPoints.getCustomerType().getPointCondition()));

                        updatedEarnPointsDTOList.add(earnPointsDTO);
                    }
                }
            }

            // In ra danh sách EarnPointsDTO đã cập nhật
            System.out.println("Updated EarnPointsDTOList: " + updatedEarnPointsDTOList);

            return updatedEarnPointsDTOList;
        } catch (Exception e) {
            // In ra thông tin lỗi không mong muốn trong quá trình cập nhật EarnPoints
            System.out.println("Error during updating earn points: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Trả về danh sách rỗng nếu có lỗi xảy ra
        }
    }

    @Override
    public ResponseData findAll() {
        ResponseData responseData = new ResponseData();
        try {
            List<CustomerTypeDTO> list = iCustomerTypeRepository.findAll().stream().map(CustomerType::getDTO)
                    .collect(Collectors.toList());
            responseData.setStatus(HttpStatus.OK);
            responseData.setData(list);
            responseData.setDesc("Success to load Customer Type");
            return responseData;
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("Fail to load customer type");
            return responseData;
        }

    }

}