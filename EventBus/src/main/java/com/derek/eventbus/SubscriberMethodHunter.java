package com.derek.eventbus;

import android.util.Log;

import com.derek.eventbus.annotation.Subscriber;
import com.derek.eventbus.type.EventType;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

class SubscriberMethodHunter {
    private Map<EventType,CopyOnWriteArrayList<Subscription>> mSubcriberMap;

    SubscriberMethodHunter(Map<EventType, CopyOnWriteArrayList<Subscription>> mSubcriberMap) {
        this.mSubcriberMap = mSubcriberMap;
    }

    /**
     * 找到订阅者的所有方法
     * @param subscriber 订阅者
     */
     void findSubcribeMethods(Object subscriber) {
        if (mSubcriberMap == null){
            throw new NullPointerException("the mSubscriberMap is null");
        }

        Class<?> clazz = subscriber.getClass();

        while (clazz != null && !isSystemClass(clazz.getName())){
            final Method[] allMethods = clazz.getDeclaredMethods();

            for (Method method : allMethods){
                //根据注解查找对应的方法
                Subscriber annotation = method.getAnnotation(Subscriber.class);
                if (annotation != null){
                    // 方法参数类型数组
                    Class<?>[] paramsTypeClass = method.getParameterTypes();

                    if (paramsTypeClass.length == 1){
                        Class<?> paramType = convertType(paramsTypeClass[0]);
                        EventType eventType = new EventType(paramType,annotation.tag());
                        // 注册的目标方法
                        TargetMethod subcriberMethod = new TargetMethod(method,eventType,annotation.mode());

                        subscibe(eventType,subcriberMethod,subscriber);
                    }
                }
            }
            //查找父类class类型，以便后续查找父类中符合要求的订阅方法。
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 将找到的订阅函数添加到订阅表中
     * @param eventType 事件类型，EventType
     * @param method    目标方法
     * @param subscriber 订阅者
     */
    private void subscibe(EventType eventType, TargetMethod method, Object subscriber) {
        // 根据事件类型找到所有 订阅列表
        CopyOnWriteArrayList<Subscription> subscriptions = mSubcriberMap.get(eventType);
        if (subscriptions == null){
            subscriptions = new CopyOnWriteArrayList<>();
        }

        Subscription newSubscription = new Subscription(subscriber,method);
        if (subscriptions.contains(newSubscription)){
            return;
        }

        subscriptions.add(newSubscription);
        mSubcriberMap.put(eventType,subscriptions);
    }

    /**
     * remove subscriber methods from map
     *
     * @param subscriber 订阅者
     */
    void removeMethodsFromMap(Object subscriber) {
        Iterator<CopyOnWriteArrayList<Subscription>> iterator = mSubcriberMap
                .values().iterator();
        while (iterator.hasNext()) {
            CopyOnWriteArrayList<Subscription> subscriptions = iterator.next();
            if (subscriptions != null) {
                List<Subscription> foundSubscriptions = new LinkedList<>();

                Iterator<Subscription> subIterator = subscriptions.iterator();
                while (subIterator.hasNext()) {
                    Subscription subscription = subIterator.next();
                    // 获取引用
                    Object cacheObject = subscription.subscriber.get();
                    if ( isObjectsEqual(cacheObject, subscriber) || cacheObject == null) {
                        Log.d("", "### 移除订阅 " + subscriber.getClass().getName());
                        foundSubscriptions.add(subscription);
                    }
                }

                // 移除该subscriber的相关的Subscription
                subscriptions.removeAll(foundSubscriptions);
            }

            // 如果针对某个Event的订阅者数量为空了,那么需要从map中清除
            if (subscriptions == null || subscriptions.size() == 0) {
                iterator.remove();
            }
        }
    }

    private boolean isObjectsEqual(Object cachedObj, Object subscriber) {
        return cachedObj != null && cachedObj.equals(subscriber);
    }

    /**
     * if the subscriber method's type is primitive, convert it to corresponding
     * Object type. for example, int to Integer.
     *
     * @param eventType origin Event Type
     * @return 对应的类对象
     */
    private Class<?> convertType(Class<?> eventType) {
        Class<?> returnClass = eventType;
        if (eventType.equals(boolean.class)) {
            returnClass = Boolean.class;
        } else if (eventType.equals(int.class)) {
            returnClass = Integer.class;
        } else if (eventType.equals(float.class)) {
            returnClass = Float.class;
        } else if (eventType.equals(double.class)) {
            returnClass = Double.class;
        }

        return returnClass;
    }

    private boolean isSystemClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
    }

}
