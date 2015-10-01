package home.spring.vertx.sync.services;

import home.spring.vertx.sync.endpoint.Endpoint;
import home.spring.vertx.sync.endpoint.Paths;
import home.spring.vertx.sync.endpoint.VertxEndpoint;
import io.vertx.core.eventbus.Message;


@VertxEndpoint(path= Paths.JSON)
public class SimpleService implements Endpoint{

    private static final  String responseString = "{name: 'Max, lastname: 'Mustermann', occupation: 'developer'}";
    @Override
    public void processRequest(Message<Object> request) {
        request.reply(responseString);
    }
}
