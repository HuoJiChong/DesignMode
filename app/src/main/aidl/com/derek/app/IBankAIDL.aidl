// IBankAIDL.aidl
package com.derek.app;

// Declare any non-default types here with import statements

interface IBankAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String openAccount(String name,String pwd);
    String saveMoney(int money,String account);
    String takeMoney(int money,String account,String pwd);
    String closeAccount(String name,String pwd);
}
