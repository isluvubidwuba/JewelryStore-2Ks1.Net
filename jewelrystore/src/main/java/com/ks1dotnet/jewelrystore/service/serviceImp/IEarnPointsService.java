package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IEarnPointsService {
    public ResponseData getAllEarnPoints();

    public void updateCustomerTypeBasedOnPoints(Integer customerId);

    public ResponseData addPoints(Integer customerId, Integer points);
}
