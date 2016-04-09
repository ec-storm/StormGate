package com.minhdtb.storm.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import static reactor.bus.selector.Selectors.$;

@Service
public class Subscriber<T> {

    @Autowired
    EventBus eventBus;

    public void on(String key, Consumer<T> callback) {
        eventBus.on($(key), new Consumer<Event<T>>() {
            @Override
            public void accept(Event<T> event) {
                callback.accept(event.getData());
            }
        });
    }
}

