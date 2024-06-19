package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.ks1dotnet.jewelrystore.dto.CounterDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeRevenueDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.RevenueDTO;
import com.ks1dotnet.jewelrystore.entity.Counter;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceRequest;

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

        public void cancelInvoice(int invoiceId, String note);

        public List<InvoiceDTO> getAllInvoices();

        public List<InvoiceDTO> getInvoicesByEmployeeId(String employeeId);

        public List<InvoiceDTO> getInvoicesByDateRange(Date startDate, Date endDate);

        public List<InvoiceDTO> getInvoicesByUserId(int userId);

        List<RevenueDTO<CounterDTO>> calculateRevenueByCounter(String period, int year, Integer quarterOrMonth);

        EmployeeRevenueDTO calculateRevenueByEmployeeID(String period, int year, Integer month, String employeeId);

        public List<RevenueDTO<EmployeeDTO>> calculateRevenueByEmployee(String period, int year, Integer month);

        public Page<InvoiceDTO> getInvoices(int page, int size);

        List<RevenueDTO<EmployeeDTO>> getTop5EmployeesByRevenue(String period, int year, Integer month);

        public List<RevenueDTO<ProductDTO>> getTop5ProductsByRevenue(String period, int year, Integer month);

        public int createImportInvoice(InvoiceRequest request, Map<String, Double> barcodePriceMap);

        public Page<InvoiceDTO> getInvoicesByEmployeeId(String employeeId, int page, int size);

}
