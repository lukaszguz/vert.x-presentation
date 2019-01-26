package pl.guz.vertx.m1.verticles;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.guz.vertx.m1.Sleeper;

import java.util.concurrent.CountDownLatch;

public class StandardVerticleTest {

    private final Logger logger = LoggerFactory.getLogger(StandardVerticleTest.class);

    @Test
    public void standard_verticle() throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        final CountDownLatch latch = new CountDownLatch(1);

        vertx.deployVerticle(
                new AbstractVerticle() {
                    @Override
                    public void start() {
                        logger.info("Standard Verticle Consumer");
                        vertx.eventBus()
                                .<JsonObject>consumer("test-address", event -> {
                                    logger.info("Got event: {}", event.body().getString("text"));
                                    latch.countDown();
                                });
                    }
                },
                idDeployedVerticle -> {
                    vertx.deployVerticle(new AbstractVerticle() {
                        @Override
                        public void start() {
                            logger.info("Standard Verticle Producer");
                            vertx.eventBus()
                                    .send("test-address", new JsonObject().put("text", "hello"));
                        }
                    });
                });
        latch.await();
    }

    @Test
    public void worker_verticle() throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        final CountDownLatch latch = new CountDownLatch(5);
        vertx.deployVerticle(() -> new BlockingWorkerVerticle(latch),
                new DeploymentOptions().setWorker(true).setWorkerPoolName("work-pool").setWorkerPoolSize(10),
                idDeployedVerticle -> {
                    vertx.deployVerticle(new AbstractVerticle() {
                        @Override
                        public void start() {
                            logger.info("Standard Verticle Producer");
                            for (int i = 0; i < 5; i++) {
                                logger.info("Send message {}", i);
                                vertx.eventBus()
                                        .send("test-address", new JsonObject().put("text", "hello " + i));
                            }
                        }
                    });
                });
        latch.await();
    }

    @Test
    public void blocking_worker_verticle() throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        final CountDownLatch latch = new CountDownLatch(5);
        vertx.deployVerticle(() -> new BlockingWorkerVerticle(latch),
                new DeploymentOptions().setWorker(true).setWorkerPoolName("work-pool").setWorkerPoolSize(5).setInstances(4),
                idDeployedVerticle -> {
                    vertx.deployVerticle(new AbstractVerticle() {
                        @Override
                        public void start() {
                            logger.info("Standard Verticle Producer");
                            for (int i = 0; i < 5; i++) {
                                logger.info("Send message {}", i);
                                vertx.eventBus()
                                        .send("test-address", new JsonObject().put("text", "hello " + i));
                            }
                        }
                    });
                });
        latch.await();
    }

    private class BlockingWorkerVerticle extends AbstractVerticle {
        private final CountDownLatch latch;

        public BlockingWorkerVerticle(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void start() {
            logger.info("Worker Verticle Consumer");
            vertx.eventBus()
                    .<JsonObject>consumer("test-address", event -> {
                        logger.info("Got event: {}", event.body().getString("text"));
                        Sleeper.sleep(1000);
                        latch.countDown();
                    });
        }
    }
}