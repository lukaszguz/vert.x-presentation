package pl.guz.vertx.m1.eventloop;

import io.vertx.core.AbstractVerticle;

import java.util.concurrent.atomic.AtomicInteger;

import static pl.guz.vertx.m1.Sleeper.sleep;

public class TheWorker extends AbstractVerticle {

    private static final AtomicInteger serial = new AtomicInteger();
    private final int id = serial.incrementAndGet();

    @Override
    public void start() {
        vertx.eventBus().consumer("the-address", msg -> {
            sleep(10);
            msg.reply("Executed by worker " + id + " with " + Thread.currentThread());
        });
    }
}
