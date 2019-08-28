package com.derek.aop.aspect;

import android.util.Log;

import com.derek.aop.annotation.BehaviorTrace;
import com.derek.aop.internal.StopWatch;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.text.SimpleDateFormat;
import java.util.Date;

@Aspect
public class BehaviorAspect {
    private static final String TAG = "dongnao";
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 如何切蛋糕，切成什么样的形状
     * 切点
     */
    @Pointcut("execution(@com.derek.aop.annotation.BehaviorTrace  * *(..))")
    public void annoBehavior()
    {

    }

    /**
     * 切面
     * 蛋糕按照切点切下来之后   怎么吃
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("annoBehavior()")
    public Object dealPoint(ProceedingJoinPoint point) throws  Throwable
    {
        //方法执行前
        MethodSignature methodSignature= (MethodSignature) point.getSignature();
        BehaviorTrace behaviorTrace=methodSignature.getMethod().getAnnotation(BehaviorTrace.class);
        String contentType=behaviorTrace.value();
        int type=behaviorTrace.type();
        Log.i(TAG,contentType+"使用时间：   "+simpleDateFormat.format(new Date()));
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //方法执行时
        Object object=null;
        try {
            object = point.proceed();
        } catch (Exception e) {
        }

        stopWatch.stop();

        //方法执行完成
        Log.i(TAG,"消耗时间：  "+stopWatch.getTotalTimeMillis()+"ms");

        return  object;
    }
}