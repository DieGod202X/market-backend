package com.tecnm.merida.market_backend.domain.service;

import com.tecnm.merida.market_backend.domain.Purchase;
import com.tecnm.merida.market_backend.domain.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public List<Purchase> getAll() {
        return purchaseRepository.getAll();
    }

    public Optional<Purchase> getPurchase(int purchaseId) {
        return purchaseRepository.getPurchase(purchaseId);
    }

    public Optional<List<Purchase>> getByCliente(String clientId) {
        return purchaseRepository.getByClient(clientId);
    }

    @Transactional
    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public Optional<Purchase> update(int purchaseId, Purchase purchase) {
        return getPurchase(purchaseId).map(existing -> {
            purchase.setPurchaseId(purchaseId);
            return purchaseRepository.save(purchase);
        });
    }

    @Transactional
    public boolean delete(int purchaseId) {
        return getPurchase(purchaseId).map(purchase -> {
            purchaseRepository.delete(purchaseId);
            return true;
        }).orElse(false);
    }
}
