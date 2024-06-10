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
import com.ks1dotnet.jewelrystore.entity.Role;
import com.ks1dotnet.jewelrystore.entity.UserInfo;
import com.ks1dotnet.jewelrystore.repository.ICustomerTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IEarnPointsRepository;
import com.ks1dotnet.jewelrystore.repository.IUserInfoRepository;
import com.ks1dotnet.jewelrystore.service.CustomerTypeService;

public class CustomerTypeServiceTest {

    @Mock
    private ICustomerTypeRepository customerTypeRepository;

    @Mock
    private IEarnPointsRepository earnPointsRepository;

    @Mock
    private IUserInfoRepository userInfoRepository;

    @InjectMocks
    private CustomerTypeService customerTypeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDeleteCustomerTypeAndUpdateRanks() {
        // Sắp xếp dữ liệu
        int customerTypeId = 1;

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

        Role customerRole = new Role();
        customerRole.setId(4);
        customerRole.setName("Customer");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(1);
        userInfo.setRole(customerRole);

        EarnPoints earnPoints = new EarnPoints();
        earnPoints.setId(1);
        earnPoints.setPoint(1200);
        earnPoints.setUserInfo(userInfo);
        earnPoints.setCustomerType(silver);

        when(customerTypeRepository.findById(customerTypeId)).thenReturn(Optional.of(silver));
        when(earnPointsRepository.findAll()).thenReturn(Arrays.asList(earnPoints));
        when(customerTypeRepository.findAll()).thenReturn(Arrays.asList(gold, platinum));

        // Hành động
        customerTypeService.deleteCustomerTypeAndUpdateRanks(customerTypeId);

        // Kiểm tra kết quả
        verify(customerTypeRepository, times(1)).deleteById(customerTypeId);
        verify(earnPointsRepository, times(1)).save(earnPoints);
        assertEquals(gold, earnPoints.getCustomerType());
    }
}
