package com.derek.framework;

import android.content.Intent;

import com.derek.framework.Expression.Calculator;
import com.derek.framework.Factory.AudiCar;
import com.derek.framework.Factory.AudiCarFactory;
import com.derek.framework.Factory.AudiFactory;
import com.derek.framework.Factory.AudiQ3;
import com.derek.framework.Handler.Handler1;
import com.derek.framework.Handler.Handler2;
import com.derek.framework.Handler.Handler3;
import com.derek.framework.Handler.Request1;
import com.derek.framework.Handler.Request2;
import com.derek.framework.Handler.Request3;
import com.derek.framework.Handler.base.AbstractHandler;
import com.derek.framework.Handler.base.AbstractRequest;
import com.derek.framework.Observer.Coder;
import com.derek.framework.Observer.DevTechFrontier;
import com.derek.framework.Proxy.Consignor;
import com.derek.framework.Proxy.DynamicProxy;
import com.derek.framework.Proxy.ILawsuit;
import com.derek.framework.State.TvController;
import com.derek.framework.Strategy.BusStrategy;
import com.derek.framework.Strategy.TranficCalculator;
import com.derek.framework.Visitor.BusinessReport;
import com.derek.framework.Visitor.CEOVisitor;
import com.derek.framework.Visitor.CTOVisitor;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Client {
    public static void main(String argv[]){

        dynamicTest();
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

    static void handler(){
        AbstractHandler handler1 = new Handler1();
        AbstractHandler handler2 = new Handler2();
        AbstractHandler handler3 = new Handler3();

        handler1.nextHandler = handler2;
        handler2.nextHandler = handler3;

        AbstractRequest request1 = new Request1("Request1");
        AbstractRequest request2 = new Request2("Request2");
        AbstractRequest request3 = new Request3("Request3");

        handler1.handleRequest(request1);
        handler1.handleRequest(request2);
        handler1.handleRequest(request3);
    }

    static void expressionTest(){
        Calculator calculator = new Calculator("23 + 34 + 56");
        System.out.println(calculator.calculate());

    }

    static void observerTest(){
        DevTechFrontier frontier = new DevTechFrontier();

        Coder mrSample = new Coder("mr.sample");
        Coder coder1 = new Coder("coder1");
        Coder coder2 = new Coder("coder2");
        Coder coder3 = new Coder("coder3");

        frontier.addObserver(mrSample);
        frontier.addObserver(coder1);
        frontier.addObserver(coder2);
        frontier.addObserver(coder3);

        frontier.postNewPublication("新一期的内容发布啦。。。");
    }

    static void threadLocalTest(){
        final ThreadLocalTest test = new ThreadLocalTest();
        test.set();
        test.print();
        Thread t = new Thread(){
            @Override
            public void run() {
                test.set();
                test.print();
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        test.print();
    }

    public void methodTest(String name){
        System.out.println("methodTest：" + name);
    }


    static void visitorTest(){
        BusinessReport report = new BusinessReport();
        System.out.println("==========给CEO看的报表==============");
        report.showReport(new CEOVisitor());

        System.out.println("==========给CTO看的报表==============");
        report.showReport(new CTOVisitor());

    }

    /**
     * 动态代理
     */
    static void dynamicTest(){
        ILawsuit consignor = new Consignor();
        DynamicProxy proxy = new DynamicProxy(consignor);
        ClassLoader loader = consignor.getClass().getClassLoader();

        ILawsuit lawyer = (ILawsuit) Proxy.newProxyInstance(loader,new Class[]{ILawsuit.class},proxy);

        lawyer.submit();
        lawyer.defend();
        lawyer.burden();
        lawyer.finish();

    }

}
