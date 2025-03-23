package com.tms.service;

import com.tms.model.Product;
import com.tms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        System.out.println("Fetched from repository: " + (products != null ? products.size() : "null"));
        return products == null ? new ArrayList<>() : products;
    }

    public Optional<Product> getProductById(Integer id) {
        return productRepository.getProductById(id);
    }
    public boolean createProduct(Product product) {
        return productRepository.createProduct(product);
    }
    public boolean updateProduct(Product product) {
        System.out.println("Received product for update in service: " + product);
        if (product.getId() == null) {
            System.out.println("Error: Product ID is null");
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        boolean result = false;
        try {
            result = productRepository.updateProduct(product);
            System.out.println("Update status from repository: " + result);
        } catch (Exception e) {
            System.out.println("Error while updating product in repository: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteProduct(Integer id) {
        return productRepository.deleteProduct(id);
    }
}
