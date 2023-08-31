package com.casestudy.vendingmachine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class VendingMachine {
    @Id
    private int machineID; // id of the vending machine, it's also a primary key for Product entity
    private double currentMoneyToProcess; // The temporary place to store current user's money
    private double totalMoneyInCase; // Total money in the safety case. It can only be emptied by the vending machine supplier
    private double temperatureInside; // Keeps the temperature of inside the machine

    public int getMachineID() {
        return machineID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    public double getCurrentMoneyToProcess() {
        return currentMoneyToProcess;
    }

    public void setCurrentMoneyToProcess(double currentMoneyToProcess) {
        this.currentMoneyToProcess = currentMoneyToProcess;
    }

    public double getTotalMoneyInCase() {
        return totalMoneyInCase;
    }

    public void setTotalMoneyInCase(double totalMoneyInCase) {
        this.totalMoneyInCase = totalMoneyInCase;
    }

    public double getTemperatureInside() {
        return temperatureInside;
    }

    public void setTemperatureInside(double temperatureInside) {
        this.temperatureInside = temperatureInside;
    }
}
