package com.casestudy.vendingmachine.service.VendingMachine;

import com.casestudy.vendingmachine.model.Product;
import com.casestudy.vendingmachine.model.VendingMachine;
import com.casestudy.vendingmachine.repository.VendingMachineRepository;
import com.casestudy.vendingmachine.service.Product.ProductService;
import com.casestudy.vendingmachine.utilities.Constants;
import com.casestudy.vendingmachine.utilities.UnitMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VendingMachineServiceImplementation implements VendingMachineService{

    @Autowired
    private VendingMachineRepository vendingMachineRepository;
    @Autowired
    private ProductService productService;
    private VendingMachine vendingMachineInstance;
    private double change = 0;

    // Adds desired amount in the safe case, returns the remaining amount in process
    /*This is a private interior function*/
    private double addMoneyToCase(VendingMachine vendingMachineInstance, double moneyToAdd) {
        double currentMoneyToProcess = vendingMachineInstance.getCurrentMoneyToProcess();
        double currentMoneyInCase = vendingMachineInstance.getTotalMoneyInCase();
        // Safety control, no db updates if something is wrong
        if (currentMoneyToProcess < moneyToAdd) {
            return -1;
        }
        vendingMachineInstance.setTotalMoneyInCase(currentMoneyInCase + moneyToAdd);
        vendingMachineInstance.setCurrentMoneyToProcess(0);
        // Update in db
        updateVendingMachineInstance(vendingMachineInstance);
        return currentMoneyToProcess - moneyToAdd;
    }
    // Returns -1 if tx is problematic
    // Else returns the change, adds remaining amount to safety case by using a function
    /*This is a private interior function*/

    private double returnChange(double productPrice) {
        vendingMachineInstance = getVendingMachineInstance();
        change = addMoneyToCase(vendingMachineInstance, productPrice);
        if (change < 0) {
            // There is a problem, no db updates.
            return -1;
        }

        // Update in db
        updateVendingMachineInstance(vendingMachineInstance);
        return change; // This amount is returned
    }

    @Override
    public VendingMachine createMachineInstance() {
        if (!getAllMachines().isEmpty()){
            // Only create one instance can be created
            return null;
        }
        // Create a brand-new machine
        VendingMachine vendingMachine = new VendingMachine();
        vendingMachine.setMachineID(1);
        vendingMachine.setCurrentMoneyToProcess(0);
        vendingMachine.setTotalMoneyInCase(0);
        vendingMachine.setTemperatureInside(Constants.INITIAL_OUTSIDE_TEMPERATURE);

        return vendingMachineRepository.save(vendingMachine);
    }

    @Override
    public List<VendingMachine> getAllMachines() {
        return vendingMachineRepository.findAll();
    }

    @Override
    public VendingMachine getVendingMachineInstance() {
        // Since we only allowed one machine to be created, we can safely assume there exists only one
        return getAllMachines().get(0) != null ? getAllMachines().get(0) : null;
    }

    @Override
    public VendingMachine updateVendingMachineInstance(Map<String, Object> updateVendingMachineRequest) {
        vendingMachineInstance = getVendingMachineInstance();
        updateVendingMachineRequest.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(VendingMachine.class, k);
            assert field != null;
            field.setAccessible(true);
            ReflectionUtils.setField(field, vendingMachineInstance, v);
        });
        return vendingMachineRepository.save(vendingMachineInstance);
    }

    @Override
    // Simply overwrites the vending machine instance at the database
    public VendingMachine updateVendingMachineInstance(VendingMachine vendingMachineInstance) {
        return vendingMachineRepository.save(vendingMachineInstance);
    }


    @Override
    // Should be called periodically
    public double cooling() {
        vendingMachineInstance = getVendingMachineInstance();
        double currentTemp = vendingMachineInstance.getTemperatureInside();
        if (currentTemp > Constants.DESIRED_INTERIOR_TEMPERATURE) {
            currentTemp -= 0.1;
            currentTemp = Math.round(currentTemp * Math.pow(10, 1))
                    / Math.pow(10, 1);
            vendingMachineInstance.setTemperatureInside(currentTemp);
            // Update in db
            updateVendingMachineInstance(vendingMachineInstance);
        }
        return currentTemp;
    }


    // Returns null if there is a problem, returns the change if tx is successfully done
    @Override
    public double returnProductToUserById(int productID) {
        if (productService.findProductByID(productID) == null) {
            return -1;
        }
        Product product = productService.findProductByID(productID);
        if (product.getStock() < 1) {
            return -1;
        }

        double productPrice = product.getPrice();
        change = returnChange(productPrice);

        if (change < 0) {
            // No db updates, problematic tx
            return -1;
        }

        // Decrease product stocks safely, return the change to front-end.
        productService.decreaseProductStocksByID(productID);
        return change;

    }

    // Cancel the transaction, no product is returned, money is given back so case stays same.

    @Override
    public double takeRefund() {
        vendingMachineInstance = getVendingMachineInstance();
        double currentMoneyToProcess = vendingMachineInstance.getCurrentMoneyToProcess();
        if (currentMoneyToProcess <= 0) {
            return 0;
        }
        vendingMachineInstance.setCurrentMoneyToProcess(0);
        // Update the db
        updateVendingMachineInstance(vendingMachineInstance);
        return currentMoneyToProcess;
    }

    @Override
    // This function is called every time user puts unit money into the machine.
    public double acceptMoneyFromUser(UnitMoney unitMoney) {
        vendingMachineInstance = getVendingMachineInstance();
        double currentMoney = vendingMachineInstance.getCurrentMoneyToProcess();
        vendingMachineInstance.setCurrentMoneyToProcess(currentMoney + unitMoney.getLabel());
        updateVendingMachineInstance(vendingMachineInstance);
        return vendingMachineInstance.getCurrentMoneyToProcess(); /*02,14 0 -> bu yaptım*/
    }

    @Override
    // Handle put money request in controller
    public double putMoney(double desiredAmount) {
        vendingMachineInstance = getVendingMachineInstance();
        for (UnitMoney unitMoney:Constants.moneyToAccept) {
            if ( desiredAmount == unitMoney.getLabel()) {
                acceptMoneyFromUser(unitMoney);
            }
        }
        return vendingMachineInstance.getCurrentMoneyToProcess();
    }

    @Override
    public void resetVendingMachine() {
        vendingMachineInstance = getVendingMachineInstance();
        vendingMachineInstance.setCurrentMoneyToProcess(0);
        vendingMachineInstance.setTotalMoneyInCase(0);
        vendingMachineInstance.setTemperatureInside(Constants.INITIAL_OUTSIDE_TEMPERATURE);
        productService.resetAllStocksAndPrices();
    }

    @Override
    // Returns the collected amount
    public double collectMoney() {
        vendingMachineInstance = getVendingMachineInstance();
        double totalMoneyInCase = vendingMachineInstance.getTotalMoneyInCase();
        vendingMachineInstance.setTotalMoneyInCase(0); // Money is collected by admin
        updateVendingMachineInstance(vendingMachineInstance);
        return totalMoneyInCase;
    }

    @Override
    public double requestProductByID(int id) {
        return returnProductToUserById(id);
    }
}
