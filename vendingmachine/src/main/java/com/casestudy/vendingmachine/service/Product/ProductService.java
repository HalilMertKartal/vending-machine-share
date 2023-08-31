package com.casestudy.vendingmachine.service.Product;

import com.casestudy.vendingmachine.model.Product;

import java.util.List;
import java.util.Map;

// Interface to add business logic of Products
public interface ProductService {
    Product addProduct(Product product);
    Product decreaseProductStocksByID(int productID);
    List<Product> getAllProducts();
    // Adds stock number given to stocks of an existing product
    Product addStocksToProduct(int id, Map<String, Object> productAddStockRequest);
    void resetAllStocksAndPrices();
    double changeProductPrice(int id, Map<String, Object> productChangeProductPriceRequest);
    Product findProductByID(int id);
    void initializeProducts();
}
