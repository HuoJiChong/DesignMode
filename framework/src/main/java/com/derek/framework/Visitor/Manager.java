package com.derek.framework.Visitor;

import java.util.Random;

/**
 * 经理
 */
public class Manager extends Staff {
    private int products;

    public Manager(String name) {
        super(name);
        products = new Random().nextInt(10);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visite(this);
    }

    public int getProducts() {
        return products;
    }
}
