package pl.guz.vertx.m2;

import java.util.concurrent.TimeUnit;

public class Sleeper {
    public static void sleep(int timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
