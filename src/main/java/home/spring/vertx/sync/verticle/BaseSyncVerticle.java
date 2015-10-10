package home.spring.vertx.sync.verticle;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.Future;
import io.vertx.ext.sync.SyncVerticle;

/**
 * Created by alex on 9/28/2015.
 */
public class BaseSyncVerticle extends SyncVerticle {

    @Override
    @Suspendable
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

    }

    @Override
    @Suspendable
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }

    @Override
    @Suspendable
    public void start() throws Exception {
        super.start();
        System.out.println("SYNC VERTICLE DEPLOYED");
    }

    @Override
    @Suspendable
    public void stop() throws Exception {
        super.stop();
    }


}
