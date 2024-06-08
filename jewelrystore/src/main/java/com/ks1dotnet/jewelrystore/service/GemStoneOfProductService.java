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

    private Page<GemStoneOfProductDTO> convertToDtoPage(List<GemStoneOfProduct> productPage) {
        List<GemStoneOfProductDTO> dtoList = productPage
                .stream()
                .map(GemStoneOfProduct::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, PageRequest.of(0, productPage.size()), productPage.size());
    }

    @Override
    public ResponseData findById(Integer id) {
        GemStoneOfProduct gsc = iGemStoneOfProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("gem stone of product not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find gem stone of product successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(GemStoneOfProductDTO t) {
        validateGemStoneOfProduct(t, "create");

        if (iGemStoneOfProductRepository.existsById(t.getId())) {
            throw new BadRequestException("Cannot create gemstone of product that already exists.");
        }

        try {
            iGemStoneOfProductRepository.save(new GemStoneOfProduct(t));
            return new ResponseData(HttpStatus.CREATED, "Create gemstone of product successfully", t);
        } catch (Exception e) {
            throw new BadRequestException("Create gemstone of product failed", e.getMessage());
        }
    }

    @Override
    public ResponseData update(GemStoneOfProductDTO t) {
        validateGemStoneOfProduct(t, "update");
        try {
            if (!iGemStoneOfProductRepository.existsById(t.getId()))
                throw new BadRequestException("Can not update gem stone of product that not exsist !");

            return new ResponseData(HttpStatus.OK, "Update gem stone of product successfully",
                    iGemStoneOfProductRepository.save(new GemStoneOfProduct(t)).getDTO());
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
        List<GemStoneOfProduct> list = iGemStoneOfProductRepository.findGemStonesByProductId(id);
        if (list.isEmpty())
            return new ResponseData(HttpStatus.NOT_FOUND,
                    "No gem stone of product id: " + id + " found!",
                    null);
        return new ResponseData(HttpStatus.OK,
                "Gem stone of product id: " + id + " found!",
                convertToDtoPage(list));
    }

    private void validateGemStoneOfProduct(GemStoneOfProductDTO t, String type) {
        if (t.getId() != 0 && type.compareTo("create") == 0) {
            throw new BadRequestException("Create gemstone of product failed");
        }
        if (t.getGemstoneType() == null || t.getGemstoneType().getId() == 0) {
            throw new BadRequestException("Cannot " + type + " gemstone of product without type.");
        }
        if (t.getGemstoneCategory() == null || t.getGemstoneCategory().getId() == 0) {
            throw new BadRequestException("Cannot " + type + " gemstone of product without category.");
        }
        if (t.getProduct() == null || t.getProduct().getId() == 0) {
            throw new BadRequestException("Cannot " + type + " gemstone of product without associated product.");
        }
        if (isNullOrEmpty(t.getColor())) {
            throw new BadRequestException("Cannot " + type + " gemstone of product without color.");
        }
        if (isNullOrEmpty(t.getClarity())) {
            throw new BadRequestException("Cannot " + type + " gemstone of product without clarity.");
        }
        if (t.getCarat() <= 0) {
            throw new BadRequestException(
                    "Cannot " + type + " gemstone of product with carat less than or equal to 0.");
        }
        if (t.getPrice() <= 0) {
            throw new BadRequestException(
                    "Cannot " + type + " gemstone of product with price less than or equal to 0.");
        }
        if (t.getQuantity() <= 0) {
            throw new BadRequestException(
                    "Cannot " + type + " gemstone of product with quantity less than or equal to 0.");
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
