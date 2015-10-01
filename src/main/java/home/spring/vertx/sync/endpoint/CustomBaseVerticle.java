package home.spring.vertx.sync.endpoint;

/**
 * Created by alex on 9/30/2015.
 */

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.sync.SyncVerticle;

import static io.vertx.ext.sync.Sync.awaitResult;
import static io.vertx.ext.sync.Sync.fiberHandler;

/**
 *
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class CustomBaseVerticle extends SyncVerticle {

    private static final String ADDRESS = "some-address";

    /*
    Convenience method so you can run it in your IDE
    Note if in IDE need to edit run settings and add:
    -javaagent:/home/tim/.m2/repository/co/paralleluniverse/quasar-core/0.7.2/quasar-core-0.7.2.jar
     */
    public static void main(String[] args) {
//        Consumer<Vertx> runner = vertx -> {
//            try {
//                vertx.deployVerticle(CustomBaseVerticle.class.getName());
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        };
//        Vertx vertx = Vertx.vertx();
//        runner.accept(vertx);
    }

    @Suspendable
    @Override
    public void start() throws Exception {

        EventBus eb = vertx.eventBus();
        eb.consumer(ADDRESS).handler(msg -> {
            System.out.println("Waiting");
            // reply after one second
            vertx.setTimer(1000, tid -> msg.reply("wibble"));
        });

        // If you want to do sync stuff in an async handler it must be transformed to a fiber handler
        vertx.createHttpServer().requestHandler(fiberHandler(req -> {

            // Send a message to address and wait for a reply
            Message<String> reply = awaitResult(h -> eb.send(ADDRESS, "blah", h));

            System.out.println("Got reply: " + reply.body());

            req.response().end("blah");

        })).listen(8080, "localhost");

    }

}
