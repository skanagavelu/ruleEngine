package com.nepu.metro.common.consumer.handler;

import java.util.function.Function;

public interface Converter<T, R> {

    Function<T, R> convert();
}
