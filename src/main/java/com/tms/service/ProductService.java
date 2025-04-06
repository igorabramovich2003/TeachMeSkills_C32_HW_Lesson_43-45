package com.tms.service;

import com.tms.model.Product;
import com.tms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.getProductById(id);
    }

    public Optional<Product> updateProduct(Product product){
        Boolean result = productRepository.updateProduct(product);
        if(result){
            return getProductById(product.getId());
        }
        return Optional.empty();
    }

    public boolean deleteProduct(Long id) {
        return productRepository.deleteProduct(id);
    }


    public Optional<Product> createProduct(Product product){
        Optional<Long> productId = productRepository.createProduct(product);
        if(productId.isPresent()){
            return getProductById(productId.get());
        }
        return Optional.empty();
    }
}
