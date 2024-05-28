package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.ICounterRepository;
import com.ks1dotnet.jewelrystore.repository.IMaterialOfProductRepository;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

@Service
public class ProductService implements IProductService {
    @Autowired
    IProductRepository iProductRepository;
    @Autowired
    IMaterialOfProductRepository iMaterialOfProductRepository;
    @Autowired
    IProductCategoryRepository iProductCategoryRepository;
    @Autowired
    ICounterRepository iCounterRepository;

    // @Override
    // public ResponseData Page(Page<ProductDTO> t) {
    // try {
    // if (t != null) {
    // List<Product> product = t.getContent()
    // .stream()
    // .map(entry -> {
    // return iProductRepository.findById(entry.getId())
    // .orElseThrow(() -> new BadRequestException(
    // "Product with ID " + entry.getId() + " does not exist"));

    // })
    // .collect(Collectors.toList());
    // Page<Product> p = new PageImpl<>(product, t.previousOrFirstPageable(),
    // product.size());
    // p = iProductRepository.findAll(p.nextPageable());
    // return new ResponseData(HttpStatus.OK, "Find all product successfully", p);
    // }
    // Page<Product> p = iProductRepository.findAll(PageRequest.of(0, 5));
    // List<ProductDTO> productDTO = p.getContent()
    // .stream()
    // .map(Product::getDTO)
    // .collect(Collectors.toList());
    // Page<ProductDTO> pDTO = new PageImpl<>(productDTO, PageRequest.of(0, 5),
    // productDTO.size());
    // return new ResponseData(HttpStatus.OK, "Find all product successfully",
    // pDTO);
    // } catch (Exception e) {
    // throw new RunTimeExceptionV1("Find all product error");
    // }
    // }
    @Override
    public ResponseData Page(int page, int size) {
        try {
            Page<Product> p = iProductRepository.findAll(PageRequest.of(page, size));
            return new ResponseData(HttpStatus.OK, "Find all products successfully", convertToDtoPage(p));

        } catch (RuntimeException e) {
            throw new RunTimeExceptionV1("Find all product error", e.getMessage());
        }
    }

    private Page<ProductDTO> convertToDtoPage(Page<Product> productPage) {
        List<ProductDTO> dtoList = productPage.getContent()
                .stream()
                .map(Product::getDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(String id) {
        Product gsc = iProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return new ResponseData(HttpStatus.OK, "Find product successfully", gsc.getDTO());
    }

    @Override
    public ResponseData insert(ProductDTO t) {
        try {
            if (t == null)
                throw new BadRequestException(": Can not create product that because the object are null");
            if (t.getId() != null && iProductRepository.existsById(t.getId()))
                throw new BadRequestException(": Can not create product that already exsist failed!");
            if (t.getName() == null)
                throw new BadRequestException(": Can not create product that don't have name!");
            if (t.getFee() == null)
                throw new BadRequestException(": Can not create product that don't fee!");
            if (t.getMaterialOfProductDTO() == null
                    || !iMaterialOfProductRepository.existsById(t.getMaterialOfProductDTO().getId()))
                throw new BadRequestException(": Can not create product that don't material!");
            if (t.getProductCategoryDTO() == null
                    || !iProductCategoryRepository.existsById(t.getProductCategoryDTO().getId()))
                throw new BadRequestException(": Can not create product that don't category!");
            if (t.getCounterDTO() == null || !iCounterRepository.existsById(t.getCounterDTO().getId()))
                throw new BadRequestException(": Can not create product that haven't assign to any counter");
            if (findByAllFieldsExceptId(t).size() > 0)
                throw new BadRequestException(": Can not create product that already exsist!");
            t.setMaterialOfProductDTO(
                    iMaterialOfProductRepository.findById(t.getMaterialOfProductDTO().getId()).get().getDTO());
            t.setProductCategoryDTO(
                    iProductCategoryRepository.findById(t.getProductCategoryDTO().getId()).get().getDTO());
            t.setCounterDTO(iCounterRepository.findById(t.getCounterDTO().getId()).get().getDTO());
            return new ResponseData(HttpStatus.CREATED, "Create product successfully",
                    iProductRepository.save(new Product(t)).getDTO());
        } catch (Exception e) {
            throw new BadRequestException("Create product failed", e.getMessage());
        }
    }

    @Override
    public ResponseData update(ProductDTO t) {
        try {
            if (t == null)
                throw new BadRequestException(": Can not update product that because the object are null");
            if (t.getId() == null ||
                    !iProductRepository.existsById(t.getId()))
                throw new BadRequestException(": Can not update product that not exsist failed!");

            ProductDTO product = iProductRepository.findById(t.getId()).get().getDTO();
            if (t.getName() != null)
                product.setName(t.getName());
            if (t.getFee() != null)
                product.setFee(t.getFee());
            if (t.getMaterialOfProductDTO() != null
                    && iMaterialOfProductRepository.existsById(t.getMaterialOfProductDTO().getId()))
                product.setMaterialOfProductDTO(t.getMaterialOfProductDTO());
            if (t.getProductCategoryDTO() != null
                    && iProductCategoryRepository.existsById(t.getProductCategoryDTO().getId()))
                product.setProductCategoryDTO(t.getProductCategoryDTO());
            if (t.getCounterDTO() != null && iCounterRepository.existsById(t.getCounterDTO().getId()))
                product.setCounterDTO(t.getCounterDTO());
            product.setStatus(t.isStatus());
            return new ResponseData(HttpStatus.OK, "Update product successfully",
                    iProductRepository.save(new Product(product)).getDTO());
        } catch (Exception e) {
            throw new BadRequestException("Update product failed", e.getMessage());
        }
    }

    @Override
    public ResponseData updateStatus(String id, int status) {
        try {
            if (id == null || !iProductRepository.existsById(id))
                throw new BadRequestException(": Can not update product that not exsist failed!");
            Product product = iProductRepository.findById(id).get();
            product.setStatus(status == 1);
            return new ResponseData(HttpStatus.OK, "Update product successfully",
                    iProductRepository.save(product).getDTO());
        } catch (Exception e) {
            throw new BadRequestException("Update product failed", e.getMessage());
        }
    };

    @Override
    public ResponseData updateStatus(Map<String, Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new BadRequestException("Cannot update products because the input map is empty");
        }

        try {
            List<Product> productsToUpdate = list.entrySet().stream()
                    .map(entry -> {
                        String productId = entry.getKey();
                        Integer statusValue = entry.getValue();

                        Product product = iProductRepository.findById(productId)
                                .orElseThrow(() -> new BadRequestException(
                                        "Product with ID " + productId + " does not exist"));
                        product.setStatus(statusValue == 1);
                        return product;
                    })
                    .collect(Collectors.toList());

            iProductRepository.saveAll(productsToUpdate);

            List<ProductDTO> updatedProductDTOs = productsToUpdate.stream()
                    .map(Product::getDTO)
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "All products updated successfully!", updatedProductDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to update all products", e.getMessage());
        }
    }

    @Override
    public ResponseData existsById(String id) {
        boolean exists = iProductRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Product exists!" : "Product not found!",
                exists);
    }

    private List<Product> findByAllFieldsExceptId(ProductDTO t) {
        return iProductRepository.findByAllFieldsExceptId(t.getName(), t.getMaterialOfProductDTO().getId(),
                t.getProductCategoryDTO().getId(), t.getCounterDTO().getId(), t.getFee());
    }

}
