package com.casestudy.vendingmachine.utilities;
import com.casestudy.vendingmachine.service.Product.ProductService;
import com.casestudy.vendingmachine.service.VendingMachine.VendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InitializeMachine {
    private final VendingMachineService vendingMachineService;
    private final ProductService productService;

    @Autowired
    public InitializeMachine(VendingMachineService vendingMachineService, ProductService productService) {
        this.vendingMachineService = vendingMachineService;
        this.productService = productService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void handleContextRefresh(ContextRefreshedEvent event) {
        // This method will be invoked when the application context is refreshed
        vendingMachineService.createMachineInstance();
        productService.initializeProducts();
    }
}
