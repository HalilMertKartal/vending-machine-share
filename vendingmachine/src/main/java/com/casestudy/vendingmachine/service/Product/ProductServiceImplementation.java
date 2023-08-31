package com.casestudy.vendingmachine.service.Product;

import com.casestudy.vendingmachine.model.Product;
import com.casestudy.vendingmachine.repository.ProductRepository;
import com.casestudy.vendingmachine.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImplementation implements ProductService{

    // Repository instance is needed for database operations
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // If there is no product with given id or no stocks left for the product, returns null
    // Else decreases the product stock and returns the instance of it
    @Override
    public Product decreaseProductStocksByID(int productID) {
        Product product = findProductByID(productID);
        if (product == null || product.getStock() <= 0) {
            return null;
        }
        product.setStock(product.getStock()-1);
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product addStocksToProduct(int id, Map<String, Object> productAddStockRequest) {

        Optional<Product> existingProduct = productRepository.findById(id);
        existingProduct.ifPresent(product -> productAddStockRequest.forEach(
                (k, v) -> {
                    if (k.equals("stock") && (int)v >= 0){
                        Field field = ReflectionUtils.findField(Product.class, k);
                        assert field != null;
                        field.setAccessible(true);
                        int val = product.getStock() + (int)v;
                        if (val > Constants.MAX_STOCKS) {
                            val = Constants.MAX_STOCKS;
                        }
                        ReflectionUtils.setField(field, existingProduct.get(), val);
                    }
                }
            )
        );
        return existingProduct.map(product -> productRepository.save(product)).orElse(null);

    }

    @Override
    public void resetAllStocksAndPrices() {
        List<Product> allProducts = getAllProducts();
        for (Product product : allProducts) {
            product.setStock(0);
            if(product.getName().equals("Water")) {
                product.setPrice(Constants.INITIAL_WATER_PRICE);
            }
            switch (product.getId()) {
                case 1 -> product.setPrice(Constants.INITIAL_WATER_PRICE);
                case 2 -> product.setPrice(Constants.INITIAL_COKE_PRICE);
                case 3 -> product.setPrice(Constants.INITIAL_SODA_PRICE);
                default -> {}
            }
            productRepository.save(product);
        }
    }

    @Override
    public double changeProductPrice(int id, Map<String, Object> productChangeProductPriceRequest) {
        Optional<Product> existingProduct = productRepository.findById(id);
        existingProduct.ifPresent(product -> productChangeProductPriceRequest.forEach(
                        (k, v) -> {
                            if (k.equals("price") && Double.parseDouble(v.toString()) >= 0){
                                Field field = ReflectionUtils.findField(Product.class, k);
                                assert field != null;
                                field.setAccessible(true);
                                ReflectionUtils.setField(field, existingProduct.get(), v);
                            }
                        }
                )
        );
        if(existingProduct.isEmpty()){
            return -1;
        }
        productRepository.save(existingProduct.get());
        return existingProduct.get().getPrice();
    }

    @Override
    public Product findProductByID(int id) {
        if(!productRepository.existsById(id)) {
            return null;
        }
        return productRepository.findById(id).get();
    }

    @Override
    public void initializeProducts() {
        // This method is called whenever the application starts
        if (getAllProducts().size() == 3) {
            return;
        }

        int i = 1;
        while (i < 4) {
            Product product = new Product();
            switch (i) {
                case 1 -> {
                    product.setId(1);
                    product.setName(Constants.PRODUCT_1_NAME);
                    product.setPrice(Constants.INITIAL_WATER_PRICE);
                    product.setStock(0);
                    productRepository.save(product);

                }
                case 2 -> {
                    product.setId(2);
                    product.setName(Constants.PRODUCT_2_NAME);
                    product.setPrice(Constants.INITIAL_COKE_PRICE);
                    product.setStock(0);
                    productRepository.save(product);

                }
                case 3 -> {
                    product.setId(3);
                    product.setName(Constants.PRODUCT_3_NAME);
                    product.setPrice(Constants.INITIAL_SODA_PRICE);
                    product.setStock(0);
                    productRepository.save(product);
                }
                default -> {

                }
            }
            i++;
        }
    }

}

