package com.production.microservices.microservicea.controller;

import com.production.microservices.microservicea.entity.Product;
import com.production.microservices.microservicea.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class
})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllProducts_ShouldReturnProductList() throws Exception {
        // Given
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Test Product 1");
        product1.setPrice(BigDecimal.valueOf(99.99));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Test Product 2");
        product2.setPrice(BigDecimal.valueOf(149.99));

        when(productService.getAllActiveProducts()).thenReturn(Arrays.asList(product1, product2));

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product 1"))
                .andExpect(jsonPath("$[0].price").value(99.99))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Test Product 2"))
                .andExpect(jsonPath("$[1].price").value(149.99));
    }

    @Test
    @WithMockUser
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(99.99));

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    @WithMockUser
    void getProductById_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        Product inputProduct = new Product();
        inputProduct.setName("New Product");
        inputProduct.setPrice(BigDecimal.valueOf(199.99));

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("New Product");
        savedProduct.setPrice(BigDecimal.valueOf(199.99));

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    @WithMockUser
    void updateProduct_WhenProductExists_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        Product inputProduct = new Product();
        inputProduct.setName("Updated Product");
        inputProduct.setPrice(BigDecimal.valueOf(299.99));

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(BigDecimal.valueOf(299.99));

        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(Optional.of(updatedProduct));

        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(299.99));
    }

    @Test
    @WithMockUser
    void updateProduct_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        Product inputProduct = new Product();
        inputProduct.setName("Updated Product");
        inputProduct.setPrice(BigDecimal.valueOf(299.99));

        when(productService.updateProduct(anyLong(), any(Product.class))).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/products/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteProduct_WhenProductExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(productService.deleteProduct(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/products/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteProduct_WhenProductNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(productService.deleteProduct(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/products/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}