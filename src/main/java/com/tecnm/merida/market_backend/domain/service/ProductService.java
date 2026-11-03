package com.tecnm.merida.market_backend.domain.service;


import com.tecnm.merida.market_backend.domain.Product;
import com.tecnm.merida.market_backend.domain.repository.ProductoRepository;
import com.tecnm.merida.market_backend.domain.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private ProductoRepository productRepository;

    public List<Product> getAll(){
        return productRepository.getAll();
    }

    public Optional<Product> getProduct(int productId){
        return productRepository.getProduct(productId);
    }

    public Optional<List<Product>> getByCategory(int categoryId){
        return productRepository.getByCategory(categoryId);
    }

    public Product save(Product product){
        return productRepository.save(product);
    }

    public boolean delete(int productId) {
        return getProduct(productId).map(product -> {
            productRepository.delete(product);
            return true;
        }).orElse(false);
    }
}