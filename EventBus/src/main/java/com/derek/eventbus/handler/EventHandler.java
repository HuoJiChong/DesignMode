package com.derek.eventbus.handler;

import com.derek.eventbus.Subscription;

public interface EventHandler {
    void handleEvent(final Subscription subscription, final Object event);
}
