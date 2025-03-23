package com.tms.controller;

import com.tms.model.Product;
import com.tms.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Integer id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product";
        }
        model.addAttribute("message", "Product not found");
        return "innerError";
    }

    @GetMapping("/allproducts")
    public String getAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        System.out.println("Fetched products: " + products);
        if (products == null) {
            products = new ArrayList<>();
        }
        model.addAttribute("products", products);
        return "listProducts";
    }

    @GetMapping("/create")
    public String getCreateProductPage(Model model) {
        model.addAttribute("product", new Product());
        return "createProduct";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product, Model model) {
        boolean isCreated = productService.createProduct(product);
        if (isCreated) {
            System.out.println("Created product: " + product);
            model.addAttribute("product", product);
            return "product";
        }
        model.addAttribute("message", "Failed to create product");
        return "innerError";
    }

    @GetMapping("/update/{id}")
    public String getUpdateProductPage(@PathVariable Integer id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "editProduct";
        }
        model.addAttribute("errorMessage", "Product not found with this ID");
        return "innerError";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product, Model model) {
        product.setId(id);
        try {
            boolean updated = productService.updateProduct(product);
            if (updated) {
                model.addAttribute("message", "Product updated successfully");
                return "success";
            } else {
                model.addAttribute("message", "Failed to update product");
                return "innerError";
            }
        } catch (Exception ex) {
            model.addAttribute("message", "Update error: " + ex.getMessage());
            return "innerError";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, Model model) {
        boolean isDeleted = productService.deleteProduct(id);
        if (isDeleted) {
            model.addAttribute("message", "Product deleted successfully");
            return "success";
        }
        model.addAttribute("message", "Failed to delete product");
        return "innerError";
    }
}
