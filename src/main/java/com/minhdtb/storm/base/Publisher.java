package com.minhdtb.storm.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
public class Publisher<T> {

    @Autowired
    EventBus eventBus;

    public void publish(String key, T object) {
        eventBus.notify(key, Event.wrap(object));
    }
}
