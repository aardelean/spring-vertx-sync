package home.spring.vertx.sync.services;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import home.spring.vertx.sync.endpoint.Endpoint;
import home.spring.vertx.sync.endpoint.Paths;
import home.spring.vertx.sync.endpoint.VertxEndpoint;
import home.spring.vertx.sync.rest.RestClient;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Created by alex on 9/30/2015.
 */
@VertxEndpoint(path= Paths.EXTERNAL_API)
@Service
public class FacadeService implements Endpoint{
    @Autowired
    private RestClient restClient;


    private static final String targetUrl = "http://localhost:9090/json";

    @Override
    @Suspendable
    public void processRequest(Message<Object> request) throws UnsupportedEncodingException, SuspendExecution {
        request.reply((restClient.get(targetUrl, String.class)));
    }
}
