package com.derek.eventbus;

import android.print.PrinterId;

import com.derek.eventbus.handler.AsyncEventHandler;
import com.derek.eventbus.handler.DefaultEventHandler;
import com.derek.eventbus.handler.EventHandler;
import com.derek.eventbus.handler.UIThreadEventHandler;
import com.derek.eventbus.policy.DefaultMatchPolicy;
import com.derek.eventbus.policy.MatchPolicy;
import com.derek.eventbus.type.EventType;
import com.derek.eventbus.type.ThreadMode;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventBus {

    private static final String DESCRIPTOR = EventBus.class.getSimpleName();

    /**
     * 事件总线描述符描述符
     */
    private String mDesc = DESCRIPTOR;
    private EventBus() {
        this(DESCRIPTOR);
    }

    /**
     * constructor with desc
     *
     * @param desc the descriptor of eventbus
     */
    public EventBus(String desc) {
        mDesc = desc;
    }

    private static class EventBusHolder{
        private static EventBus instance = new EventBus();
    }

    public static EventBus getDefault() {
        return EventBusHolder.instance;
    }

    private final Map<EventType,CopyOnWriteArrayList<Subscription>> mSubcriberMap = new ConcurrentHashMap<>();

    SubscriberMethodHunter mMethodHunter = new SubscriberMethodHunter(mSubcriberMap);

    public void register(Object subscriber){
        if (subscriber == null){
            return;
        }

        synchronized (this){
            mMethodHunter.findSubcribeMethods(subscriber);
        }
    }

    public void unregister(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        synchronized (this) {
            mMethodHunter.removeMethodsFromMap(subscriber);
        }
    }


    /**
     * 发布
     */
    /**
     * local event queue
     */
    ThreadLocal<Queue<EventType>> mLocalEvents = new ThreadLocal<Queue<EventType>>(){
        @Override
        protected Queue<EventType> initialValue() {
            return new ConcurrentLinkedQueue<EventType>();
        }
    };

    EventDispatcher mDispatcher = new EventDispatcher();


    public void post(Object event) {
        post(event, EventType.DEFAULT_TAG);
    }


    /**
     * 发布事件
     * @param event
     * @param tag
     */
    public void post(Object event,String tag){
        mLocalEvents.get().offer(new EventType(event.getClass(),tag));
        mDispatcher.dispatchEvents(event);
    }

    /**
     * 事件分发器
     */
    class EventDispatcher {
        EventHandler mUIThreadEventHandler = new UIThreadEventHandler();
        EventHandler mPostThreadHandler = new DefaultEventHandler();
        EventHandler mAsyncEventHandler = new AsyncEventHandler();

        private Map<EventType, List<EventType>> mCacheEventTypes = new ConcurrentHashMap<EventType, List<EventType>>();

        /**
         * 事件匹配策略,根据策略来查找对应的EventType集合
         */
        MatchPolicy mMatchPolicy = new DefaultMatchPolicy();

        void dispatchEvents(Object event) {
            Queue<EventType> eventsQueue = mLocalEvents.get();
            while (eventsQueue.size() > 0){
                deliveryEvent(eventsQueue.poll(),event);
            }
        }

        private void deliveryEvent(EventType type, Object event) {
            Class<?> eventClass = event.getClass();
            List<EventType> eventTypes = null;

            if (mCacheEventTypes.containsKey(eventClass)){
                eventTypes = mCacheEventTypes.get(type);
            }else {
                eventTypes = mMatchPolicy.findMatchEventTypes(type,event);
                mCacheEventTypes.put(type,eventTypes);
            }

            for (EventType eventType : eventTypes){
                handleEvent(eventType,event);
            }
        }

        private void handleEvent(EventType eventType, Object event) {
            List<Subscription> subscriptions = mSubcriberMap.get(eventType);
            if (subscriptions == null) {
                return;
            }

            for (Subscription subscription : subscriptions) {
                final ThreadMode mode = subscription.threadMode;
                EventHandler eventHandler = getEventHandler(mode);
                // 处理事件
                eventHandler.handleEvent(subscription, event);
            }
        }

        private EventHandler getEventHandler(ThreadMode mode) {
            if (mode == ThreadMode.ASYNC) {
                return mAsyncEventHandler;
            }
            if (mode == ThreadMode.POST) {
                return mPostThreadHandler;
            }
            return mUIThreadEventHandler;
        }
    }
}
