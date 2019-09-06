package com.derek.android.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Calendar;

@Aspect
public class TestAnnoAspect {

    @Pointcut("execution(* com.derek.android.MainActivity.onAopTest(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before(JoinPoint point) {
        System.out.println("@Before");
    }

    @Around("pointcut()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("@Around");
//        防止多次点击
        long currentClickTime = Calendar.getInstance().getTimeInMillis();
        if (currentClickTime - lastClickTime > clickInterval){
            joinPoint.proceed();
        }
    }

    @After("pointcut()")
    public void after(JoinPoint point) {
        System.out.println("@After");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(JoinPoint point, Object returnValue) {
        System.out.println("@AfterReturning");
    }

    @AfterThrowing(value = "pointcut()", throwing = "ex")
    public void afterThrowing(Throwable ex) {
        System.out.println("@afterThrowing");
        System.out.println("ex = " + ex.getMessage());
    }

    private static long lastClickTime = 0;
    private static long clickInterval = 600;

}