package home.spring.vertx.sync.endpoint.direct;

import co.paralleluniverse.fibers.Suspendable;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.spring.vertx.sync.dao.EmployeeDao;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alex on 10/10/2015.
 */
@VertxEndpoint(path = Paths.MYSQL, blocking = true)
public class MysqlHandler implements Endpoint {

    @Autowired
    private EmployeeDao employeeDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Suspendable
    public void processGet(Message<Object> request) throws Exception{
        request.reply(objectMapper.writeValueAsString(employeeDao.findOne(1l)));
    }
}
