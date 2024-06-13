package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.CounterDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.entity.Counter;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.ICounterRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.ICounterSerivce;

@Service
public class CounterService implements ICounterSerivce {

    @Autowired
    private ICounterRepository iCounterRepository;

    @Autowired
    private IProductRepository iProductRepository;

    @Override
    public ResponseData insert(String name) {
        Counter counter = new Counter();
        counter.setName(name);
        counter.setStatus(true);
        iCounterRepository.save(counter);
        ResponseData responseData = new ResponseData();
        responseData.setData(counter.getDTO());
        responseData.setDesc("Counter inserted successfully");
        responseData.setStatus(HttpStatus.OK);
        return responseData;
    }

    @Override
    public ResponseData addProductsToCounter(int counterId, List<ProductDTO> products) {
        Counter counter = iCounterRepository.findById(counterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));

        List<Product> productList = new ArrayList<>();
        for (ProductDTO dto : products) {
            Optional<Product> productOpt = iProductRepository.findById(dto.getId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setCounter(counter);
                productList.add(product);
            }
        }

        iProductRepository.saveAll(productList);

        // Lấy tất cả sản phẩm thuộc counter này
        List<Product> allProductsInCounter = iProductRepository.findByCounterId(counterId);

        List<ProductDTO> updatedProductDTOs = allProductsInCounter.stream()
                .map(Product::getDTO)
                .collect(Collectors.toList());

        ResponseData responseData = new ResponseData();
        responseData.setData(updatedProductDTOs);
        responseData.setDesc("Products added to counter successfully");
        responseData.setStatus(HttpStatus.OK);
        return responseData;
    }

    @Override
    public Map<String, Object> listProductsByCounter(int counterId, int page) {
        Map<String, Object> response = new HashMap<>();
        int pageSize = 5;
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<Product> products = iProductRepository.findByCounterId(counterId);

        int start = page * pageSize;
        int end = Math.min((page + 1) * pageSize, products.size());
        List<Product> paginatedProducts = products.subList(start, end);

        for (Product product : paginatedProducts) {
            ProductDTO dto = product.getDTO();
            productDTOList.add(dto);
        }

        response.put("products", productDTOList);
        response.put("totalPages", (products.size() + pageSize - 1) / pageSize); // manually calculate total pages
        response.put("currentPage", page);
        return response;
    }

    // Method to load all products with counter id 1
    @Override
    public ResponseData getAllProductsInCounterOne() {
        List<Product> products = iProductRepository.findByCounterId(1);
        List<ProductDTO> productDTOs = products.stream().map(Product::getDTO).collect(Collectors.toList());
        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.OK);
        responseData.setData(productDTOs);
        return responseData;
    }


    @Override
    public ResponseData moveProductsToCounter(List<Integer> productIds, int newCounterId) {
        Counter newCounter = iCounterRepository.findById(newCounterId)
                .orElseThrow(() -> new IllegalArgumentException("Counter not found"));

        List<Product> updatedProducts = new ArrayList<>();
        for (Integer productId : productIds) {
            Optional<Product> productOpt = iProductRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setCounter(newCounter);
                updatedProducts.add(product);
            } else {
                throw new IllegalArgumentException("Product with id " + productId + " not found");
            }
        }

        iProductRepository.saveAll(updatedProducts);

        List<ProductDTO> updatedProductDTOs = new ArrayList<>();
        for (Product product : updatedProducts) {
            updatedProductDTOs.add(product.getDTO());
        }

        ResponseData responseData = new ResponseData();
        responseData.setData(updatedProductDTOs);
        responseData.setDesc("Products moved to new counter successfully");
        responseData.setStatus(HttpStatus.OK);
        return responseData;
    }


    @Override
    public ResponseData getProductDetails(int productId) {
        Optional<Product> productOpt = iProductRepository.findById(productId);
        if (productOpt.isPresent()) {
            ProductDTO productDTO = productOpt.get().getDTO();
            ResponseData responseData = new ResponseData();
            responseData.setData(productDTO);
            responseData.setDesc("Product details retrieved successfully");
            responseData.setStatus(HttpStatus.OK);
            return responseData;
        } else {
            ResponseData responseData = new ResponseData();
            responseData.setDesc("Product not found");
            responseData.setStatus(HttpStatus.NOT_FOUND);
            return responseData;
        }
    }

    @Override
    public ResponseData getAllProducts() {
        List<Product> products = iProductRepository.findAll();
        List<ProductDTO> productDTOs = products.stream().map(Product::getDTO).collect(Collectors.toList());

        ResponseData responseData = new ResponseData();
        responseData.setData(productDTOs);
        responseData.setDesc("All products retrieved successfully");
        responseData.setStatus(HttpStatus.OK);
        return responseData;
    }

    @Override
    public ResponseData getAllCountersActive() {
        List<Counter> counters = iCounterRepository.findAllActiveCounters();
        List<CounterDTO> counterDTOs = counters.stream().map(Counter::getDTO).collect(Collectors.toList());

        ResponseData responseData = new ResponseData();
        responseData.setData(counterDTOs);
        responseData.setDesc("All counters retrieved successfully");
        responseData.setStatus(HttpStatus.OK);
        return responseData;
    }

    @Override
    public ResponseData deleteCounter(int counterId) {
        ResponseData responseData = new ResponseData();
        Optional<Counter> counterOptional = iCounterRepository.findById(counterId);
        if (counterOptional.isPresent()) {
            Counter counter = counterOptional.get();

            List<Product> products = iProductRepository.findByCounterId(counterId);
            Optional<Counter> defaultCounterOptional = iCounterRepository.findById(1);
            if (defaultCounterOptional.isPresent()) {
                Counter defaultCounter = defaultCounterOptional.get();
                for (Product product : products) {
                    product.setCounter(defaultCounter); // Set counter to the default counter with id 1
                }
                iProductRepository.saveAll(products);

                counter.setStatus(false); // Set status to 0 (inactive)
                iCounterRepository.save(counter);

                responseData.setStatus(HttpStatus.OK);
                responseData.setDesc("Counter status set to inactive successfully");
            } else {
                // Handle the case where the default counter does not exist
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setDesc("Cannot find warehouse counter");
            }
        } else {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("Counter not found");
        }
        return responseData;
    }

    @Override
    public ResponseData getInactiveCounters() {
        List<Counter> counters = iCounterRepository.findByStatus(false);
        List<CounterDTO> counterDTOs = counters.stream().map(Counter::getDTO).collect(Collectors.toList());

        ResponseData responseData = new ResponseData();
        responseData.setData(counterDTOs);
        responseData.setDesc("Inactive counters retrieved successfully");
        responseData.setStatus(HttpStatus.OK);
        return responseData;
    }

    @Override
    public ResponseData updateCounter(int id, String name, boolean status) {
        ResponseData responseData = new ResponseData();
        Optional<Counter> counterOptional = iCounterRepository.findById(id);
        if (counterOptional.isPresent()) {
            Counter counter = counterOptional.get();
            counter.setName(name);
            counter.setStatus(status);
            iCounterRepository.save(counter);
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Counter updated successfully");
        } else {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("Counter not found");
        }
        return responseData;
    }

}
