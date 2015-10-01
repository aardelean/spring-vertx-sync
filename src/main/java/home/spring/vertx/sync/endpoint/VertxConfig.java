package home.spring.vertx.sync.endpoint;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Created by alex on 9/27/2015.
 */
@Configuration
public class VertxConfig implements ApplicationContextAware{

    public static AnnotationConfigApplicationContext ctx;

    @Autowired
    private HttpServer httpServer;

    @Autowired
    private Vertx vertx;

    @Bean
    public Vertx vertx(){
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);
        return vertx;
    }

    @Bean
    public CustomBaseVerticle customBaseVerticle(){
        return new CustomBaseVerticle();
    }

    @Bean
    public Router router(Vertx vertx){
        return Router.router(vertx);
    }

    @PostConstruct
    public void gameOn(){
        vertx.deployVerticle(JsonVerticle.class.getName(), new DeploymentOptions().setInstances(8));
        httpServer.listen(8080);
    }

    @Bean
    public HttpServer server(Router router, Vertx vertx){
        HttpServer server = vertx.createHttpServer().requestHandler(router::accept);
        server.requestHandler(req -> {
            if (req.method() == HttpMethod.GET) {
                if(req.path().equals(Paths.FILE.getValue())){
                    req.response().sendFile("index.html");
                }else {
                    req.response().setChunked(true);
                    delegateToBus(req, vertx.eventBus());
                }
            } else {
                // We only support GET for now
                req.response().setStatusCode(405).end();
            }
        });
        return server;
    }

    public void delegateToBus(HttpServerRequest req, EventBus eventBus){
        Paths operations = Paths.getByValue(req.path());
        eventBus.<String>send(operations.getValue(), "", result -> {
            checkResult(req, result);
        });
    }
    private void checkResult(HttpServerRequest req, AsyncResult<Message<String>> result){
        if (result.succeeded()) {
            req.response().setStatusCode(200).write(result.result().body()).end();
        } else {
            req.response().setStatusCode(500).write(result.cause().toString()).end();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = (AnnotationConfigApplicationContext)applicationContext;
    }

////    @PostConstruct
//    public void deployHandlers(){
//        Map<String, Object> handlers = ctx.getBeansWithAnnotation(VertxEndpoint.class);
//        EventBus eventBus = vertx.eventBus();
//        for(Map.Entry<String, Object> handlerEntry : handlers.entrySet()){
//            VertxEndpoint vertxSetting= handlerEntry.getValue().getClass().getAnnotation(VertxEndpoint.class);
//            if (Endpoint.class.isAssignableFrom(handlerEntry.getValue().getClass())) {
//                Endpoint endpointService = (Endpoint) handlerEntry.getValue();
//                if(vertxSetting.blocking()){
//                    eventBus.consumer(vertxSetting.path().getValue()).handler(blockingHandler(endpointService));
//                }else {
//                    eventBus.consumer(vertxSetting.path().getValue()).handler(msg -> {
//                        try {
//                            endpointService.processRequest(msg);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            msg.reply(e.getCause());
//                        }
//                    });
//                }
//            }
//        }
//    }
//
//    private Handler<Message<Object>> blockingHandler(Endpoint service) {
//        return msg -> vertx.<Object>executeBlocking(future -> {
//                    try {
//                        service.processRequest(msg);
//                        future.complete(msg.body());
//                    } catch (Exception e) {
//                        future.fail(e);
//                    }
//                },false,
//                result -> {
//                    if (result.succeeded()) {
//                        msg.reply(result.result());
//                    } else {
//                        msg.reply(result.cause().toString());
//                    }
//                });
//    }
}
