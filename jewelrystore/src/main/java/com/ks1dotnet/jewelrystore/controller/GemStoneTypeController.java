package com.ks1dotnet.jewelrystore.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneType;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.repository.IGemStoneTypeRepository;

@RestController
@RequestMapping("/gemStone/type")
@CrossOrigin("*")
public class GemStoneTypeController {
    @Autowired
    private IGemStoneTypeRepository iGemStoneTypeRepository;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            GemStoneType gs = iGemStoneTypeRepository.findById(id).orElse(null);
            if (gs == null)
                return new ResponseEntity<>(new responseData(404, "Gem stone type not found", null),
                        HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(new responseData(201, "Get gem stone type successfully",
                    new GemStoneTypeDTO(id, gs.getName())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new responseData(400, "Get gem stone of product failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {

        try {
            List<GemStoneType> listGs = iGemStoneTypeRepository.findAll();
            List<GemStoneTypeDTO> listDTO = new ArrayList<>();
            for (GemStoneType gemStoneType : listGs) {
                listDTO.add(new GemStoneTypeDTO(gemStoneType.getId(), gemStoneType.getName()));
            }
            return new ResponseEntity<>(new responseData(201, "Get all gem stone type successfully",
                    listDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new responseData(400, null, "Error get all gem stone type : " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
