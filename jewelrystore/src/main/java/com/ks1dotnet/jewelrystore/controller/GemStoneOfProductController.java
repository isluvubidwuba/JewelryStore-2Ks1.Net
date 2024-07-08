package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneOfProductService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneTypeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

@RestController
@RequestMapping("${apiURL}/gemStone")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class GemStoneOfProductController {
    @Autowired
    private IGemStoneOfProductService iGemStoneOfProductService;

    @Autowired
    private IGemStoneTypeService iGemStoneTypeService;

    @Autowired
    private IGemStoneCategoryService iGemStoneCategoryService;

    @Autowired
    private IProductService iProductService;

    @GetMapping("/product")
    public ResponseEntity<?> getByProductId(@RequestParam int id) {
        ResponseData response = iGemStoneOfProductService.getGemStonesByProductId(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity<?> addGemStoneToProduct(@RequestParam GemStoneOfProductDTO t) {
        ResponseData response = iGemStoneOfProductService.insert(t);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
