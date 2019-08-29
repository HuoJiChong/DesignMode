
#AOP的基本知识

##1、AOP术语
* `通知、增强处理（Advice）`：就是你想要的功能，也就是上面说的日志、耗时计算等。
* `连接点（JoinPoint）`：允许你通知（Advice）的地方，那可就真多了，基本每个方法的前、后（两者都有也行），或抛出异常是时都可以是连接点（spring只支持方法连接点）。AspectJ还可以让你在构造器或属性注入时都行，不过一般情况下不会这么做，只要记住，和方法有关的前前后后都是连接点。
* `切入点（Pointcut）`：上面说的连接点的基础上，来定义切入点，你的一个类里，有15个方法，那就有十几个连接点了对吧，但是你并不想在所有方法附件都使用通知（使用叫织入，下面再说），你只是想让其中几个，在调用这几个方法之前、之后或者抛出异常时干点什么，那么就用切入点来定义这几个方法，让切点来筛选连接点，选中那几个你想要的方法。
* `切面（Aspect）`：切面是通知和切入点的结合。现在发现了吧，没连接点什么事，连接点就是为了让你好理解切点搞出来的，明白这个概念就行了。通知说明了干什么和什么时候干（什么时候通过before，after，around等AOP注解就能知道），而切入点说明了在哪干（指定到底是哪个方法），这就是一个完整的切面定义。
* `织入（weaving）`： 把切面应用到目标对象来创建新的代理对象的过程。

上述术语的解释引用自《AOP中的概念通知、切点、切面》这篇文章，作者的描述非常直白，很容易理解，点个赞。

##2、AOP注解与使用
* @Aspect：声明切面，标记类
* @Pointcut(切点表达式)：定义切点，标记方法
* @Before(切点表达式)：前置通知，切点之前执行
* @Around(切点表达式)：环绕通知，切点前后执行
* @After(切点表达式)：后置通知，切点之后执行
* @AfterReturning(切点表达式)：返回通知，切点方法返回结果之后执行
* @AfterThrowing(切点表达式)：异常通知，切点抛出异常时执行

@Pointcut、@Before、@Around、@After、@AfterReturning、@AfterThrowing需要在切面类中使用，即在使用@Aspect的类中。

###1）切点表达式
这就是切点表达式：`execution (* com.lqr..*.*(..))`。切点表达式的组成如下：

execution(<修饰符模式>? <返回类型模式> <方法名模式>(<参数模式>) <异常模式>?)

除了返回类型模式、方法名模式和参数模式外，其它项都是可选的。

修饰符模式指的是public、private、protected，异常模式指的是NullPointException等。

对于切点表达式的理解不是本篇重点，下面列出几个例子说明一下就好了：

    @Before("execution(public * *(..))")
    public void before(JoinPoint point) {
        System.out.println("CSDN_LQR");
    }
匹配所有public方法，在方法执行之前打印"CSDN_LQR"。

    @Around("execution(* *to(..))")
    public void around(ProceedingJoinPoint joinPoint) {
        System.out.println("CSDN");
        joinPoint.proceed();
        System.out.println("LQR");
    }
匹配所有以"to"结尾的方法，在方法执行之前打印"CSDN"，在方法执行之后打印"LQR"。

    @After("execution(* com.lqr..*to(..))")
    public void after(JoinPoint point) {
        System.out.println("CSDN_LQR");
    }
匹配com.lqr包下及其子包中以"to"结尾的方法，在方法执行之后打印"CSDN_LQR"。

    @AfterReturning("execution(int com.lqr.*(..))")
    public void afterReturning(JoinPoint point, Object returnValue) {
        System.out.println("CSDN_LQR");
    }
匹配com.lqr包下所有返回类型是int的方法，在方法返回结果之后打印"CSDN_LQR"。

    @AfterThrowing(value = "execution(* com.lqr..*(..))", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        System.out.println("ex = " + ex.getMessage());
    }
匹配com.lqr包及其子包中的所有方法，当方法抛出异常时，打印"ex = 报错信息"。

###2）@Pointcut的使用
@Pointcut是专门用来定义切点的，让切点表达式可以复用。

你可能需要在切点执行之前和切点报出异常时做些动作（如：出错时记录日志），可以这么做：

    @Before("execution(* com.lqr..*(..))")
    public void before(JoinPoint point) {
        System.out.println("CSDN_LQR");
    }

    @AfterThrowing(value = "execution(* com.lqr..*(..))", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        System.out.println("记录日志");
    }
可以看到，表达式是一样的，那要怎么重用这个表达式呢？这就需要用到@Pointcut注解了，@Pointcut注解是注解在一个空方法上的，如：

    @Pointcut("execution(* com.lqr..*(..))")
    public void pointcut() {}
这时，"pointcut()"就等价于"execution(* com.lqr..*(..))"，那么上面的代码就可以这么改了：

    @Before("pointcut()")
    public void before(JoinPoint point) {
        System.out.println("CSDN_LQR");
    }

    @AfterThrowing(value = "pointcut()", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        System.out.println("记录日志");
    }