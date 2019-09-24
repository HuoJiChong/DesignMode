package com.derek.framework.Decorator;

public abstract class PersonCloth extends Person {
    private Person mPerson;

    public PersonCloth(Person mPerson) {
        this.mPerson = mPerson;
    }

    @Override
    public void dressed() {
        mPerson.dressed();
    }
}
