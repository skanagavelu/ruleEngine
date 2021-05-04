package com.nepu.metro.common.consumer.handler;

import java.util.function.Function;

public abstract class EventHandlerBase<T, R> implements EventHandler<T, R> {

    //Converter to enrich or map the raw queue event
    public final Function<T, R> converter;

    public EventHandlerBase(Function<T, R> converter) {

        //Convertor function, to generify consumer raw object
        this.converter = converter;
    }
}
