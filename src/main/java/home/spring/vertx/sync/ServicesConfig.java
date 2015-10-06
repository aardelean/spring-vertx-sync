package home.spring.vertx.sync;

import home.spring.vertx.sync.services.ComplexService;
import home.spring.vertx.sync.services.EmployeeService;
import home.spring.vertx.sync.services.JsonService;
import home.spring.vertx.sync.services.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by alex on 10/2/2015.
 */
@Configuration
public class ServicesConfig {
    @Bean
    public JsonService simpleService(){
        return new JsonService();
    }
    @Bean
    public PersonService personService(){
        return new PersonService();
    }
    @Bean
    public EmployeeService employeeService(){
        return new EmployeeService();
    }

    @Bean
    public ComplexService complexService(){
        return new ComplexService();
    }
}
