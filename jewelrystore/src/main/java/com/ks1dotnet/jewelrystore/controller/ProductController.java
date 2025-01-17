package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.FirebaseStorageService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

@RestController
@RequestMapping("${apiURL}/product")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class ProductController {
    @Value("${fileUpload.productPath}")
    private String filePath;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private IProductService iProductService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        ResponseData response = iProductService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll(@RequestParam int page, @RequestParam int size) {
        ResponseData response = iProductService.Page(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /*
     * @PostMapping("searchV1") public ResponseEntity<?> searchProduct(@RequestParam String
     * search, @RequestParam String id_material,
     * 
     * @RequestParam String id_product_category, @RequestParam String id_counter) { if
     * (search.isEmpty() && id_material.isEmpty() && id_product_category.isEmpty() &&
     * id_counter.isEmpty()) { return new ResponseEntity<>(new ResponseData(HttpStatus.OK,
     * "Find product successfully", null), HttpStatus.OK); } ResponseData response =
     * iProductService.searchProduct(search, id_material, id_product_category, id_counter); return
     * new ResponseEntity<>(response, response.getStatus()); }
     */
    @PostMapping("search")
    public ResponseEntity<?> searchProductV2(@RequestParam String search,
            @RequestParam String id_material, @RequestParam String id_product_category,
            @RequestParam String id_counter, @RequestParam int page, int size) {
        if (search.isEmpty() && id_material.isEmpty() && id_product_category.isEmpty()
                && id_counter.isEmpty()) {
            return new ResponseEntity<>(
                    new ResponseData(HttpStatus.OK, "Find product successfully", null),
                    HttpStatus.OK);
        }
        search = search.isEmpty() ? null : search.trim();
        id_material = id_material.isEmpty() ? null : id_material;
        id_product_category = id_product_category.isEmpty() ? null : id_product_category;
        id_counter = id_counter.isEmpty() ? null : id_counter;
        ResponseData response = iProductService.searchProductV2(search, id_material,
                id_product_category, id_counter, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("update")
    public ResponseEntity<?> update(@RequestBody ProductDTO t) {
        ResponseData response = iProductService.update(t);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("update/status")
    public ResponseEntity<?> update(@RequestParam int id, @RequestParam int status) {
        ResponseData response = iProductService.updateStatus(id, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("update/statusAll")
    public ResponseEntity<?> update(@RequestBody Map<Integer, Integer> map) {
        ResponseData response = iProductService.updateStatus(map);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductDTO t) {
        System.out.println(t);
        ResponseData response = iProductService.insert(t);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        ResponseData response = firebaseStorageService.uploadImage(file, filePath);
        return new ResponseEntity<>(response, response.getStatus());

    }

    @PostMapping("/image")
    public ResponseEntity<?> deleteImage(@RequestParam String img) {
        ResponseData response = firebaseStorageService.deleteImage(img, filePath);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<?> getMethodName(@PathVariable String barcode) {
        ResponseData responseData = iProductService.getProductByBarCode(barcode);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
