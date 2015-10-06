package home.spring.vertx.sync;

import home.spring.vertx.sync.endpoint.BaseVerticle;
import home.spring.vertx.sync.services.ComplexService;
import home.spring.vertx.sync.services.EmployeeService;
import home.spring.vertx.sync.services.JsonService;
import home.spring.vertx.sync.services.PersonService;
import io.advantageous.qbit.server.EndpointServerBuilder;
import io.advantageous.qbit.server.ServiceEndpointServer;
import io.advantageous.qbit.vertx.http.VertxHttpServerBuilder;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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

    @Bean
    public Vertx vertx(){
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);
        return vertx;
    }

    @Bean
    public Router router(Vertx vertx){
        Router router = Router.router(vertx);
        return Router.router(vertx);
    }

    @Bean
    public HttpServer server(Router router, Vertx vertx){
        return vertx.createHttpServer().requestHandler(router::accept);
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
                                                       JsonService jsonService,
                                                       EmployeeService employeeService,
                                                       PersonService personService,
                                                       ComplexService complexService){
        return EndpointServerBuilder.endpointServerBuilder()
                       .setHttpServer(qbitServer)
                       .setUri("/")
                       .addServices(jsonService, employeeService, personService, complexService)
                       .build();
    }

    @PostConstruct
    public void gameOn(){
        vertx.deployVerticle(BaseVerticle.class.getName(), new DeploymentOptions().setInstances(16));
        serviceEndpointServer.start();
        server.listen(8080);
    }
}
