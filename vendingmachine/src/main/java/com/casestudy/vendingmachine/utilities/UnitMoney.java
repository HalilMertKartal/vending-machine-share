package com.casestudy.vendingmachine.utilities;

public enum UnitMoney {
    ONE(1),
    FIVE(5),
    TEN(10),
    TWENTY(20);

    private final double label;

    public double getLabel() {
        return label;
    }

    UnitMoney(double label) {
        this.label = label;
    }
}
