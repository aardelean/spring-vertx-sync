package home.spring.vertx.sync;

import home.spring.vertx.sync.endpoint.direct.Endpoint;
import home.spring.vertx.sync.endpoint.direct.Paths;
import home.spring.vertx.sync.endpoint.direct.VertxEndpoint;
import home.spring.vertx.sync.endpoint.qbit.RestComplexEndpoint;
import home.spring.vertx.sync.endpoint.qbit.RestJsonEndpoint;
import home.spring.vertx.sync.endpoint.qbit.RestMongoEndpoint;
import home.spring.vertx.sync.endpoint.qbit.RestMysqlEndpoint;
import home.spring.vertx.sync.verticle.BaseSyncVerticle;
import home.spring.vertx.sync.verticle.BaseVerticle;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import io.advantageous.qbit.vertx.http.VertxHttpServerBuilder;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Created by alex on 9/27/2015.
 */
@Configuration
public class VertxConfig {

    private static final  String responseString = "{name: 'Max, lastname: 'Mustermann', occupation: 'developer'}";

    @Autowired
    private ServiceEndpointServer serviceEndpointServer;

    @Autowired
    private HttpServer server;

    @Autowired
    private Vertx vertx;

    @Autowired
    private ApplicationContext ctx;

    @Value("${qbitEndpoint}")
    private boolean useQbit;

    @Value("${syncVerticle}")
    private boolean useSyncVerticle;


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

    @Bean
    public io.advantageous.qbit.http.server.HttpServer qbitServer(HttpServer server, Vertx vertx){
        return VertxHttpServerBuilder.vertxHttpServerBuilder()
                .setHttpServer(server)
                .setVertx(vertx)
                .build();
    }

    @Bean
    public ServiceEndpointServer serviceEndpointServer(io.advantageous.qbit.http.server.HttpServer qbitServer,
                                                       RestJsonEndpoint jsonService,
                                                       RestMysqlEndpoint employeeService,
                                                       RestMongoEndpoint personService,
                                                       RestComplexEndpoint complexService){
        return EndpointServerBuilder.endpointServerBuilder()
                       .setHttpServer(qbitServer)
                       .setUri("/qbit/")
                       .addServices(jsonService, employeeService, personService, complexService)
                       .build();
    }

    @PostConstruct
    public void gameOn(){
        Class<?> clazz = useSyncVerticle ? BaseSyncVerticle.class : BaseVerticle.class;
        vertx.deployVerticle(clazz.getName(), new DeploymentOptions().setInstances(16));
        if(useQbit) {
            serviceEndpointServer.start();
        }else{
            sendRequestsToBusConfiguration();
            consumeRequestsFromBusConfiguration();
        }
        server.listen(8080);
    }


    private void sendRequestsToBusConfiguration(){
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
    }

    /**
     *
     * @param req
     * @param eventBus
     */
    public void delegateToBus(HttpServerRequest req, EventBus eventBus){
        Paths operations = Paths.getByValue(req.path());
        eventBus.<String>send(operations.getValue(), "", result -> {
            checkResult(req, result);
        });
    }

    /**
     * After the request response cycle finished, the response is decorated with the appropriate error codes.
     * @param req
     * @param result
     */
    private void checkResult(HttpServerRequest req, AsyncResult<Message<String>> result){
        if (result.succeeded()) {
            req.response().setStatusCode(200).write(result.result().body()).end();
        } else {
            req.response().setStatusCode(500).write(result.cause().toString()).end();
        }
    }

    /**
     * All requests delegated to eventbus get consumed by the handlers identified here.
     */
    private void consumeRequestsFromBusConfiguration(){
        Map<String, Object> handlers = ctx.getBeansWithAnnotation(VertxEndpoint.class);
        EventBus eventBus = vertx.eventBus();
        for(Map.Entry<String, Object> handlerEntry : handlers.entrySet()){
            VertxEndpoint vertxSetting= handlerEntry.getValue().getClass().getAnnotation(VertxEndpoint.class);
            if (Endpoint.class.isAssignableFrom(handlerEntry.getValue().getClass())) {
                Endpoint endpointService = (Endpoint) handlerEntry.getValue();
                if(vertxSetting.blocking()){
                    eventBus.consumer(vertxSetting.path().getValue()).handler(blockingHandler(endpointService));
                }else {
                    eventBus.consumer(vertxSetting.path().getValue()).handler(msg -> {
                        try {
                            endpointService.processRequest(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            msg.reply(e.getCause());
                        }
                    });
                }
            }
        }
    }

    /**
     * Executes from normal verticle blocking operations, like mysql queries.
     * @param service
     * @return
     */
    private Handler<Message<Object>> blockingHandler(Endpoint service) {
        return msg -> vertx.<Object>executeBlocking(future -> {
                    try {
                        service.processRequest(msg);
                        future.complete(msg.body());
                    } catch (Exception e) {
                        future.fail(e);
                    }},
                false,
                result -> {
                    if (result.succeeded()) {
                        msg.reply(result.result());
                    } else {
                        msg.reply(result.cause().toString());
                    }
                });
    }
}
