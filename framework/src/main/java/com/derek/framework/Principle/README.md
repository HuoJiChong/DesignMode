* 1、单一职责原则(Single Responsibility Principle)：SRP,就一个类而言，应该仅有一个引起他变化的原因。
* 2、开闭原则(Open Close Principle)：软件中的对象（类，模块，函数等）应该对于扩展是开放的，但是对于修改是封闭的。
* 3、李氏替换原则(Liskov Substitution Principle)：
    * 1、如果对每一个类型为S的对象O1,都有类型为T的对象O2,使得以T定义的所有程序P在所有的对象O1都替换成O2时，程序P的行为没有发生变化。
    * 2、所有引用基类的地方必须能透明的使用其子类的对象。

* 4、依赖倒置原则(Dependence Inversion Principle)：
    * 1、指代了一种特定的解耦形式，使得高层次的模块啊不依赖于低层次模块的实现细节的目的，依赖模块被颠倒啦。
    * 2、模块间的依赖通过抽象发生，实现类之间不发生直接的依赖关系，其依赖关系是通过接口会模块了类产生的。

* 5、接口隔离原则(Interface Segregation Principle)：
    * 1、客户端不应该依赖他不需要的接口。
    * 2、类间的关系应该建立在最小的接口上。

* 6、迪米特原则(Law of Demeter)，又称为最小知识原则(Least Knowledge Principle)：一个对象应该对其他对象有最小的了解。