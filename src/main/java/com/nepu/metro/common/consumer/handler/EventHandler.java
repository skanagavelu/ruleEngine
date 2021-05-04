package com.nepu.metro.common.consumer.handler;

import com.nepu.metro.common.consumer.Callback;

public interface EventHandler<T, R> extends Callback<R> {

    void handle(final R message);

    void done();
}