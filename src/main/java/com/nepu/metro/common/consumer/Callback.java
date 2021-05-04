package com.nepu.metro.common.consumer;

public interface Callback<T> {

    public void handle(final T message);
}