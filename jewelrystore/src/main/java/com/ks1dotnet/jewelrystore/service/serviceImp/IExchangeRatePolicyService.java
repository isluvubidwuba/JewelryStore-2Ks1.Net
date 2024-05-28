package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IExchangeRatePolicyService {
    public List<ExchangeRatePolicyDTO> getFullExchange();

    public List<InvoiceTypeDTO> getFullByID(String idExchangeRate);

    public ResponseData updateExchangeRatePolicy(String idExchange, String desc, float rate, boolean status);

    public ResponseData deleteExchangeRatePolicy(String idExchange);

    public ResponseData createExchangeRatePolicy(String idExchange, String desc, float rate, boolean status);

    public ResponseData getInforByID(String idExchangeRate);

    public ResponseData applySelectOptions(String idExchangeRate, List<Integer> selectedOptions);

    public List<ExchangeRatePolicyDTO> searchExchangeRate(String keyword);

}
