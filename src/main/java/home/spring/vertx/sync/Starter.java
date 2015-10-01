package home.spring.vertx.sync;

import home.spring.vertx.sync.endpoint.VertxConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Created by alex on 10/1/2015.
 */
@SpringBootApplication
@Import(VertxConfig.class)
public class Starter {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Starter.class, args);
//        final Channel<Integer> ch = Channels.newChannel(0);
//
//        new Fiber<Void>(() -> {
//            for (int i = 0; i < 10; i++) {
//                Strand.sleep(100);
//                ch.send(i);
//            }
//            ch.close();
//        }).start();
//
//        new Fiber<Void>(() -> {
//            Integer x;
//            while((x = ch.receive()) != null)
//                System.out.println("--> " + x);
//        }).start().join(); // join waits for this fiber to finish
    }
}
