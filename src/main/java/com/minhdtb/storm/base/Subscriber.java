package com.minhdtb.storm.base;

import reactor.bus.Event;
import reactor.fn.Consumer;

public class Subscriber<T> implements Consumer<Event<T>> {

    private Consumer<T> localCallback;

    public Subscriber(Consumer<T> callback) {
        localCallback = callback;
    }

    @Override
    public void accept(Event<T> event) {
        localCallback.accept(event.getData());
    }
}
