package pl.guz.vertx.m1.context;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextTest {

    private final Logger logger = LoggerFactory.getLogger(ContextTest.class);

    @Test
    public void should_return_null_context() {
        Vertx vertx = Vertx.vertx();
        logger.info("Current context is {}", Vertx.currentContext());
    }

    @Test
    public void should_get_the_same_context() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AbstractVerticle() {
            public void start() {
                logger.info("Current context is {}", Vertx.currentContext());
                logger.info("Verticle context is " + context);
            }
        });
    }

    @Test
    public void should_get_the_same_context1() {
        Vertx vertx = Vertx.vertx();
        Context context = vertx.getOrCreateContext();
        logger.info("Current context is {}", Vertx.currentContext());
        context.runOnContext(event -> {
            logger.info("Current context is {}", Vertx.currentContext());
        });
    }

    @Test
    public void should_get_the_same_context2() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new AbstractVerticle() {
            private int numberOfFiles;

            public void start() {
                Context context = Vertx.currentContext();
                logger.info("Running with context : {}", Vertx.currentContext());

                Thread thread = new Thread() {
                    public void run() {
                        // No context here!
                        logger.info("Current context : {}", Vertx.currentContext());

                        int n = getNumberOfFiles();
                        context.runOnContext(v -> {

                            // Runs on the same context
                            logger.info("Runs on the original context : {}" , Vertx.currentContext());
                            numberOfFiles = n;
                        });
                    }
                };

                thread.start();
            }
        });
    }

    private int getNumberOfFiles() {
        return 10; // Assuming this block
    }
}
