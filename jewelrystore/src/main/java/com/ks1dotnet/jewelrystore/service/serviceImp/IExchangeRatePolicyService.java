package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;

public interface IExchangeRatePolicyService {
    public List<ExchangeRatePolicyDTO> getFullExchange();

    public responseData getFullByID(String idExchangeRate);

    public responseData updateExchangeRatePolicy(String idExchange, String desc, float rate, boolean status);

    public responseData deleteExchangeRatePolicy(String idExchange);

    public responseData createExchangeRatePolicy(String idExchange, String desc, float rate, boolean status);

    public responseData getInforByID(String idExchangeRate);

}
