package home.spring.vertx.sync.endpoint.direct;

import io.vertx.core.eventbus.Message;

/**
 * Created by alex on 9/27/2015.
 */
public interface Endpoint {
    void processGet(Message<Object> request) throws Exception;

    default void processPost(Message<Object> request) throws Exception{

    }
}
