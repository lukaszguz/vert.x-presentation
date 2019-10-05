package pl.guz.vertx.m1.eventloop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static pl.guz.vertx.m1.Sleeper.sleep;

public class CreateVerticles {

    private static final Logger logger = LoggerFactory.getLogger(CreateVerticles.class);


    //When Vert.x creates an event loop context, it chooses an event loop for this context, the event loop is chosen via a round robin algorithm.
    @Test
    public void should_create_many_verticles_in_event_loop_context() {
        logger.info("Current thread");
        Vertx vertx = Vertx.vertx();
        for (int i = 0; i < 20; i++) {
            int index = i;
            vertx.setTimer(1, timerID -> logger.info("{}", index));
        }
    }

    @Test
    public void should_create_many_verticles_in_worker_context() throws InterruptedException {
        int messageCount = 10;
        CountDownLatch latch = new CountDownLatch(messageCount);
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(5));
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                vertx.eventBus().consumer("the-address", msg -> {
                    sleep(10);
                    logger.info("Executed");
                    msg.reply("whatever");
                    latch.countDown();
                });
            }
        }, new DeploymentOptions().setWorker(true));

        recursionSent(vertx, messageCount);
        latch.await();
    }

    @Test
    public void should_use_different_thread_in_different_verticle_instance() throws InterruptedException {
        int messageCount = 10;
        CountDownLatch latch = new CountDownLatch(messageCount);
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(2));
        vertx.deployVerticle(
                TheWorker.class.getName(),
                new DeploymentOptions().setWorker(true).setInstances(4)
        );

        for (int i = 0; i < messageCount; i++) {
            vertx.eventBus().send("the-address", "the-message", reply -> {
                logger.info("Reply body: {}", reply.result().body());
                latch.countDown();
            });
        }
        latch.await();
    }

    @Test
    public void should_use_different_thread_in_timer_using_workers() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() throws Exception {
                long now = System.currentTimeMillis();
                logger.info("Starting timer on {}", Thread.currentThread());
                vertx.setTimer(100, id -> {
                    logger.info("Timer fired after {} ms", (System.currentTimeMillis() - now));
                    latch.countDown();
                });
                Thread.sleep(300);
            }
        }, new DeploymentOptions().setWorker(true));

        latch.await();
    }

    @Test
    public void should() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(5));
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() {
                AtomicLong count = new AtomicLong(10);
                long now = System.currentTimeMillis();
                logger.info("Starting periodic on {}", Thread.currentThread());
                vertx.setPeriodic(1000, id -> {
                    if (count.decrementAndGet() < 0) {
                        vertx.cancelTimer(id);
                        latch.countDown();
                    }
                    logger.info("Periodic fired after {} ms", (System.currentTimeMillis() - now));
                });
            }
        }, new DeploymentOptions().setWorker(true));
        latch.await();
    }


    private void recursionSent(Vertx vertx, int count) {
        // We recursionSent when we get the reply in order to not recursionSent all messages at the same time
        // otherwise they might be using the same worker thread and that would defeat the purpose
        // of this example
        if (count >= 0) {
            vertx.eventBus().send("the-address", count, reply ->
                    recursionSent(vertx, count - 1));
        }
    }

}
