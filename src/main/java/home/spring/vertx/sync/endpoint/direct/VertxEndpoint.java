package home.spring.vertx.sync.endpoint.direct;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Created by alex on 9/27/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
@Service
public @interface VertxEndpoint{
    Paths path();
    boolean blocking() default false;
    boolean file() default false;
}
