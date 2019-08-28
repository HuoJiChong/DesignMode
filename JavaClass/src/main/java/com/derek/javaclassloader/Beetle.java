package com.derek.javaclassloader;

/**
 * 1. Java虚拟机加载.class过程
 * <p>
 * 虚拟机把Class文件加载到内存，然后进行校验，解析和初始化，最终形成java类型，这就是虚拟机的类加载机制。加载，验证，准备，初始化这5个阶段的顺序是确定的，
 * <p>
 * 类的加载过程，必须按照这种顺序开始。这些阶段通常是相互交叉和混合进行的。解析阶段在某些情况下，可以在初始化阶段之后再开始---为了支持java语言的运行时绑定。
 * <p>
 * Java虚拟机规范中，没有强制约束什么时候要开始加载，但是，却严格规定了几种情况必须进行初始化（加载，验证，准备则需要在初始化之前开始）：
 * <p>
 * 1）遇到 new、getstatic、putstatic、或者invokestatic 这4条字节码指令，如果没有类没有进行过初始化，则触发初始化
 * <p>
 * 2）使用java.lang.reflect包的方法，对垒进行反射调用的时候，如果没有初始化，则先触发初始化
 * <p>
 * 3）初始化一个类时候，如果发现父类没有初始化，则先触发父类的初始化
 * <p>
 * 2. 加载，验证，解析
 * <p>
 * 加载就是通过指定的类全限定名，获取此类的二进制字节流，然后将此二进制字节流转化为方法区的数据结构，在内存中生成一个代表这个类的Class对象。验证是为了确
 * <p>
 * 保Class文件中的字节流符合虚拟机的要求，并且不会危害虚拟机的安全。加载和验证阶段比较容易理解，这里就不再过多的解释。解析阶段比较特殊，解析阶段是虚拟机
 * <p>
 * 将常量池中的符号引用转换为直接引用的过程。如果想明白解析的过程，得先了解一点class文件的一些信息。class文件采用一种类似C语言的结构体的伪结构来存储我们编
 * <p>
 * 码的java类的各种信息。其中，class文件中常量池（constant_pool）是一个类似表格的仓库，里面存储了我们编写的java类的类和接口的全限定名，字段的名称和描述符，
 * <p>
 * 方法的名称和描述符。在java虚拟机将class文件加载到虚拟机内存之后，class类文件中的常量池信息以及其他的数据会被保存到java虚拟机内存的方法区。我们知道class文件
 * <p>
 * 的常量池存放的是java类的全名，接口的全名和字段名称描述符，方法的名称和描述符等信息，这些数据加载到jvm内存的方法区之后，被称做是符号引用。而把这些类的
 * <p>
 * 全限定名，方法描述符等转化为jvm可以直接获取的jvm内存地址，指针等的过程，就是解析。虚拟机实现可以对第一次的解析结果进行缓存，避免解析动作的重复执行。
 * <p>
 * 在解析类的全限定名的时候，假设当前所处的类为D，如果要把一个从未解析过的符号引用N解析为一个类或者接口C的直接引用，具体的执行办法就是虚拟机会把代表N的
 * <p>
 * 全限定名传递给D的类加载器去加载这个类C。这块可能不太好理解，但是我们可以直接理解为调用D类的ClassLoader来加载N，然后就完成了N--->C的解析，就可以了。
 * <p>
 * 3. 准备阶段
 * <p>
 * 之所以把在解析阶段前面的准备阶段，拿到解析阶段之后讲，是因为，准备阶段已经涉及到了类数据的初始化赋值。和我们本文讲的初始化有关系，所以，就拿到这里来讲
 * <p>
 * 述。在java虚拟机加载class文件并且验证完毕之后，就会正式给类变量分配内存并设置类变量的初始值。这些变量所使用的内存都将在方法区分配。注意这里说的是类变量，
 * <p>
 * 也就是static修饰符修饰的变量，在此时已经开始做内存分配，同时也设置了初始值。比如在 Public static int value = 123 这句话中，在执行准备阶段的时候，会给value
 * <p>
 * 分配内存并设置初始值0， 而不是我们想象中的123. 那么什么时候 才会将我们写的123 赋值给 value呢？就是我们下面要讲的初始化阶段。
 * <p>
 * 4. 初始化阶段
 * <p>
 * 类初始化阶段是类加载过程的最后阶段。在这个阶段，java虚拟机才真正开始执行类定义中的java程序代码。Java虚拟机是怎么完成初始化的呢？这要从编译开始讲起。在编
 * <p>
 * 译的时候，编译器会自动收集类中的所有静态变量（类变量）和静态语句块（static｛｝块）中的语句合并产生的，编译器收集的顺序是根据语句在java代码中的顺序决定的。
 * <p>
 * 收集完成之后，会编译成java类的 static{} 方法，java虚拟机则会保证一个类的static{} 方法在多线程或者单线程环境中正确的执行，并且只执行一次。在执行的过程中，便完
 * <p>
 * 成了类变量的初始化。值得说明的是，如果我们的java类中，没有显式声明static{}块，如果类中有静态变量，编译器会默认给我们生成一个static{}方法。
 */
class Insect {
    private int i = 9;
    protected int j;

    protected static int x1 = printInit("static Insect.x1 initialized");

    Insect() {
        System.out.println("基类构造函数阶段： i = " + i + ", j = " + j);
        j = 39;
    }

    static int printInit(String s) {
        System.out.println(s);
        return 47;
    }
}

public class Beetle extends Insect {

    protected int k = printInit("Beetle.k initialized");

    protected static int x2 = printInit("static Beetle.x2 initialized");

    public static void main(String[] args) {
        /**
         * 注释掉new Beetle()的输出
         * static Insect.x1 initialized
         * static Beetle.x2 initialized
         *
         * -------------------------------------------
         *
         * 有new Beetle()的输出
         * static Insect.x1 initialized
         * static Beetle.x2 initialized
         * 基类构造函数阶段： i = 9, j = 0
         * Beetle.k initialized
         */
        Beetle b = new Beetle();
    }
}


