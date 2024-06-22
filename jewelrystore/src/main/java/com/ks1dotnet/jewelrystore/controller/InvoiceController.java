package com.ks1dotnet.jewelrystore.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.CounterDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeRevenueDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.RevenueDTO;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.payload.request.BuyBackInvoiceRequest;
import com.ks1dotnet.jewelrystore.payload.request.ImportInvoiceRequestWrapper;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceDetailRequest;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceRequest;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceService;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("*")
public class InvoiceController {
    @Autowired
    private IInvoiceService invoiceService;
    @Autowired
    private IInvoiceRepository invoiceRepository;

    @PostMapping("/create-detail")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceDetailRequest request) {
        try {
            InvoiceDetailDTO invoiceDetailDTO = invoiceService.createInvoiceDetail(
                    request.getBarcode(), request.getInvoiceTypeId(), request.getQuantity());

            ResponseData responseData = new ResponseData(HttpStatus.OK, "Invoice created successfully",
                    invoiceDetailDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-invoice")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceRequest request) {
        try {
            HashMap<String, Integer> barcodeQuantity = new HashMap<>();
            for (Map.Entry<String, String> entry : request.getBarcodeQuantityMap().entrySet()) {
                barcodeQuantity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            }

            int isSuccessCreateInvoice = invoiceService.createInvoiceFromDetails(barcodeQuantity,
                    request.getInvoiceTypeId(),
                    request.getUserId(), request.getEmployeeId(), request.getPayment(), request.getNote());

            ResponseData responseData = new ResponseData(HttpStatus.OK, "Invoice created successfully",
                    isSuccessCreateInvoice);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/buyback")
    public ResponseEntity<ResponseData> createBuybackInvoice(@RequestBody BuyBackInvoiceRequest request) {
        try {
            int invoiceId = invoiceService.createBuybackInvoice(request.getRequest(), request.getIdDetailQuantityMap());
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Buyback invoice created successfully",
                    invoiceId);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create-import")
    public ResponseEntity<ResponseData> createImportInvoice(
            @RequestBody ImportInvoiceRequestWrapper importInvoiceRequestWrapper) {
        try {
            InvoiceRequest request = importInvoiceRequestWrapper.getRequest();
            Map<String, Double> barcodePriceMap = importInvoiceRequestWrapper.getBarcodePriceMap();
            int invoiceId = invoiceService.createImportInvoice(request, barcodePriceMap);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Import invoice created successfully",
                    invoiceId);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/view-invoice")
    public ResponseEntity<ResponseData> viewInvoice(@RequestParam int invoice) {
        try {
            Invoice Invoice = invoiceRepository.findById(invoice)
                    .orElseThrow(() -> new BadRequestException("Not found invoice with id: " + invoice));

            return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "Get invoice successfull", Invoice.getDTO()),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<ResponseData> cancelInvoice(@RequestParam int invoiceId, @RequestParam String note) {
        try {
            invoiceService.cancelInvoice(invoiceId, note);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Invoice canceled successfully", null);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<ResponseData> getAllInvoices() {
        try {
            List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Retrieved all invoices successfully",
                    invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history/employee")
    public ResponseEntity<ResponseData> getInvoicesByEmployeeId(@RequestParam String employeeId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByEmployeeId(employeeId);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved invoices by employee ID successfully", invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history/date-range")
    public ResponseEntity<ResponseData> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Retrieved invoices by date range successfully",
                    invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history/user")
    public ResponseEntity<ResponseData> getInvoicesByUserId(@RequestParam int userId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getInvoicesByUserId(userId);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Retrieved invoices by user ID successfully",
                    invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/revenue/counter")
    public ResponseEntity<ResponseData> calculateRevenueByCounter(@RequestParam String period,
            @RequestParam int year,
            @RequestParam(required = false) Integer quarterOrMonth) {
        try {
            List<RevenueDTO<CounterDTO>> revenueByCounter = invoiceService.calculateRevenueByCounter(period, year,
                    quarterOrMonth);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Retrieved revenue by counter successfully",
                    revenueByCounter);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/revenue/employee")
    public ResponseEntity<ResponseData> calculateRevenueByEmployee(@RequestParam String period,
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {
        try {
            List<RevenueDTO<EmployeeDTO>> revenueByEmployee = invoiceService.calculateRevenueByEmployee(period, year,
                    month);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Retrieved revenue by employee successfully",
                    revenueByEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/revenue/employeeId")
    public ResponseEntity<ResponseData> calculateRevenueByEmployeeID(@RequestParam String period,
            @RequestParam int year,
            @RequestParam(required = false) Integer month, @RequestParam String employeeId) {
        try {
            EmployeeRevenueDTO revenueDTO = invoiceService.calculateRevenueByEmployeeID(period, year, month,
                    employeeId);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Retrieved revenue by employee successfully",
                    revenueDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getInvoices(@RequestParam int page, @RequestParam int size) {
        Page<InvoiceDTO> invoices = invoiceService.getInvoices(page, size);
        ResponseData responseData = new ResponseData(HttpStatus.OK,
                "Get invoice page: " + page + " Size: " + size + " successfully", invoices);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getInvoicesByEmployeeId(@RequestParam String employeeId, @RequestParam int page,
            @RequestParam int size) {
        Page<InvoiceDTO> invoices = invoiceService.getInvoicesByEmployeeId(employeeId, page, size);
        ResponseData responseData = new ResponseData(HttpStatus.OK,
                "Get top invoices for employee ID: " + employeeId + " Page: " + page + " Size: " + size
                        + " successfully",
                invoices);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/revenue/top5employees")
    public ResponseEntity<ResponseData> getTop5EmployeesByRevenue(@RequestParam String period,
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {
        try {
            List<RevenueDTO<EmployeeDTO>> top5Employees = invoiceService.getTop5EmployeesByRevenue(period, year, month);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved top 5 employees by revenue successfully", top5Employees);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/revenue/top5products")
    public ResponseEntity<ResponseData> getTop5ProductsByRevenue(@RequestParam String period,
            @RequestParam int year,
            @RequestParam(required = false) Integer month) {
        try {
            List<RevenueDTO<ProductDTO>> top5Products = invoiceService.getTop5ProductsByRevenue(period, year, month);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved top 5 products by revenue successfully", top5Products);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
