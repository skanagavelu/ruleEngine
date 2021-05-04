package com.nepu.metro.ruleEngine.events.consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import com.nepu.metro.common.consumer.Consumer;
import com.nepu.metro.common.consumer.handler.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Application execution begins here.
 * Events are consumed and handled here.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class TravelEventConsumer<T> {

    @Autowired
    private Consumer<T> consumer;

    @Autowired
    private EventHandler<T, T> handler;

    @Value("${consumer.workers.count:2}")
    private int corePoolSize;

    private ThreadPoolExecutor executor;

    private volatile boolean proceed;

    @PostConstruct
    public void start() {

        proceed = true;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(corePoolSize, new CustomThreadFactory("EventConsumer"));
        consumer.start();
        IntStream.range(0, corePoolSize).forEach(i -> {
            executor.execute(new TravelEventTask());
        });
    }

    @PreDestroy
    public void close() {

        this.proceed = false;
        this.executor.shutdown();
        this.handler.done();
    }

    private class TravelEventTask implements Runnable {

        @Override
        public void run() {

            while (proceed) {

                if (consumer.hasNext()) {

                    T event = consumer.next();
                    handler.handle(event);
                }
            }
        }
    }

    private class CustomThreadFactory implements ThreadFactory
    {
        private String name;
        private int counter = 1;

        public CustomThreadFactory(String name)
        {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable runnable)
        {
            Thread t = new Thread(runnable, name + "-Thread-" + counter);
            counter++;
            return t;
        }
    }
}
