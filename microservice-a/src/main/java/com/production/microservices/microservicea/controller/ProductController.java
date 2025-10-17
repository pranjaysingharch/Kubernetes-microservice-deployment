package com.production.microservices.microservicea.controller;

import com.production.microservices.microservicea.entity.Product;
import com.production.microservices.microservicea.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Get all active products
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("GET /api/v1/products - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                   page, size, sortBy, sortDir);
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : 
                       Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            List<Product> products = productService.getAllActiveProducts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("totalCount", productService.getTotalActiveProductsCount());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching products", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch products");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        logger.info("GET /api/v1/products/{}", id);
        
        return productService.getProductById(id)
                .map(product -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("product", product);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Product not found");
                    errorResponse.put("id", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }
    
    /**
     * Create a new product
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody Product product) {
        logger.info("POST /api/v1/products - Creating product: {}", product.getName());
        
        try {
            Product createdProduct = productService.createProduct(product);
            Map<String, Object> response = new HashMap<>();
            response.put("product", createdProduct);
            response.put("message", "Product created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating product", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create product");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Update an existing product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, 
                                                            @Valid @RequestBody Product productDetails) {
        logger.info("PUT /api/v1/products/{} - Updating product", id);
        
        return productService.updateProduct(id, productDetails)
                .map(updatedProduct -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("product", updatedProduct);
                    response.put("message", "Product updated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Product not found");
                    errorResponse.put("id", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                });
    }
    
    /**
     * Delete a product (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        logger.info("DELETE /api/v1/products/{}", id);
        
        boolean deleted = productService.deleteProduct(id);
        Map<String, Object> response = new HashMap<>();
        
        if (deleted) {
            response.put("message", "Product deleted successfully");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Product not found");
            response.put("id", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Search products by name
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/v1/products/search - name: {}, page: {}, size: {}", name, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productPage = productService.searchProductsByName(name, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", productPage.getContent());
            response.put("totalElements", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching products", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to search products");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get products in price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<Map<String, Object>> getProductsInPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        
        logger.info("GET /api/v1/products/price-range - min: {}, max: {}", minPrice, maxPrice);
        
        try {
            List<Product> products = productService.getProductsInPriceRange(minPrice, maxPrice);
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("count", products.size());
            response.put("priceRange", Map.of("min", minPrice, "max", maxPrice));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching products in price range", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch products in price range");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<Map<String, Object>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        
        logger.info("GET /api/v1/products/low-stock - threshold: {}", threshold);
        
        try {
            List<Product> products = productService.getLowStockProducts(threshold);
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("count", products.size());
            response.put("threshold", threshold);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching low stock products", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch low stock products");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}