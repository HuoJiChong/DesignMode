package com.derek.framework.Observer;

import java.util.Observable;
import java.util.Observer;

/**
 * 订阅者
 */
public class Coder implements Observer {
    public String name;

    public Coder(String name) {
        this.name = name;
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Hi, name " + name + ", DevTechFrontier 内容更新啦 : " + arg.toString());
    }

    @Override
    public String toString() {
        return "Coder{" +
                "name='" + name + '\'' +
                '}';
    }
}
