package com.derek.framework;

public class ThreadLocalTest {
    ThreadLocal<Long> longLocal = new ThreadLocal<>();
    ThreadLocal<String> stringLocal = new ThreadLocal<>();

    public void set(){
        longLocal.set(Thread.currentThread().getId());
        stringLocal.set(Thread.currentThread().getName());
    }

    public long getLong(){
        return longLocal.get();
    }

    public String getString(){
        return stringLocal.get();
    }

    public void print(){
        System.out.println(getLong());
        System.out.println(getString());
        System.out.println();
    }
}
