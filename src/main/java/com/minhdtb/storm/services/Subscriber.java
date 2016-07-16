package com.minhdtb.storm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import static reactor.bus.selector.Selectors.$;

@Service
public class Subscriber<T> {

    private final EventBus eventBus;

    @Autowired
    public Subscriber(EventBus eventBus) {
        Assert.notNull(eventBus, "EventBus must not be null");
        this.eventBus = eventBus;
    }

    public void on(String key, Consumer<T> callback) {
        eventBus.on($(key), (Consumer<Event<T>>) event -> callback.accept(event.getData()));
    }
}

