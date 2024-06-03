package com.ks1dotnet.jewelrystore.employeeRepositoryTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ks1dotnet.jewelrystore.entity.CustomerType;
import com.ks1dotnet.jewelrystore.entity.EarnPoints;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.repository.ICustomerTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IEarnPointsRepository;
import com.ks1dotnet.jewelrystore.service.EarnPointsService;

public class EarnPointsServiceTest {

    @Mock
    private IEarnPointsRepository earnPointsRepository;

    @Mock
    private ICustomerTypeRepository customerTypeRepository;

    @InjectMocks
    private EarnPointsService earnPointsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateCustomerTypeBasedOnPoints_Silver() {
        // Sắp xếp dữ liệu
        int customerId = 2;
        int points = 700;

        UserInfo userInfo = new UserInfo();
        userInfo.setId(customerId);

        EarnPoints earnPoints = new EarnPoints();
        earnPoints.setId(1);
        earnPoints.setPoint(points);
        earnPoints.setUserInfo(userInfo);

        CustomerType bronze = new CustomerType();
        bronze.setId(0);
        bronze.setType("Bronze");
        bronze.setPointCondition(0);

        CustomerType silver = new CustomerType();
        silver.setId(1);
        silver.setType("Silver");
        silver.setPointCondition(500);

        CustomerType gold = new CustomerType();
        gold.setId(2);
        gold.setType("Gold");
        gold.setPointCondition(1000);

        CustomerType platinum = new CustomerType();
        platinum.setId(3);
        platinum.setType("Platinum");
        platinum.setPointCondition(1500);

        earnPoints.setCustomerType(bronze);

        when(earnPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(earnPoints));
        when(customerTypeRepository.findAll()).thenReturn(Arrays.asList(bronze, silver, gold, platinum));

        // Hành động
        earnPointsService.updateCustomerTypeBasedOnPoints(customerId);

        // Kiểm tra kết quả
        assertEquals(silver, earnPoints.getCustomerType());
        verify(earnPointsRepository, times(1)).save(earnPoints);
    }

    @Test
    public void testUpdateCustomerTypeBasedOnPoints_Gold() {
        // Sắp xếp dữ liệu
        int customerId = 2;
        int points = 1100;

        UserInfo userInfo = new UserInfo();
        userInfo.setId(customerId);

        EarnPoints earnPoints = new EarnPoints();
        earnPoints.setId(1);
        earnPoints.setPoint(points);
        earnPoints.setUserInfo(userInfo);

        CustomerType bronze = new CustomerType();
        bronze.setId(0);
        bronze.setType("Bronze");
        bronze.setPointCondition(0);

        CustomerType silver = new CustomerType();
        silver.setId(1);
        silver.setType("Silver");
        silver.setPointCondition(500);

        CustomerType gold = new CustomerType();
        gold.setId(2);
        gold.setType("Gold");
        gold.setPointCondition(1000);

        CustomerType platinum = new CustomerType();
        platinum.setId(3);
        platinum.setType("Platinum");
        platinum.setPointCondition(1500);

        earnPoints.setCustomerType(silver);

        when(earnPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(earnPoints));
        when(customerTypeRepository.findAll()).thenReturn(Arrays.asList(bronze, silver, gold, platinum));

        // Hành động
        earnPointsService.updateCustomerTypeBasedOnPoints(customerId);

        // Kiểm tra kết quả
        assertEquals(gold, earnPoints.getCustomerType());
        verify(earnPointsRepository, times(1)).save(earnPoints);
    }

    @Test
    public void testUpdateCustomerTypeBasedOnPoints_Platinum() {
        // Sắp xếp dữ liệu
        int customerId = 2;
        int points = 1800;

        UserInfo userInfo = new UserInfo();
        userInfo.setId(customerId);

        EarnPoints earnPoints = new EarnPoints();
        earnPoints.setId(1);
        earnPoints.setPoint(points);
        earnPoints.setUserInfo(userInfo);

        CustomerType bronze = new CustomerType();
        bronze.setId(0);
        bronze.setType("Bronze");
        bronze.setPointCondition(0);

        CustomerType silver = new CustomerType();
        silver.setId(1);
        silver.setType("Silver");
        silver.setPointCondition(500);

        CustomerType gold = new CustomerType();
        gold.setId(2);
        gold.setType("Gold");
        gold.setPointCondition(1000);

        CustomerType platinum = new CustomerType();
        platinum.setId(3);
        platinum.setType("Platinum");
        platinum.setPointCondition(1500);

        earnPoints.setCustomerType(silver);

        when(earnPointsRepository.findByCustomerId(customerId)).thenReturn(Optional.of(earnPoints));
        when(customerTypeRepository.findAll()).thenReturn(Arrays.asList(bronze, silver, gold, platinum));

        // Hành động
        earnPointsService.updateCustomerTypeBasedOnPoints(customerId);

        // Kiểm tra kết quả
        assertEquals(platinum, earnPoints.getCustomerType());
        verify(earnPointsRepository, times(1)).save(earnPoints);
    }
}