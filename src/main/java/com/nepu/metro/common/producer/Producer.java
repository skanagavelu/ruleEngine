package com.nepu.metro.common.producer;

public interface Producer<T> {

    void submit(T message);
    void close();
}
