package com.casestudy.vendingmachine.controller;

import com.casestudy.vendingmachine.model.Product;
import com.casestudy.vendingmachine.service.Product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Rest controller handle HTTP requests
@RestController
@CrossOrigin
@RequestMapping("/product")
public class ProductController {
    // Instance of product service is needed
    @Autowired
    private ProductService productService;

    // Method to handle post requests for getting a product
    @GetMapping("/getAll")
    public List<Product> get() {
        return productService.getAllProducts();
    }

    @GetMapping("/get{id}")
    public Product getProductWithID(@PathVariable int id){
        return productService.findProductByID(id);
    }

    /*
    Supplier functions
    */

    // Method to handle post requests for adding a product
    @PostMapping("/add")
    public String add(@RequestBody Product product) {
        productService.addProduct(product);
        return "A new product is added";
    }

    @PatchMapping("/addStocks{id}")
    public String addStocksToAProduct(@PathVariable int id, @RequestBody Map<String, Object> productAddStockRequest) {
        Product p = productService.addStocksToProduct(id, productAddStockRequest);
        if(p == null) {
            return "Product with id: "+id+" is not present";
        }
        return "Stocks of product: "+p.getName()+" is changed as: "+p.getStock();
    }

    @PatchMapping("/changePrice{id}")
    public String changeProductPrice(@PathVariable int id, @RequestBody Map<String, Object> productChangeProductPriceRequest) {
        double newPrice = productService.changeProductPrice(id, productChangeProductPriceRequest);
        if(newPrice < 0) {
            return "Product with id: "+id+" is not present";
        }
        return "Price of product: "+productService.findProductByID(id).getName()+" is changed as: "+newPrice;
    }
}
