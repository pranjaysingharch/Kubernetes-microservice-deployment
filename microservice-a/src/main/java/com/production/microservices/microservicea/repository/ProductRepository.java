package com.production.microservices.microservicea.repository;

import com.production.microservices.microservicea.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find all active products
     */
    List<Product> findByActiveTrue();
    
    /**
     * Find products by name containing given string (case insensitive)
     */
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
    
    /**
     * Find products within price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    List<Product> findProductsInPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    /**
     * Find products with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity <= :threshold AND p.active = true")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
    
    /**
     * Count active products
     */
    long countByActiveTrue();
}