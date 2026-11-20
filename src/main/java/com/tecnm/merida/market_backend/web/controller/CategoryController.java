package com.tecnm.merida.market_backend.web.controller;

import com.tecnm.merida.market_backend.domain.Category;
import com.tecnm.merida.market_backend.domain.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Category", description = "Manage your categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    @Operation(summary = "Get all categories",
            description = "Return a list of all available categories")
    @ApiResponse(responseCode = "200",
            description = "Successful retrieval of categories")
    public ResponseEntity<List<Category>> getAll() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID",
            description = "Return a category by its ID if it exists")
    @ApiResponse(responseCode = "200",
            description = "Category found")
    @ApiResponse(responseCode = "404",
            description = "Category not found")
    public ResponseEntity<Category> getCategory(
            @Parameter(description = "ID of the category to be retrieved",
                    example = "1",
                    required = true)
            @PathVariable("id") int categoryId) {
        return categoryService.getCategory(categoryId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    @Operation(summary = "Create a new category",
            description = "Save a new category in the catalog")
    @ApiResponse(responseCode = "201",
            description = "Category created successfully")
    public ResponseEntity<Category> save(@RequestBody Category category) {
        return new ResponseEntity<>(categoryService.save(category), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category",
            description = "Update a category by its ID if it exists")
    @ApiResponse(responseCode = "200",
            description = "Category updated successfully")
    @ApiResponse(responseCode = "404",
            description = "Category not found")
    public ResponseEntity<Category> update(
            @Parameter(description = "ID of the category to be updated",
                    example = "1",
                    required = true)
            @PathVariable("id") int categoryId,
            @RequestBody Category category) {
        return categoryService.update(categoryId, category)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category by ID",
            description = "Delete a category by its ID if it exists")
    @ApiResponse(responseCode = "200",
            description = "Category deleted successfully")
    @ApiResponse(responseCode = "404",
            description = "Category not found")
    public ResponseEntity<Void> delete(@PathVariable("id") int categoryId) {
        if (categoryService.delete(categoryId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
