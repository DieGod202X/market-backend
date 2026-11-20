package com.tecnm.merida.market_backend.web.controller;

import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.service.PurchaseService;
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
@RequestMapping("/purchases")
@Tag(name = "Purchase", description = "Manage purchases and master-detail transactions")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("")
    @Operation(summary = "Get all purchases",
            description = "Return a list of all registered purchases with their items")
    @ApiResponse(responseCode = "200",
            description = "Successful retrieval of purchases")
    public ResponseEntity<List<Purchase>> getAll() {
        return new ResponseEntity<>(purchaseService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase by ID",
            description = "Return a purchase by its ID if it exists")
    @ApiResponse(responseCode = "200",
            description = "Purchase found")
    @ApiResponse(responseCode = "404",
            description = "Purchase not found")
    public ResponseEntity<Purchase> getPurchase(
            @Parameter(description = "ID of the purchase to be retrieved",
                    example = "1",
                    required = true)
            @PathVariable("id") int purchaseId) {
        return purchaseService.getPurchase(purchaseId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/client/{idClient}")
    @Operation(summary = "Get purchases by client ID",
            description = "Return all purchases made by a specific client")
    @ApiResponse(responseCode = "200",
            description = "Purchase(s) found for the client")
    @ApiResponse(responseCode = "404",
            description = "No purchases found for the client")
    public ResponseEntity<List<Purchase>> getByClient(
            @Parameter(description = "Client ID",
                    example = "4546221",
                    required = true)
            @PathVariable("idClient") String clientId) {
        return purchaseService.getByCliente(clientId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("")
    @Operation(summary = "Create a new purchase (Master-Detail)",
            description = "Save a purchase header along with all its items in a single cascade transaction")
    @ApiResponse(responseCode = "201",
            description = "Purchase created successfully")
    public ResponseEntity<Purchase> save(@RequestBody Purchase purchase) {
        return new ResponseEntity<>(purchaseService.save(purchase), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing purchase",
            description = "Update a purchase by its ID if it exists")
    @ApiResponse(responseCode = "200",
            description = "Purchase updated successfully")
    @ApiResponse(responseCode = "404",
            description = "Purchase not found")
    public ResponseEntity<Purchase> update(
            @Parameter(description = "ID of the purchase to be updated",
                    example = "1",
                    required = true)
            @PathVariable("id") int purchaseId,
            @RequestBody Purchase purchase) {
        return purchaseService.update(purchaseId, purchase)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a purchase by ID",
            description = "Delete a purchase and its items by its ID if it exists")
    @ApiResponse(responseCode = "200",
            description = "Purchase deleted successfully")
    @ApiResponse(responseCode = "404",
            description = "Purchase not found")
    public ResponseEntity<Void> delete(@PathVariable("id") int purchaseId) {
        if (purchaseService.delete(purchaseId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
