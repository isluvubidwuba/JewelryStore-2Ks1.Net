package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductCategoryService;

@Service
public class ProductCategoryService implements IProductCategoryService {
    @Autowired
    private IProductCategoryRepository iProductCategoryRepository;

    @Override
    public List<ProductCategoryDTO> findAll() {
        try {
            List<ProductCategoryDTO> listDTO = new ArrayList<>();
            for (ProductCategory ProductCategory : iProductCategoryRepository.findAll()) {
                listDTO.add(ProductCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Product category find all error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProductCategoryDTO findById(Integer id) {
        try {
            ProductCategory gsp = iProductCategoryRepository.findById(id).orElse(null);
            if (gsp == null)
                return null;
            return gsp.getDTO();
        } catch (Exception e) {
            System.out.println("Product category find by id error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProductCategoryDTO save(ProductCategoryDTO t) {
        try {
            iProductCategoryRepository.save(new ProductCategory(t));
            return t;
        } catch (Exception e) {
            System.out.println("Product category save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProductCategoryDTO saveAndFlush(ProductCategoryDTO t) {
        try {
            iProductCategoryRepository.saveAndFlush(new ProductCategory(t));
            return t;
        } catch (Exception e) {
            System.out.println("Product category save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ProductCategoryDTO> saveAll(Iterable<ProductCategoryDTO> t) {
        List<ProductCategory> list = new ArrayList<>();
        for (ProductCategoryDTO ProductCategoryDTO : t) {
            list.add(new ProductCategory(ProductCategoryDTO));
        }
        try {
            List<ProductCategoryDTO> listDTO = new ArrayList<>();
            for (ProductCategory ProductCategory : iProductCategoryRepository.saveAll(list)) {
                listDTO.add(ProductCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Product category saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ProductCategoryDTO> saveAllAndFlush(Iterable<ProductCategoryDTO> t) {
        List<ProductCategory> list = new ArrayList<>();
        for (ProductCategoryDTO ProductCategoryDTO : t) {
            list.add(new ProductCategory(ProductCategoryDTO));
        }
        try {
            List<ProductCategoryDTO> listDTO = new ArrayList<>();
            for (ProductCategory ProductCategory : iProductCategoryRepository.saveAllAndFlush(list)) {
                listDTO.add(ProductCategory.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Product category saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return iProductCategoryRepository.existsById(id);
    }

    @Override
    public boolean deleteById(Integer id) {
        try {
            iProductCategoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Product category delete by id error: " + e.getMessage());
            return false;
        }
    }
}
