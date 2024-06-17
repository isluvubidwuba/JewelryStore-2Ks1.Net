package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;

public interface IInvoiceService {
        public InvoiceDetailDTO createInvoiceDetail(String barcode, Integer invoiceType, Integer quantity);

        public int createInvoiceFromDetails(HashMap<String, Integer> barcodeQuantity, Integer invoiceTypeId,
                        Integer userId,
                        String employeeId, String payment, String note);

        public void saveInvoice(Invoice invoice, List<InvoiceDetailDTO> invoiceDetails);

        public double applyGemstonePromotions(Map<Integer, Double> gemstoneValues,
                        List<Promotion> promotionForGemstone);

        public InvoiceDetailDTO calculateInvoiceDetail(Product product, int quantity, List<Promotion> promotions,
                        InvoiceType invoiceType);

        public int convertDoubleToInt(double input);
}
