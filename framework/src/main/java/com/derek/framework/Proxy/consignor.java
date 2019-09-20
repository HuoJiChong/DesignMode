package com.derek.framework.Proxy;

public class Consignor implements ILawsuit {
    @Override
    public void submit() {
        System.out.println(" consignor submit");
    }

    @Override
    public void burden() {
        System.out.println(" consignor burden");
    }

    @Override
    public void defend() {
        System.out.println(" consignor defend");
    }

    @Override
    public void finish() {
        System.out.println(" consignor finish");
    }
}
