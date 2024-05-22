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
import com.ks1dotnet.jewelrystore.payload.responseData;
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


    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            GemStoneOfProductDTO gs = iGemStoneOfProductService.findById(id);
            if (gs == null)
                return new ResponseEntity<>(
                        new responseData(404, "Gem stone of product not found", null),
                        HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(
                    new responseData(201, "Get gem stone of product successfully", gs),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400,
                    "Get gem stone of product failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {

        try {
            List<GemStoneOfProductDTO> listGs = iGemStoneOfProductService.findAll();
            if (listGs == null)
                return new ResponseEntity<>(
                        new responseData(404, "List gem stone of product not found", null),
                        HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(
                    new responseData(201, "Get all gem stone of product successfully", listGs),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new responseData(400, null,
                            "Get all gem stone of product failed: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody int id, 
                                    @RequestBody String color,
                                    @RequestBody String clarity, 
                                    @RequestBody float carat, 
                                    @RequestBody double price,
                                    @RequestBody int id_gemStoneType, 
                                    @RequestBody int id_gemStone_category,
                                    @RequestBody String id_product) {
                try {
                    GemStoneOfProductDTO gs = iGemStoneOfProductService.findById(id);
                    GemStoneTypeDTO gsT = iGemStoneTypeService.findById(id_gemStoneType);
                    GemStoneCategoryDTO gscT = iGemStoneCategoryService.findById(id_gemStone_category);
                    ProductDTO product = iProductService.findById(id_product);
                    if (gs == null) {
                        return new ResponseEntity<>(new responseData(404, "Gem stone of product not found", null),HttpStatus.NOT_FOUND);
                    }
                    gs.setCarat(carat);
                    gs.setClarity(clarity);
                    gs.setColor(color);
                    gs.setGemstoneCategory(gscT);
                    gs.setGemstoneType(gsT);
                    gs.setPrice(price);
                    gs.setProduct(product);
                    iGemStoneOfProductService.saveAndFlush(gs);
                    return new ResponseEntity<>(new responseData(201, "Update gem stone of
                    product successfully", null),
                    HttpStatus.OK);
                    } catch (Exception e) {
                    return new ResponseEntity<>(
                    new responseData(400, "Error update gem stone of product: " + e.getMessage(),
                    null),
                    HttpStatus.BAD_REQUEST);
                    }

    }

    // @PostMapping("/insert")
    // public ResponseEntity<?> insert(
    // @RequestParam String color,
    // @RequestParam String clarity,
    // @RequestParam float carat,
    // @RequestParam double price,
    // @RequestParam int id_gemStoneType,
    // @RequestParam int id_gemstoneCategory,
    // @RequestParam String id_product) {

    // try {
    // GemStoneType gsT = iGemStoneTypeService.findById(id_gemStoneType);
    // GemStoneCategory gscT =
    // iGemStoneCategoryService.findById(id_gemstoneCategory);
    // Product product = iProductService.findById(id_product);
    // GemStoneOfProduct gs = new GemStoneOfProduct();
    // gs.setCarat(carat);
    // gs.setClarity(clarity);
    // gs.setColor(color);
    // gs.setGemstoneCategory(gscT);
    // gs.setGemstoneType(gsT);
    // gs.setPrice(price);
    // gs.setProduct(product);
    // iGemStoneOfProductService.saveAndFlush(gs);
    // return new ResponseEntity<>(new responseData(201, "Update gem stone of
    // product successfully", null),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(
    // new responseData(400, "Error update gem stone of product: " + e.getMessage(),
    // null),
    // HttpStatus.BAD_REQUEST);
    // }
    // }

}
