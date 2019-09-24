package com.derek.framework.Decorator;

public class CheapCloth extends PersonCloth {
    public CheapCloth(Person mPerson) {
        super(mPerson);
    }

    private void dressShorts(){
        System.out.println("穿件短裤");
    }

    @Override
    public void dressed() {
        super.dressed();
        dressShorts();
    }
}
