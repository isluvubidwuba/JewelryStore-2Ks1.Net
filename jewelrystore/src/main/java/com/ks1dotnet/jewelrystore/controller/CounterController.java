package com.ks1dotnet.jewelrystore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.ICounterSerivce;

@RestController
@RequestMapping("${apiURL}/counter")
@CrossOrigin("*")
public class CounterController {

    @Autowired
    private ICounterSerivce iCounterSerivce;

    // insert các quầy
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> insertCounter(@RequestParam String name) {
        ResponseData responseData = iCounterSerivce.insert(name);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // 2 phướng này xử lý add các product nằm trong quầy kho
    @GetMapping("/products/counter1")
    public ResponseEntity<?> getAllProductsInCounterOne() {
        ResponseData responseData = iCounterSerivce.getAllProductsInCounterOne();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/addproductsforcounter")
    public ResponseEntity<?> addProductsToCounter(@RequestParam int counterId,
            @RequestBody List<ProductDTO> products) {
        ResponseData responseData = iCounterSerivce.addProductsToCounter(counterId, products);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // load các list product theo counter lên để thể hiện counter nào chứa những
    // product nào
    @GetMapping("/listproductsbycounter")
    public ResponseEntity<Map<String, Object>> listProductsByCounter(@RequestParam int counterId,
            @RequestParam int page) {
        Map<String, Object> response = iCounterSerivce.listProductsByCounter(counterId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // trường hợp chuyển product sang quầy khác
    @PostMapping("/moveProductsToCounter")
    public ResponseEntity<ResponseData> moveProductsToCounter(@RequestBody List<Integer> productIds,
            @RequestParam int newCounterId) {
        ResponseData responseData = iCounterSerivce.moveProductsToCounter(productIds, newCounterId);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // xử lý khi user click vào sản phẩm
    // lấy thông tin chi tiết của product
    @GetMapping("/product/details")
    public ResponseEntity<ResponseData> getProductDetails(@RequestParam int productId) {
        ResponseData responseData = iCounterSerivce.getProductDetails(productId);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // thể hiện lên 1 list product dùng cho trường hợp chọn list product khác
    // counter
    // và chọn counter mong muốn sản phẩm chuyển đến
    @GetMapping("/products/all")
    public ResponseEntity<ResponseData> getAllProducts() {
        ResponseData responseData = iCounterSerivce.getAllProducts();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // load các counter lên để tạo ra các tab động
    @GetMapping("/allactivecounter")
    public ResponseEntity<ResponseData> getAllCounters() {
        ResponseData responseData = iCounterSerivce.getAllCountersActive();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{counterId}")
    public ResponseEntity<?> deleteCounter(@PathVariable int counterId) {
        ResponseData responseData = iCounterSerivce.deleteCounter(counterId);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/inactive")
    public ResponseEntity<ResponseData> getInactiveCounters() {
        ResponseData responseData = iCounterSerivce.getInactiveCounters();
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseData> updateCounter(@RequestParam int id,
            @RequestParam String name, @RequestParam boolean status) {
        ResponseData responseData = iCounterSerivce.updateCounter(id, name, status);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

}
