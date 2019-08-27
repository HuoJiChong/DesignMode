package com.derek.javaclassloader;

class Base {

    Base() {
        System.out.println("Base()");
        preProcess();
    }

    void preProcess() {
        System.out.println("Base preProcess()");
    }
}

public class Derived extends Base {

    public String whenAmISet = "set when declared";

    @Override
    void preProcess() {
        whenAmISet = "set in preProcess";
        System.out.println("Derived preProcess()");
    }

    /**
     * 输出结果为：
     *  Base()
     *  Derived preProcess()
     *  set when declared
     *
     *  加载过程：
     * 1. 执行Derived 类 static main 方法的时候，执行类变量初始化，但是此例中父类和子类都没有类变量，所以此步骤什么都不做，进行实例变量初始化
     *
     * 2. 执行new Derived()的时候，先调用了父类的构造函数（输出: Base() ），因为子类的重载，调用了子类的preProcess方法，为实例变量whenAmISet 赋值为"set in preProcess",（输出: Derived preProcess() ）
     *
     * 3. 然后执行子类Derived 的构造函数，在构造函数中，有编译器为我们收集生成的实例变量赋值语句，最终，又将实例变量whenAmISet 赋值为"set when declared"
     *
     * 4. 所以最终的输出是：
     *       Base()
     *       Derived preProcess()
     *       set when declared
     *
     * @param args
     */
    public static void main(String[] args) {
        Derived d = new Derived();
        System.out.println(d.whenAmISet);
    }
}