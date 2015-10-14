package home.spring.vertx.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by alex on 10/1/2015.
 */
//@FiberSpringBootApplication
@SpringBootApplication
//@Import(FiberWebMvcConfigurationSupport.class)
public class Starter {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Starter.class, args);
    }
}
