package com.derek.single;

import java.io.Serializable;

public class DCLSingle implements Serializable {
    private DCLSingle(){

    }
    private static DCLSingle single;

    /**
     * 双重检查锁
     *
     *  JVM为了使得处理器内部的运算单元能充分利用，处理器可能会对输入代码进行乱序执行（Out Of Order Execute）优化，
     *  处理器会在计算之后将乱序执行的结果进行重组，保证该结果与顺序执行的结果是一样的，
     *  但并不保证程序中各个语句计算的先后顺序与输入的代码顺序一致
     *
     *  (不是百分百线程安全)
     * @return
     */
    public static DCLSingle getSingle(){
        if (single == null){
            synchronized (DCLSingle.class){
                if (single == null){
                    single = new DCLSingle();
                }
            }
        }
        return single;
    }

    public void tellEveryone(){
        System.out.println("This is a DoubleCheckLockSingleton  " + this.hashCode());
    }
}
