package com.tms.controller;

import com.tms.model.Product;
import com.tms.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/product")
@Tag(name = "Product Controller", description = "Управление товарами")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Создание продукта", description = "Добавляет новый продукт в систему")
    @ApiResponse(responseCode = "201", description = "Продукт успешно создан")
    @ApiResponse(responseCode = "409", description = "Конфликт: продукт не создан")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Optional<Product> createdProduct = productService.createProduct(product);
        if (createdProduct.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(createdProduct.get(), HttpStatus.CREATED);
    }

    @Operation(summary = "Получение продукта", description = "Возвращает продукт по ID")
    @ApiResponse(responseCode = "200", description = "Продукт найден")
    @ApiResponse(responseCode = "404", description = "Продукт не найден")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") @Parameter(description = "Product id") Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product.get(), HttpStatus.OK);
    }

    @Operation(summary = "Обновление продукта", description = "Обновляет данные существующего продукта")
    @ApiResponse(responseCode = "200", description = "Продукт успешно обновлён")
    @ApiResponse(responseCode = "409", description = "Конфликт при обновлении продукта")
    @PutMapping
    public ResponseEntity<Product> updateProduct(@RequestBody Product product) {
        Optional<Product> productUpdated = productService.updateProduct(product);
        if (productUpdated.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Удаление продукта", description = "Удаляет продукт по ID")
    @ApiResponse(responseCode = "204", description = "Продукт успешно удалён")
    @ApiResponse(responseCode = "409", description = "Ошибка при удалении продукта")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") @Parameter(description = "Product id") Long productId) {
        boolean isDeleted = productService.deleteProduct(productId);
        if (!isDeleted) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
