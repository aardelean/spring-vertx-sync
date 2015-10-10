package home.spring.vertx.sync.endpoint.direct;

import io.advantageous.qbit.annotation.Service;
import io.vertx.core.eventbus.Message;

/**
 * Created by alex on 10/10/2015.
 */
@Service
@VertxEndpoint(path = Paths.JSON)
public class DirectJsonHandler implements Endpoint{

    private static final  String responseString = "{name: 'Max, lastname: 'Mustermann', occupation: 'developer'}";
    @Override
    public void processRequest(Message<Object> request) throws Exception {
        request.reply(responseString);
    }
}
