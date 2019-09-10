package com.derek.framework;

import android.content.Intent;

import com.derek.framework.Factory.AudiCar;
import com.derek.framework.Factory.AudiCarFactory;
import com.derek.framework.Factory.AudiFactory;
import com.derek.framework.Factory.AudiQ3;
import com.derek.framework.State.TvController;
import com.derek.framework.Strategy.BusStrategy;
import com.derek.framework.Strategy.TranficCalculator;

public class Client {
    public static void main(String argv[]){
        stateTest();
    }

    public void intent(){
        Intent sharedIntent = new Intent();
        sharedIntent.clone();

    }

    static void factoryTest(){
        AudiFactory factory = new AudiCarFactory();
        AudiCar car = factory.createAudiCar(AudiQ3.class);
        car.drive();
        car.selfNavigation();
    }

    static void strategyTest(){
        TranficCalculator calculator = new TranficCalculator();
        calculator.setmStrategy(new BusStrategy());
        System.out.println("price : " + calculator.calculatePrice(20));
    }

    static void stateTest(){
        TvController controller = new TvController();
        controller.powerOn();

        controller.nextChannel();
        controller.prevChannel();
        controller.turnDown();

        controller.powerOff();
        controller.turnUp();
    }



}
