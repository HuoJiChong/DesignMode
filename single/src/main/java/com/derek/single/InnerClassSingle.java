package com.derek.single;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class InnerClassSingle implements Serializable {
    private static final long serialVersionUID = 0L;
    private InnerClassSingle(){

    }
    private static class SingleHolder {
        private static final InnerClassSingle instance = new InnerClassSingle();
    }

    /**
     * 内部类 单例
     *
     *  内部类SingleHolder只有在getInstance()方法第一次调用的时候才会被加载（实现了延迟加载效果），而且其加载过程是线程安全的（实现线程安全）。
     *  内部类加载的时候只实例化了一次instance
     *
     *  1、保证线程安全性
     *  2、延迟加载
     *
     * @return
     */
    public static InnerClassSingle getSingle(){
        return SingleHolder.instance;
    }

    public void tellEveryone(){
        System.out.println("This is a InnerClassSingle  " + this.hashCode());
    }

    /**
     * 可以让开发人员控制对象的反序列化。
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return  SingleHolder.instance;
    }
}
