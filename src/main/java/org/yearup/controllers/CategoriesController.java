package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// REST controller annotation
@RestController
// Base URL: http://localhost:8080/categories
@RequestMapping("categories")
// Allow cross-origin requests
@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;

    // Autowired constructor to inject DAOs
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;

    }

    @GetMapping
    public List<Category> getAll() {
        try {
            return categoryDao.getAllCategories();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error listing categories.");
        }
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id )
    {
        Category ct = null;
        try
        {
            ct = categoryDao.getById(id);
        }
        catch(Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(ct == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ct;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("/categories/{categoryId}/products")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        List<Product> products = productDao.listByCategoryId(categoryId);

        if (products == null || products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for category ID: " + categoryId);
        }
        return products;
    }
    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category) {
        try {
            return categoryDao.create(category);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error adding new category.", ex);
        }
    }
    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        try {
            categoryDao.update(id, category);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating category with ID: " + id, ex);
        }
    }

    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    // add annotation to ensure that only an ADMIN can call this function
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT) //204 code  returned after successful delete
    public void deleteCategory(@PathVariable int id) {
        try {
            Category category = categoryDao.getById(id);
            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id);
            }
            categoryDao.delete(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting category with ID: " + id, ex);
        }
    }
}