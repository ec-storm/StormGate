package com.minhdtb.storm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class Publisher<T> {

    private final EventBus eventBus;

    @Autowired
    public Publisher(EventBus eventBus) {
        Assert.notNull(eventBus, "EventBus must not be null");
        this.eventBus = eventBus;
    }

    public void publish(String key, T object) {
        eventBus.notify(key, Event.wrap(object));
    }
}
