package com.ks1dotnet.jewelrystore.service;

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
        CustomerType customerType = iCustomerTypeRepository.findById(id).orElse(new CustomerType());
        customerType.setType(type);
        customerType.setPointCondition(pointCondition);
        iCustomerTypeRepository.save(customerType);
        List<EarnPointsDTO> updatedEarnPoints = updateAllEarnPoints();
        return new ResponseData(HttpStatus.OK, "Point condition updated and EarnPoints updated successfully",
                updatedEarnPoints);
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
        List<EarnPoints> earnPointsList = iEarnPointsRepository.findAll();
        List<CustomerType> customerTypes = iCustomerTypeRepository.findAll();

        List<EarnPoints> updatedEarnPointsList = earnPointsList.stream().map(earnPoints -> {
            UserInfo userInfo = earnPoints.getUserInfo();
            if (userInfo.getRole().getName().equals("CUSTOMER")) { // Đảm bảo tên role là "CUSTOMER"
                Integer totalPoints = earnPoints.getPoint();

                CustomerType newCustomerType = customerTypes.stream()
                        .filter(ct -> totalPoints >= ct.getPointCondition())
                        .max((ct1, ct2) -> Integer.compare(ct1.getPointCondition(), ct2.getPointCondition()))
                        .orElse(null);

                if (newCustomerType != null && !newCustomerType.equals(earnPoints.getCustomerType())) {
                    earnPoints.setCustomerType(newCustomerType);
                    EarnPoints savedEarnPoints = iEarnPointsRepository.save(earnPoints); // Lưu và lấy kết quả trả về
                    System.out.println(savedEarnPoints); // In ra thông tin để kiểm tra
                }
            }
            return earnPoints;
        }).collect(Collectors.toList());

        return updatedEarnPointsList.stream().map(earnPoints -> new EarnPointsDTO(
                earnPoints.getId(),
                earnPoints.getPoint(),
                new UserInfoDTO(
                        earnPoints.getUserInfo().getDTO().getId(),
                        earnPoints.getUserInfo().getDTO().getFullName(),
                        earnPoints.getUserInfo().getDTO().getPhoneNumber(),
                        earnPoints.getUserInfo().getDTO().getEmail(),
                        earnPoints.getUserInfo().getDTO().getAddress(),
                        new RoleDTO(
                                earnPoints.getUserInfo().getRole().getDTO().getId(),
                                earnPoints.getUserInfo().getRole().getDTO().getName()),
                        earnPoints.getUserInfo().getImage()),
                new CustomerTypeDTO(
                        earnPoints.getCustomerType().getDTO().getId(),
                        earnPoints.getCustomerType().getDTO().getType(),
                        earnPoints.getCustomerType().getDTO().getPointCondition())))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseData findAll() {
        ResponseData responseData = new ResponseData();
        try {
            List<CustomerType> list = iCustomerTypeRepository.findAll();
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
