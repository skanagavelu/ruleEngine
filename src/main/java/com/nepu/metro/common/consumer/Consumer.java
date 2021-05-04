package com.nepu.metro.common.consumer;

import java.util.Iterator;

public interface Consumer<T> extends Iterator<T> {

    void start();

    void close();

    boolean hasNext();

    T next();
}
