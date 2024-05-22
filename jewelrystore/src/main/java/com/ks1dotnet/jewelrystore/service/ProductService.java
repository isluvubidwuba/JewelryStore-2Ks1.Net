package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

@Service
public class ProductService implements IProductService {
    @Autowired
    private IProductRepository iProductRepository;

    @Override
    public List<ProductDTO> findAll() {
        try {
            List<ProductDTO> listDTO = new ArrayList<>();
            for (Product Product : iProductRepository.findAll()) {
                listDTO.add(Product.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Product category find all error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProductDTO findById(String id) {
        try {
            Product gsp = iProductRepository.findById(id).orElse(null);
            if (gsp == null)
                return null;
            return gsp.getDTO();
        } catch (Exception e) {
            System.out.println("Product category find by id error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProductDTO save(ProductDTO t) {
        try {
            iProductRepository.save(new Product(t));
            return t;
        } catch (Exception e) {
            System.out.println("Product category save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ProductDTO saveAndFlush(ProductDTO t) {
        try {
            iProductRepository.saveAndFlush(new Product(t));
            return t;
        } catch (Exception e) {
            System.out.println("Product category save error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ProductDTO> saveAll(Iterable<ProductDTO> t) {
        List<Product> list = new ArrayList<>();
        for (ProductDTO ProductDTO : t) {
            list.add(new Product(ProductDTO));
        }
        try {
            List<ProductDTO> listDTO = new ArrayList<>();
            for (Product Product : iProductRepository.saveAll(list)) {
                listDTO.add(Product.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Product category saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ProductDTO> saveAllAndFlush(Iterable<ProductDTO> t) {
        List<Product> list = new ArrayList<>();
        for (ProductDTO ProductDTO : t) {
            list.add(new Product(ProductDTO));
        }
        try {
            List<ProductDTO> listDTO = new ArrayList<>();
            for (Product Product : iProductRepository.saveAllAndFlush(list)) {
                listDTO.add(Product.getDTO());
            }
            return listDTO;
        } catch (Exception e) {
            System.out.println("Product category saveAll error: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean existsById(String id) {
        return iProductRepository.existsById(id);
    }

    @Override
    public boolean deleteById(String id) {
        try {
            iProductRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Product category delete by id error: " + e.getMessage());
            return false;
        }
    }
}
