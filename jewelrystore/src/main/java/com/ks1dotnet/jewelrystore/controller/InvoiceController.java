package com.ks1dotnet.jewelrystore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.payload.request.ImportInvoiceRequestWrapper;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceDetailRequest;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceRequest;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.MailService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

@RestController
@RequestMapping("${apiURL}/invoice")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class InvoiceController {
    @Autowired
    private IInvoiceService invoiceService;
    @Autowired
    private IInvoiceRepository invoiceRepository;
    @Value("${invoiceID.Sell}")
    private int Sell;
    @Value("${invoiceID.Imports}")
    private int Imports;
    @Value("${invoiceID.BuyBack}")
    private int BuyBack;

    @Autowired
    private MailService mailService;

    @PostMapping("/create-detail")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceDetailRequest request) {
        try {
            InvoiceDetailDTO invoiceDetailDTO = invoiceService.createInvoiceDetail(
                    request.getBarcode(), request.getInvoiceTypeId(), request.getQuantity());

            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Invoice created successfully", invoiceDetailDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at createInvoice InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at createInvoice InvoiceController: " + e.getMessage(),
                    "Something wrong while create invoice detail!");
        }
    }

    @PostMapping("/create-invoice")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceRequest request) {
        try {
            HashMap<String, Integer> barcodeQuantity = new HashMap<>();
            for (Map.Entry<String, String> entry : request.getBarcodeQuantityMap().entrySet()) {
                barcodeQuantity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            }
            String idEmployee = JwtUtilsHelper.getAuthorizationByTokenType("at").getSubject();
            int isSuccessCreateInvoice = invoiceService.createInvoiceFromDetails(barcodeQuantity,
                    request.getInvoiceTypeId(), request.getUserId(), idEmployee,
                    request.getPayment(), request.getNote());

            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Invoice created successfully", isSuccessCreateInvoice);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at createInvoice InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at createInvoice InvoiceController: " + e.getMessage(),
                    "Something wrong while create invoice!");
        }
    }

    @PostMapping("/buyback")
    public ResponseEntity<ResponseData> createBuybackInvoice(@RequestBody InvoiceRequest request) {
        try {
            if (request.getInvoiceTypeId() != 3) {
                throw new ApplicationException("Error invoice type!", HttpStatus.BAD_REQUEST);
            }
            HashMap<Integer, Integer> barcodeQuantity = new HashMap<>();
            for (Map.Entry<String, String> entry : request.getBarcodeQuantityMap().entrySet()) {
                barcodeQuantity.put(Integer.parseInt(entry.getKey()),
                        Integer.parseInt(entry.getValue()));
            }
            String idEmployee = JwtUtilsHelper.getAuthorizationByTokenType("at").getSubject();
            int invoiceId = invoiceService.createBuybackInvoice(barcodeQuantity,
                    request.getInvoiceTypeId(), request.getUserId(), idEmployee,
                    request.getPayment(), request.getNote());
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Buyback invoice created successfully", invoiceId);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at createBuybackInvoice InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at createBuybackInvoice InvoiceController: " + e.getMessage(),
                    "Something wrong while create buy back invoice!");
        }
    }

    @PostMapping("/create-import")
    public ResponseEntity<ResponseData> createImportInvoice(
            @RequestBody ImportInvoiceRequestWrapper importInvoiceRequestWrapper) {
        try {
            InvoiceRequest request = importInvoiceRequestWrapper.getRequest();
            Map<String, Double> barcodePriceMap = importInvoiceRequestWrapper.getBarcodePriceMap();
            int invoiceId = invoiceService.createImportInvoice(request, barcodePriceMap);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Import invoice created successfully", invoiceId);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at createImportInvoice InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at createImportInvoice InvoiceController: " + e.getMessage(),
                    "Something wrong while create import invocie!");
        }
    }

    @PostMapping("/view-invoice")
    public ResponseEntity<ResponseData> viewInvoice(@RequestParam int invoice) {
        try {
            Invoice Invoice = invoiceRepository.findById(invoice).orElseThrow(
                    () -> new ApplicationException("Not found invoice with id: " + invoice,
                            HttpStatus.NOT_FOUND));

            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.OK, "Get invoice successfull", Invoice.getDTO()),
                    HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at viewInvoice InvoiceController: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at viewInvoice InvoiceController: " + e.getMessage(),
                    "Something wrong while view invoice!");
        }
    }

    @PostMapping("/view-invoice-resale")
    public ResponseEntity<ResponseData> viewInvoiceResale(@RequestParam int invoice) {
        try {
            Invoice Invoice = invoiceRepository.findById(invoice).orElseThrow(
                    () -> new ApplicationException("Not found invoice with id: " + invoice,
                            HttpStatus.NOT_FOUND));
            if (Invoice.getInvoiceType().getId() != Sell) {
                return new ResponseEntity<>(new ResponseData(HttpStatus.NOT_FOUND,
                        "This invoice cannot buy back", null), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.OK, "Get invoice successfull", Invoice.getDTO()),
                    HttpStatus.OK);
        } catch (ApplicationException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<ResponseData> cancelInvoice(@RequestParam int invoiceId,
            @RequestParam String note) {
        try {
            invoiceService.cancelInvoice(invoiceId, note);
            ResponseData responseData = new ResponseData(HttpStatus.OK, "Invoice canceled successfully", null);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at cancelInvoice InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at cancelInvoice InvoiceController: " + e.getMessage(),
                    "Something wrong while cancel invocie!");
        }
    }

    // @GetMapping("/history")
    // public ResponseEntity<ResponseData> getAllInvoices() {
    // try {
    // List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
    // ResponseData responseData = new ResponseData(HttpStatus.OK,
    // "Retrieved all invoices successfully", invoices);
    // return new ResponseEntity<>(responseData, HttpStatus.OK);
    // } catch (ApplicationException e) {
    // throw new ApplicationException(
    // "Error at getAllInvoices InvoiceController: " + e.getMessage(),
    // e.getErrorString(), e.getStatus());
    // } catch (Exception e) {
    // throw new ApplicationException(
    // "Error at getAllInvoices InvoiceController: " + e.getMessage(),
    // "Something wrong while get all invoice!");
    // }
    // }

    // @GetMapping("/history/employee")
    // public ResponseEntity<ResponseData> getInvoicesByEmployeeId(@RequestParam
    // String employeeId)
    // {
    // try {
    // List<InvoiceDTO> invoices =
    // invoiceService.getInvoicesByEmployeeId(employeeId);
    // ResponseData responseData = new ResponseData(HttpStatus.OK,
    // "Retrieved invoices by employee ID successfully", invoices);
    // return new ResponseEntity<>(responseData, HttpStatus.OK);
    // } catch (ApplicationException e) {
    // throw new ApplicationException(
    // "Error at getInvoicesByEmployeeId InvoiceController: " + e.getMessage(),
    // e.getErrorString(), e.getStatus());
    // } catch (Exception e) {
    // throw new ApplicationException(
    // "Error at getInvoicesByEmployeeId InvoiceController: " + e.getMessage(),
    // "Something wrong while get invoice by employee id : " + employeeId);
    // }
    // }

    // @GetMapping("/history/date-range")
    // public ResponseEntity<ResponseData> getInvoicesByDateRange(
    // @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
    // @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
    // try {
    // List<InvoiceDTO> invoices = invoiceService.getInvoicesByDateRange(startDate,
    // endDate);
    // ResponseData responseData = new ResponseData(HttpStatus.OK,
    // "Retrieved invoices by date range successfully", invoices);
    // return new ResponseEntity<>(responseData, HttpStatus.OK);
    // } catch (ApplicationException e) {
    // throw new ApplicationException(
    // "Error at getInvoicesByDateRange InvoiceController: " + e.getMessage(),
    // e.getErrorString(), e.getStatus());
    // } catch (Exception e) {
    // throw new ApplicationException(
    // "Error at getInvoicesByDateRange InvoiceController: " + e.getMessage(),
    // "Something wrong while get invoice by date range from " + startDate + " to "
    // + endDate + " !");
    // }
    // }

    // @GetMapping("/history/user")
    // public ResponseEntity<ResponseData> getInvoicesByUserId(@RequestParam int
    // userId) {
    // try {
    // List<InvoiceDTO> invoices = invoiceService.getInvoicesByUserId(userId);
    // ResponseData responseData = new ResponseData(HttpStatus.OK,
    // "Retrieved invoices by user ID successfully", invoices);
    // return new ResponseEntity<>(responseData, HttpStatus.OK);
    // } catch (ApplicationException e) {
    // throw new ApplicationException(
    // "Error at getInvoicesByUserId InvoiceController: " + e.getMessage(),
    // e.getErrorString(), e.getStatus());
    // } catch (Exception e) {
    // throw new ApplicationException(
    // "Error at getInvoicesByUserId InvoiceController: " + e.getMessage(),
    // "Something wrong while get invocie by user id!");
    // }
    // }

    @GetMapping("/revenue/counter")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseData> calculateRevenueByCounter(@RequestParam String period,
            @RequestParam int year, @RequestParam(required = false) Integer quarterOrMonth) {
        try {
            List<RevenueDTO<CounterDTO>> revenueByCounter = invoiceService.calculateRevenueByCounter(period, year,
                    quarterOrMonth);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved revenue by counter successfully", revenueByCounter);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByCounter InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByCounter InvoiceController: " + e.getMessage(),
                    "Something wrong while calculate revenue by counter!");
        }
    }

    @GetMapping("/revenue/employee")
    public ResponseEntity<ResponseData> calculateRevenueByEmployee(@RequestParam String period,
            @RequestParam int year, @RequestParam(required = false) Integer month) {
        try {
            List<RevenueDTO<EmployeeDTO>> revenueByEmployee = invoiceService.calculateRevenueByEmployee(period, year,
                    month);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved revenue by employee successfully", revenueByEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByEmployee InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByEmployee InvoiceController: " + e.getMessage(),
                    "Something wrong calculate revenue by employee!");
        }
    }

    @GetMapping("/revenue/employeeId")
    public ResponseEntity<ResponseData> calculateRevenueByEmployeeID(@RequestParam String period,
            @RequestParam int year, @RequestParam(required = false) Integer month,
            @RequestParam String employeeId) {
        try {
            EmployeeRevenueDTO revenueDTO = invoiceService.calculateRevenueByEmployeeID(period, year, month,
                    employeeId);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved revenue by employee successfully", revenueDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByEmployeeID InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByEmployeeID InvoiceController: " + e.getMessage(),
                    "Something wrong while calculate revenue by employee id!");
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> getInvoices(@RequestParam int page, @RequestParam int size) {

        try {
            Page<InvoiceDTO> invoices = invoiceService.getInvoices(page, size);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Get invoice page: " + page + " Size: " + size + " successfully", invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getInvoices InvoiceController: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getInvoices InvoiceController: " + e.getMessage(),
                    "Something wrong while get invoices!");
        }
    }

    //
    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> getInvoicesByEmployeeId(@RequestParam String employeeId,
            @RequestParam int page, @RequestParam int size) {

        try {
            Page<InvoiceDTO> invoices = invoiceService.getInvoicesByEmployeeId(employeeId, page, size);
            ResponseData responseData = new ResponseData(
                    HttpStatus.OK, "Get top invoices for employee ID: " + employeeId
                            + " Page: " + page + " Size: " + size + " successfully",
                    invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getInvoicesByEmployeeId InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getInvoicesByEmployeeId InvoiceController: " + e.getMessage(),
                    "Something wrong get invoices by employee!");
        }
    }

    // lấy invoice của employyee nhất định
    @GetMapping("/employee2")
    public ResponseEntity<?> getInvoicesByEmployeeId2(@RequestParam String employeeId,
            @RequestParam int page, @RequestParam int size) {

        try {
            Page<InvoiceDTO> invoices = invoiceService.getInvoicesByEmployeeId2(employeeId, page, size);
            ResponseData responseData = new ResponseData(
                    HttpStatus.OK, "Get top invoices for employee ID: " + employeeId
                            + " Page: " + page + " Size: " + size + " successfully",
                    invoices);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getInvoicesByEmployeeId2 InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getInvoicesByEmployeeId2 InvoiceController: " + e.getMessage(),
                    "Something wrong get invoices by employee!");
        }
    }

    @GetMapping("/revenue/top5employees")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseData> getTop5EmployeesByRevenue(@RequestParam String period,
            @RequestParam int year, @RequestParam(required = false) Integer month) {
        try {
            List<RevenueDTO<EmployeeDTO>> top5Employees = invoiceService.getTop5EmployeesByRevenue(period, year, month);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved top 5 employees by revenue successfully", top5Employees);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getTop5EmployeesByRevenue InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getTop5EmployeesByRevenue InvoiceController: " + e.getMessage(),
                    "Something wrong get top 5 employees by revenue!");
        }
    }

    @GetMapping("/revenue/top5products")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseData> getTop5ProductsByRevenue(@RequestParam String period,
            @RequestParam int year, @RequestParam(required = false) Integer month) {
        try {
            List<RevenueDTO<ProductDTO>> top5Products = invoiceService.getTop5ProductsByRevenue(period, year, month);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved top 5 products by revenue successfully", top5Products);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getTop5ProductsByRevenue InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getTop5ProductsByRevenue InvoiceController: " + e.getMessage(),
                    "Something wrong get top 5 product by revenue!");
        }
    }

    @GetMapping("/revenue/store")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseData> calculateRevenueByStore(@RequestParam String period,
            @RequestParam int year, @RequestParam(required = false) Integer month) {
        try {
            double revenue = invoiceService.calculateStoreRevenue(period, year, month);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved store revenue successfully", revenue);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByStore InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at calculateRevenueByStore InvoiceController: " + e.getMessage(),
                    "Something wrong while calcualate revenue by store!");
        }
    }

    @GetMapping("/revenue/invoice-count")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseData> getRevenueAndInvoiceCount(@RequestParam String period) {
        try {
            Map<String, Object> result = invoiceService.calculateRevenueAndInvoiceCount(period);
            ResponseData responseData = new ResponseData(HttpStatus.OK,
                    "Retrieved revenue and invoice count successfully", result);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getRevenueAndInvoiceCount InvoiceController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getRevenueAndInvoiceCount InvoiceController: " + e.getMessage(),
                    "Something wrong while get revenue and invoice!");
        }
    }

    @PostMapping("/sendInvoice")
    public ResponseEntity<?> sendInvoice(@RequestParam String email, @RequestParam String userName,
            @RequestParam int invoiceID) {

        try {
            ResponseData response = mailService.sendInvoiceEmail(email, userName, invoiceID);
            return new ResponseEntity<>(response, response.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    "Something wrong while sending invoice to customer " + userName + "!");
        }
    }
}
