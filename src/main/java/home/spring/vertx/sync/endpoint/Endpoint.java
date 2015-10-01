package home.spring.vertx.sync.endpoint;

import io.vertx.core.eventbus.Message;

/**
 * Created by alex on 9/27/2015.
 */
public interface Endpoint {
    void processRequest(Message<Object> request) throws Exception;
}
