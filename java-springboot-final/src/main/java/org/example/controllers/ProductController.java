package org.example.controllers;

import org.example.daos.ProductDao;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for products.
 * This class is responsible for handling all HTTP requests related to products.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/products")
@PreAuthorize("isAuthenticated()")
public class ProductController {
    /**
     * The product data access object.
     */
    @Autowired
    private ProductDao productDao;

    /**
     * Gets all products.
     *
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAll() {
        return productDao.getAll();
    }

    /**
     * Gets a product by id.
     *
     * @param id The id of the product.
     * @return The product with the given id.
     */
    @GetMapping(path = "/{id}")
    public Product get(@PathVariable int id) {
        Product product = productDao.getById(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return The created product.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productDao.create(product);
    }

    /**
     * Updates a product.
     *
     * @param id The id of the product.
     * @param product The new product data.
     * @return The updated product.
     */
    @PutMapping(path = "/{id}")
    public Product update(@PathVariable int id, @RequestBody Product product) {
        if (productDao.getById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        product.setId(id);
        return productDao.update(product);
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     * @return The number of rows affected.
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        int rowsAffected = productDao.delete(id);
        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return rowsAffected;
    }
}
