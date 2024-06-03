package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface ICustomerTypeService {
    public ResponseData deleteCustomerTypeAndUpdateRanks(Integer customerTypeId);

    public ResponseData addCustomerType(String type, Integer pointCondition);

    public ResponseData updatePointCondition(Integer id, String type, Integer pointCondition);

    public ResponseData findAll();

}
