package home.spring.vertx.sync.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import home.spring.vertx.sync.dao.EmployeeDao;
import home.spring.vertx.sync.endpoint.Endpoint;
import home.spring.vertx.sync.endpoint.Paths;
import home.spring.vertx.sync.endpoint.VertxEndpoint;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alex on 9/27/2015.
 */
@VertxEndpoint(path = Paths.MYSQL, blocking = true)
public class EmployeeService implements Endpoint {

    @Autowired
    private EmployeeDao employeeDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void processRequest(Message<Object> request) throws Exception{
        request.reply(objectMapper.writeValueAsString(employeeDao.findOne(1l)));
    }
}
