package com.derek.eventbus.policy;

import com.derek.eventbus.type.EventType;

import java.util.List;

public interface MatchPolicy {
    List<EventType> findMatchEventTypes(EventType type, Object aEvent);
}
