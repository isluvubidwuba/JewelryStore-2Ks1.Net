package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IExchangeRatePolicyService {
        ResponseData getAllExchangeRatePolicies();

        ResponseData getExchangeRatePolicyById(String id);

        ResponseData createExchangeRatePolicy(String id, String descriptionPolicy, Float rate, boolean status,
                        Integer invoiceTypeId);

        ResponseData updateExchangeRatePolicy(String id, String descriptionPolicy, Float rate, boolean status,
                        Integer invoiceTypeId);

        ResponseData deactivateExchangeRatePolicy(String id);

        ResponseData getAllExchangeRatePoliciesByInvoiceTypeId(int invoiceTypeId);
}
