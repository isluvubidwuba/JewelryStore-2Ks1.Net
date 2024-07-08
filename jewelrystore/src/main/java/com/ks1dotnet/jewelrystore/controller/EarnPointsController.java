package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEarnPointsService;

@RestController
@RequestMapping("${apiURL}/earnpoints")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class EarnPointsController {
    @Autowired
    private IEarnPointsService iEarnPointsService;

    @GetMapping("/rank")
    public ResponseEntity<ResponseData> getCustomerRank() {
        ResponseData responseData = iEarnPointsService.getAllEarnPoints();
        return ResponseEntity.ok(responseData);
    }

}
