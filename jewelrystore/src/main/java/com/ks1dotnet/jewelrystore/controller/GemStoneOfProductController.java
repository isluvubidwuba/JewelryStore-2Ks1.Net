package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;
import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneCategory;
import com.ks1dotnet.jewelrystore.entity.GemStoneOfProduct;
import com.ks1dotnet.jewelrystore.entity.GemStoneType;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneOfProductService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneTypeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/gemStone")
@CrossOrigin("*")
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
}
