package home.spring.vertx.sync;

import home.spring.vertx.sync.endpoint.direct.Paths;
import home.spring.vertx.sync.endpoint.direct.VertxEndpoint;
import home.spring.vertx.sync.verticle.BaseSyncVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static io.vertx.ext.sync.Sync.fiberHandler;

/**
 * Created by alex on 9/27/2015.
 */
@Configuration
public class VertxConfig {

    @Autowired
    private HttpServer server;

    @Autowired
    private Vertx vertx;

    @Autowired
    private ApplicationContext ctx;

    @Bean
    public Vertx vertx(){
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);
        return vertx;
    }

    @Bean
    public HttpServer server(Vertx vertx){
        HttpServer server = vertx.createHttpServer();
        return server;
    }

    @PostConstruct
    public void gameOn(){
        for(int i=0;i<16; i++){
            vertx.deployVerticle(new BaseSyncVerticle(ctx.getBeansWithAnnotation(VertxEndpoint.class)));
        }
        server.requestHandler(req -> {
            if (req.method() == HttpMethod.GET || req.method() == HttpMethod.POST) {
                Paths path = Paths.getByValue(req.path());
                if (path.isResource()) {
                    req.response().sendFile(req.path().substring(1));
                } else {
                    req.response().setChunked(true);
                    delegateToBus(req, vertx.eventBus(), req.method());
                }
            }else{
                req.response().setStatusCode(405).end();
            }
        }).listen(8080);
    }

     private void delegateToBus (HttpServerRequest req, EventBus eventBus, HttpMethod method){
         Paths operations = Paths.getByValue(req.path());
         String busName = method.name()+"_"+operations.getValue();
         if(method==HttpMethod.POST){
             req.bodyHandler(handler->{
                 sendToBus(req, eventBus, handler.toString(), busName);
             });
         }else{
             sendToBus(req, eventBus, "", busName);
         }
     }

    private void sendToBus(HttpServerRequest req, EventBus eventBus,String requestData, String busName){
        eventBus.<String>send(busName, requestData, fiberHandler(result -> {
            checkResult(req, result);
        }));
    }

     /**
      * After the request response cycle finished, the response is decorated with the appropriate error codes.
      * @param req
      * @param result
      */
     private void checkResult(HttpServerRequest req, AsyncResult <Message<String >> result){
         if (result.succeeded()) {
             req.response().setStatusCode(200).write(result.result().body()).end();
         } else {
             req.response().setStatusCode(500).write(result.cause().toString()).end();
         }
     }
 }
