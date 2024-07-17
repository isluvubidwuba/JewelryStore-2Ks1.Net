package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.entity.Inventory;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.ICounterRepository;
import com.ks1dotnet.jewelrystore.repository.IInventoryRepository;
import com.ks1dotnet.jewelrystore.repository.IMaterialRepository;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

import jakarta.transaction.Transactional;

@Service
public class ProductService implements IProductService {
    @Autowired
    IProductRepository iProductRepository;
    @Autowired
    IMaterialRepository iMaterialRepository;
    @Autowired
    IProductCategoryRepository iProductCategoryRepository;
    @Autowired
    ICounterRepository iCounterRepository;
    @Autowired
    public IInventoryRepository iInventoryRepository;

    @Value("${fileUpload.productPath}")
    private String filePath;
    @Value("${firebase.img-url}")
    private String url;

    @Override
    public ResponseData Page(int page, int size) {
        try {
            Page<Product> p = iProductRepository.findAll(PageRequest.of(page, size));

            return new ResponseData(HttpStatus.OK, "Find all products successfully",
                    convertToDtoPage(p));

        } catch (ApplicationException e) {
            throw new ApplicationException("Find all product error: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    private Page<ProductDTO> convertToDtoPage(Page<Product> productPage) {
        List<ProductDTO> dtoList = productPage.getContent().stream().map(product -> {
            ProductDTO dto = product.getDTO();
            dto.setImgPath(url.trim() + filePath.trim() + dto.getImgPath());
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    @Override
    public ResponseData findById(Integer id) {
        Product gsc = iProductRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Product not found with id: " + id,
                        HttpStatus.NOT_FOUND));
        return new ResponseData(HttpStatus.OK, "Find product successfully", gsc.getDTO());
    }

    @Override
    public ResponseData update(ProductDTO t) {
        try {
            if (t == null)
                throw new ApplicationException(
                        "Can not update product that because the object are null",
                        HttpStatus.FORBIDDEN);
            if (t.getId() == 0 || !iProductRepository.existsById(t.getId()))
                throw new ApplicationException("Can not update product that not exsist failed!",
                        HttpStatus.FORBIDDEN);

            ProductDTO product = iProductRepository.findById(t.getId()).get().getDTO();
            if (t.getName() != null && !t.getName().isEmpty())
                product.setName(t.getName());
            if (t.getFee() != 0)
                product.setFee(t.getFee());
            if (t.getWeight() != 0 && t.getWeight() != product.getWeight())
                product.setWeight(t.getWeight());
            if (t.getMaterialDTO() != null
                    && iMaterialRepository.existsById(t.getMaterialDTO().getId()))
                product.setMaterialDTO(t.getMaterialDTO());
            if (t.getProductCategoryDTO() != null
                    && iProductCategoryRepository.existsById(t.getProductCategoryDTO().getId()))
                product.setProductCategoryDTO(t.getProductCategoryDTO());
            if (t.getCounterDTO() != null
                    && iCounterRepository.existsById(t.getCounterDTO().getId()))
                product.setCounterDTO(t.getCounterDTO());
            product.setStatus(t.isStatus());
            if (t.getImgPath() != null && !t.getImgPath().isEmpty())
                product.setImgPath(t.getImgPath());
            product = iProductRepository.save(new Product(product)).getDTO();
            product.setImgPath(url.trim() + filePath.trim() + product.getImgPath());
            return new ResponseData(HttpStatus.OK, "Update product successfully", product);
        } catch (ApplicationException e) {
            throw new ApplicationException("Failed to update products: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    @Override
    public ResponseData updateStatus(int id, int status) {
        try {
            if (id == 0 || !iProductRepository.existsById(id))
                throw new ApplicationException("Can not update product that not exsist failed!",
                        HttpStatus.FORBIDDEN);
            Product product = iProductRepository.findById(id).get();
            product.setStatus(status == 1);
            return new ResponseData(HttpStatus.OK, "Update product successfully",
                    iProductRepository.save(product).getDTO());
        } catch (ApplicationException e) {
            throw new ApplicationException("Failed to update status products: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    };

    @Override
    public ResponseData updateStatus(Map<Integer, Integer> list) {
        if (list == null || list.isEmpty()) {
            throw new ApplicationException("Cannot update products because the input map is empty");
        }

        try {
            List<Product> productsToUpdate = list.entrySet().stream().map(entry -> {
                Integer productId = entry.getKey();
                Integer statusValue = entry.getValue();

                Product product = iProductRepository.findById(productId)
                        .orElseThrow(() -> new ApplicationException(
                                "Product with ID " + productId + " does not exist"));
                product.setStatus(statusValue == 1);
                return product;
            }).collect(Collectors.toList());

            iProductRepository.saveAll(productsToUpdate);

            List<ProductDTO> updatedProductDTOs =
                    productsToUpdate.stream().map(Product::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "All products updated successfully!",
                    updatedProductDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException("Failed to update all products: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    @Override
    public ResponseData existsById(Integer id) {
        boolean exists = iProductRepository.existsById(id);
        return new ResponseData(exists ? HttpStatus.OK : HttpStatus.NOT_FOUND,
                exists ? "Product exists!" : "Product not found!", exists);
    }

    @Override
    @Transactional
    public ResponseData insert(ProductDTO t) {
        try {
            if (t == null)
                throw new ApplicationException(
                        "Can not create product that because the object are null",
                        HttpStatus.FORBIDDEN);
            if (t.getId() != 0 && iProductRepository.existsById(t.getId()))
                throw new ApplicationException("Can not create product that already exsist failed!",
                        HttpStatus.FORBIDDEN);
            if (t.getName() == null)
                throw new ApplicationException(": Can not create product that don't have name!",
                        HttpStatus.FORBIDDEN);
            if (t.getFee() == 0)
                throw new ApplicationException(": Can not create product that don't fee!",
                        HttpStatus.FORBIDDEN);
            if (t.getWeight() == 0)
                throw new ApplicationException(": Can not create product that don't weight!",
                        HttpStatus.FORBIDDEN);
            if (t.getMaterialDTO() == null)
                throw new ApplicationException(": Can not create product that don't material!",
                        HttpStatus.FORBIDDEN);
            if (t.getProductCategoryDTO() == null
                    || !iProductCategoryRepository.existsById(t.getProductCategoryDTO().getId()))
                throw new ApplicationException(": Can not create product that don't category!",
                        HttpStatus.FORBIDDEN);
            if (t.getImgPath() == null || t.getImgPath().isEmpty())
                t.setImgPath("none");
            t.setMaterialDTO(
                    iMaterialRepository.findById(t.getMaterialDTO().getId()).get().getDTO());
            t.setProductCategoryDTO(iProductCategoryRepository
                    .findById(t.getProductCategoryDTO().getId()).get().getDTO());
            t.setCounterDTO(
                    iCounterRepository.findById(1)
                            .orElseThrow(() -> new ApplicationException(
                                    "Counter not found with id: " + 1, HttpStatus.FORBIDDEN))
                            .getDTO());
            String productCode = generateProductCode();
            String barcode = generateBarcode(productCode);

            t.setProductCode(productCode);
            t.setBarCode(barcode);
            Product savedProduct = iProductRepository.save(new Product(t));

            Inventory inventory = new Inventory();
            inventory.setProduct(savedProduct);
            inventory.setQuantity(0);
            inventory.setTotal_import(0);
            inventory.setTotal_sold(0);
            iInventoryRepository.save(inventory);

            return new ResponseData(HttpStatus.CREATED, "Create product successfully",
                    savedProduct.getDTO());
        } catch (ApplicationException e) {
            throw new ApplicationException("Create product failed: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    private String generateProductCode() {
        Integer maxId = iProductRepository.findMaxId();
        int nextId = maxId == null ? 0 + 1 : maxId + 1;
        return String.format("%05d", nextId);
    }

    private String generateBarcode(String productCode) {
        String companyCode = "171831";
        String ean12 = "893" + companyCode + productCode;
        String checkDigit = calculateCheckDigit(ean12);
        return ean12 + checkDigit;
    }

    private String calculateCheckDigit(String ean12) {
        int sumOdd = 0;
        int sumEven = 0;

        for (int i = 0; i < ean12.length(); i++) {
            int digit = Character.getNumericValue(ean12.charAt(i));
            if (i % 2 == 0) {
                sumOdd += digit;
            } else {
                sumEven += digit;
            }
        }

        int totalSum = sumOdd + (sumEven * 3);
        int checkDigit = (10 - (totalSum % 10)) % 10;
        return String.valueOf(checkDigit);
    }

    // @Transactional(readOnly = true)
    // @Override
    // public ResponseData searchProduct(String search, String id_material, String
    // id_product_category,
    // String id_counter) {
    // try {
    // List<ProductDTO> listDTO = iProductRepository
    // .dynamicSearchProduct(search, id_material, id_product_category,
    // id_counter).stream()
    // .map(Product::getDTO).collect(Collectors.toList());
    // return new ResponseData(HttpStatus.OK, "Found Product successfully",
    // listDTO);
    // } catch (Exception e) {
    // throw new RunTimeExceptionV1("Failed search product", e.getMessage());
    // }
    // }

    @Override
    public ResponseData searchProductV2(String search, String id_material,
            String id_product_category, String id_counter, int page, int size) {
        try {
            Page<Product> listDTO = iProductRepository.dynamicSearchProductV2(search, id_material,
                    id_product_category, id_counter, PageRequest.of(page, size));
            if (listDTO.isEmpty())
                return new ResponseData(HttpStatus.NOT_FOUND, "No product found", null);
            return new ResponseData(HttpStatus.OK, "Search product successfully",
                    convertToDtoPage(listDTO));
        } catch (ApplicationException e) {
            throw new ApplicationException("Failed search product: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        }
    }

    @Override
    public ResponseData getProductByBarCode(String barCode) {
        Product product = iProductRepository.findByBarCode(barCode);
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.OK);
        responseData.setData(product.getDTO());
        return responseData;
    }

}
