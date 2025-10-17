package com.production.microservices.microservicea.service;

import com.production.microservices.microservicea.entity.Product;
import com.production.microservices.microservicea.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Get all active products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        logger.info("Fetching all active products");
        return productRepository.findByActiveTrue();
    }
    
    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        logger.info("Fetching product with id: {}", id);
        return productRepository.findById(id);
    }
    
    /**
     * Create a new product
     */
    public Product createProduct(Product product) {
        logger.info("Creating new product: {}", product.getName());
        return productRepository.save(product);
    }
    
    /**
     * Update an existing product
     */
    public Optional<Product> updateProduct(Long id, Product productDetails) {
        logger.info("Updating product with id: {}", id);
        
        return productRepository.findById(id)
            .map(product -> {
                product.setName(productDetails.getName());
                product.setDescription(productDetails.getDescription());
                product.setPrice(productDetails.getPrice());
                product.setQuantity(productDetails.getQuantity());
                product.setActive(productDetails.getActive());
                return productRepository.save(product);
            });
    }
    
    /**
     * Soft delete a product
     */
    public boolean deleteProduct(Long id) {
        logger.info("Soft deleting product with id: {}", id);
        
        return productRepository.findById(id)
            .map(product -> {
                product.setActive(false);
                productRepository.save(product);
                return true;
            })
            .orElse(false);
    }
    
    /**
     * Search products by name
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        logger.info("Searching products with name containing: {}", name);
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name, pageable);
    }
    
    /**
     * Get products in price range
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsInPriceRange(Double minPrice, Double maxPrice) {
        logger.info("Fetching products in price range: {} - {}", minPrice, maxPrice);
        return productRepository.findProductsInPriceRange(minPrice, maxPrice);
    }
    
    /**
     * Get low stock products
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(Integer threshold) {
        logger.info("Fetching products with stock below: {}", threshold);
        return productRepository.findLowStockProducts(threshold);
    }
    
    /**
     * Get total active products count
     */
    @Transactional(readOnly = true)
    public long getTotalActiveProductsCount() {
        return productRepository.countByActiveTrue();
    }
}