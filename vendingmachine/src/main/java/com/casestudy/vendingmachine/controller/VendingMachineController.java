package com.casestudy.vendingmachine.controller;

import com.casestudy.vendingmachine.model.VendingMachine;
import com.casestudy.vendingmachine.service.VendingMachine.VendingMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/vending")
public class VendingMachineController {
    @Autowired
    private VendingMachineService vendingMachineService;

    /*
    User-end functions
    */
    // Put money into the machine. It needs to be called with "money":int (amount desired to put 1,5,10 or 20)

    @PatchMapping("/putMoney{desiredAmount}")
    public String putMoney(@PathVariable double desiredAmount){
        double currentMoney = vendingMachineService.putMoney(desiredAmount);
        return String.valueOf(currentMoney);
    }

    @PatchMapping("/takeRefund")
    public String takeRefund(){
        double refundAmount = vendingMachineService.takeRefund();
        return String.valueOf(refundAmount);
    }


    @PatchMapping("/requestProduct{id}")
    public String requestProductByID(@PathVariable int id){
        double change = vendingMachineService.requestProductByID(id);
        if (change < 0) {
            return String.valueOf(-1);
        }
        return String.valueOf(change);
    }


    /*
    Supplier functions
    */

    @GetMapping("/getInstance")
    public VendingMachine getVendingMachineInstance() {
        return vendingMachineService.getVendingMachineInstance();
    }

    @GetMapping("/getTemperature")
    public double getVendingMachineTemperature() {
        return vendingMachineService.getVendingMachineInstance().getTemperatureInside();
    }

    @PatchMapping("/updateInstance")
    public VendingMachine updateVendingMachineInstance(@RequestBody Map<String, Object> productAddStockRequest) {
        return vendingMachineService.updateVendingMachineInstance(productAddStockRequest);
    }

    @PatchMapping("/resetMachine")
    public String resetVendingMachine() {
        vendingMachineService.resetVendingMachine();
        return "Vending Machine successfully reset";
    }

    @PatchMapping("/collectMoney")
    public String collectMoney() {
        double collectedAmount = vendingMachineService.collectMoney();
        return "Successfully collected: " + collectedAmount;
    }

    // This will be called periodically by front end
    @PatchMapping("/cooling")
    public double cooling() {
        return vendingMachineService.cooling();
    }

}
