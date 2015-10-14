package home.spring.vertx.sync.endpoint.direct;

import io.vertx.core.eventbus.Message;

/**
 * Created by alex on 10/10/2015.
 */
@VertxEndpoint(path = Paths.JSON)
public class JsonHandler implements Endpoint{

    private static final  String responseString = "{name: 'Max, lastname: 'Mustermann', occupation: 'developer'}";
    @Override
    public void processGet(Message<Object> request) throws Exception {
        request.reply(responseString);
    }
}
