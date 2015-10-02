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
    private ServiceEndpointServer serviceEndpointServer;

    @Autowired
    private HttpServer server;

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
        serviceEndpointServer.start();
        server.listen(8080);
    }

    @Bean
    public HttpServer server(Router router, Vertx vertx){
        HttpServer server = vertx.createHttpServer().requestHandler(router::accept);
        server.requestHandler(req -> {
            if (req.method() == HttpMethod.GET) {
                if (req.path().equals(Paths.FILE.getValue())) {
                    req.response().sendFile("index.html");
                } else {
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

    @Bean
    public io.advantageous.qbit.http.server.HttpServer qbitServer(HttpServer server){
        return VertxHttpServerBuilder.vertxHttpServerBuilder()
                .setRoute(qbitRoute)
                .setHttpServer(server)
                .setVertx(vertx)
                .build();
    }

    @Bean
    public ServiceEndpointServer serviceEndpointServer(io.advantageous.qbit.http.server.HttpServer qbitServer){
        return EndpointServerBuilder.endpointServerBuilder()
                       .setHttpServer(qbitServer)
                       .setUri("/")
                       .addServices(new SimpleService())
                       .build();
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
}
