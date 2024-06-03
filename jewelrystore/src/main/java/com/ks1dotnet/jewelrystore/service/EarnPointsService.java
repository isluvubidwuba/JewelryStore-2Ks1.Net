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
import com.ks1dotnet.jewelrystore.repository.IUserInfoRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEarnPointsService;

@Service
public class EarnPointsService implements IEarnPointsService {

    @Autowired
    public IEarnPointsRepository iEarnPointsRepository;

    @Autowired
    public IUserInfoRepository iUserInfoRepository;

    @Autowired
    public ICustomerTypeRepository iCustomerTypeRepository;

    @Override
    public ResponseData getAllEarnPoints() {
        ResponseData responseData = new ResponseData();
        try {
            List<EarnPointsDTO> earnPointsDTOList = iEarnPointsRepository.findAll().stream()
                    .map(earnPoints -> earnPoints.getDTO(new EarnPointsDTO())) // Adjusted to use the updated DTO method
                    .collect(Collectors.toList());
            responseData.setStatus(HttpStatus.OK);
            responseData.setData(earnPointsDTOList);
            responseData.setDesc("Successfull load customer rank");
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("Not Found Customer");
        }
        return responseData;
    }

    // Hàm này update thứ hạng của customer
    @Override
    public void updateCustomerTypeBasedOnPoints(Integer customerId) {
        // Fetch the customer's points using the custom query
        EarnPoints earnPoints = iEarnPointsRepository.findByCustomerId(customerId).orElse(null);

        if (earnPoints != null) {
            Integer totalPoints = earnPoints.getPoint();

            // Find the appropriate customer type based on points
            List<CustomerType> customerTypes = iCustomerTypeRepository.findAll();
            CustomerType newCustomerType = customerTypes.stream()
                    .filter(ct -> totalPoints >= ct.getPointCondition())
                    .max((ct1, ct2) -> Integer.compare(ct1.getPointCondition(), ct2.getPointCondition()))
                    .orElse(null);

            if (newCustomerType != null && !newCustomerType.equals(earnPoints.getCustomerType())) {
                // Update the customer's type
                earnPoints.setCustomerType(newCustomerType);
                iEarnPointsRepository.save(earnPoints);
            }
        }
    }

    @Override
    public ResponseData addPoints(Integer customerId, Integer points) {
        Optional<EarnPoints> optionalEarnPoints = iEarnPointsRepository.findByCustomerId(customerId);
        EarnPoints earnPoints;
        if (optionalEarnPoints.isPresent()) {
            earnPoints = optionalEarnPoints.get();
            earnPoints.setPoint(earnPoints.getPoint() + points);
        } else {
            UserInfo userInfo = iUserInfoRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            earnPoints = new EarnPoints();
            earnPoints.setUserInfo(userInfo);
            earnPoints.setPoint(points);
            // Set default customer type if needed, otherwise null
        }

        // Determine the new customer type based on the updated points
        updateCustomerTypeBasedOnPoints(customerId);

        EarnPoints savedEarnPoints = iEarnPointsRepository.save(earnPoints);
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

        return new ResponseData(HttpStatus.OK, "Points added successfully", earnPointsDTO);
    }

}
