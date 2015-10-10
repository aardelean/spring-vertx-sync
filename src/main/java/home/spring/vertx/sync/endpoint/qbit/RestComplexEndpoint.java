package home.spring.vertx.sync.endpoint.qbit;

import co.paralleluniverse.fibers.Suspendable;
import com.fasterxml.jackson.databind.ObjectMapper;
import home.spring.vertx.sync.dao.EmployeeDao;
import home.spring.vertx.sync.entities.Employee;
import home.spring.vertx.sync.entities.Person;
import home.spring.vertx.sync.rest.RestClient;
import io.advantageous.qbit.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;

/**
 * Created by alex on 10/3/2015.
 */
@RequestMapping("/complex")
public class RestComplexEndpoint {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private RestMongoEndpoint personService;

    @Autowired
    private RestClient restClient;

    @Value("${externalUrl}")
    private String externalUrl;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/check")
    public String complexShit() throws Exception {
        Employee employee = employee();
        Person person = person();
        String response = response();
        calculate();
        return "ok!";
    }

    @Suspendable
    public Employee employee(){
        return employeeDao.findOne(1l);
    }

    @Suspendable
    public Person person() throws Exception {
        return objectMapper.readValue(personService.processRequest().getBytes(),Person.class);
    }

    @Suspendable
    public String response() throws UnsupportedEncodingException {
        return restClient.get(externalUrl, String.class);
    }

    @Suspendable
    public void calculate(){
        double[] resultVal = new double[1000000];
        for(int i=0; i<100000;i++){
            resultVal[i] = Math.sqrt(i*i+123.4);
        }
    }

}
