package com.derek.framework.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 原子操作
 */
class BarWorker implements Runnable{
    private static AtomicBoolean exists = new AtomicBoolean(false) ;
    private String name;

    BarWorker(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        /**
         *  compareAndSet方法的执行过程
         *  1. 比较AtomicBoolean和expect的值，如果一致，执行方法内的语句（其实就是一个if语句）， 并且把AtomicBoolean的值设成update
         *  比较最要的是这两件事是一气呵成的，这连个动作之间不会被打断，任何内部或者外部的语句都不可能在两个动作之间运行。
         */
        if (exists.compareAndSet(false,true)) {

            System.out.println(name + " enter");
            try {
                System.out.println(name + " working");
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                // do nothing
            }
            System.out.println(name + " leave");
            exists.set(false);
        } else {
            System.out.println(name + " give up");
        }
    }
}
public class AtomicBooleanTest {

    public static void main(String[] argc){
        BarWorker worker1 = new BarWorker("bar1");
        BarWorker worker2 = new BarWorker("bar2");
        new Thread(worker1).start();
        new Thread(worker2).start();
    }

}
