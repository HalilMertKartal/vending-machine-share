package com.casestudy.vendingmachine.service.VendingMachine;

import com.casestudy.vendingmachine.model.VendingMachine;
import com.casestudy.vendingmachine.utilities.UnitMoney;

import java.util.List;
import java.util.Map;

public interface VendingMachineService {
    // In our implementation, this method will be called just once, if no machines exist in the DB.
    VendingMachine createMachineInstance();
    List<VendingMachine> getAllMachines();
    VendingMachine getVendingMachineInstance();
    VendingMachine updateVendingMachineInstance(Map<String, Object> productAddStockRequest);
    VendingMachine updateVendingMachineInstance(VendingMachine vendingMachineInstance);

    double cooling(); // Performs a dummy cooling operation if necessary

    // Methods that will be accessed from front-end
    double returnProductToUserById(int productID); // Returns the desired product based on some conditions
    double takeRefund(); // User can take the money dropped if cancel is pressed
    double acceptMoneyFromUser(UnitMoney unitMoney); // only accepts varieties of UnitMoney
    double putMoney(double desiredAmount);
    void resetVendingMachine(); // Resets the machine by emptying the stocks, making all money zero.
    double collectMoney(); // Makes zero the amount in the case
    double requestProductByID(int id);
}
