package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;
import com.ks1dotnet.jewelrystore.entity.GemStoneOfProduct;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IGemStoneOfProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneOfProductService;

@Service
public class GemStoneOfProductService implements IGemStoneOfProductService {
    @Autowired
    private IGemStoneOfProductRepository iGemStoneOfProductRepository;

    @Override
    public ResponseData Page(int page, int size) {
        try {
            Page<GemStoneOfProduct> p = iGemStoneOfProductRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<GemStoneOfProductDTO> convertToDtoPage(Page<GemStoneOfProduct> productPage) {
        List<GemStoneOfProductDTO> dtoList = productPage.getContent()
                .stream()
                .map(GemStoneOfProduct::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        GemStoneOfProduct gsc = iGemStoneOfProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("gem stone of product not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find gem stone of product successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(GemStoneOfProductDTO t) {
        try {
            if (t.getId() != 0 && iGemStoneOfProductRepository.existsById(t.getId()))
                throw new BadRequestException("Can not create gem stone of product that already exsist failed!");
            iGemStoneOfProductRepository.save(new GemStoneOfProduct(t));
            return new ResponseData(HttpStatus.CREATED, "Create gem stone of product successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create gem stone of product failed");
        }
    }

    @Override
    public ResponseData update(GemStoneOfProductDTO t) {
        try {
            if (t.getId() == 0 && !iGemStoneOfProductRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update gem stone of product that not exsist failed!");
            iGemStoneOfProductRepository.save(new GemStoneOfProduct(t));
            return new ResponseData(HttpStatus.OK, "Update gem stone of product successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Update gem stone of product failed");
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iGemStoneOfProductRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Gem stone of product exists!" : "Gem stone of product not found!",
                exists);
    }

    @Override
    public ResponseData getGemStonesByProductId(int id) {
        List<GemStoneOfProductDTO> listDto = iGemStoneOfProductRepository.findGemStonesByProductId(id).stream()
                .map(GemStoneOfProduct::getDTO).collect(Collectors.toList());
        return new ResponseData(HttpStatus.OK,
                "Gem stone of product id: " + id + " found!", listDto);
    }

}
