package home.spring.vertx.sync.endpoint.direct;

import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import home.spring.vertx.sync.dao.EmployeeDao;
import home.spring.vertx.sync.entities.Employee;
import home.spring.vertx.sync.rest.RestClient;
import io.vertx.core.eventbus.Message;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;

/**
 * Created by alex on 10/10/2015.
 */
@VertxEndpoint(path = Paths.COMPLEX)
public class ComplexHandler implements Endpoint {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private MongoDatabase mongoDatabase;

    @Autowired
    private RestClient restClient;

    @Value("${externalUrl}")
    private String externalUrl;

    public int index = 75000;

    private final static String id="eefa89c4-ec21-11e4-b08b-b75697636679-8e488775";

    private ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public void processGet(Message<Object> request) throws Exception {
        request.reply(combinatedOperation());
    }
    public String combinatedOperation() throws Exception {
        Employee employee = employee();
        CompletableFuture futureResult = person();
        String response = response();
        double calculated  = calculate();
        return Double.toString(calculated)
                + futureResult.get()
                + objectMapper.writeValueAsString(employee)
                +  response;
    }

    @Suspendable
    public Employee employee() throws SuspendExecution{
        return employeeDao.findOne(1l);
    }

    public CompletableFuture<String> person() throws Exception {
        CompletableFuture<String> mongoResult = new CompletableFuture();
        MongoCollection<Document> collection = mongoDatabase.getCollection("Person");
        collection.find().filter(Filters.eq("_id", id)).first((p, throwable) ->mongoResult.complete(((Document) p).toJson()));
        return mongoResult;
    }

    @Suspendable
    public String response()  throws SuspendExecution, UnsupportedEncodingException {
        return restClient.get(externalUrl, String.class);
    }

    public double calculate(){
        double[] resultVal = new double[100_000];
        for(int i=0; i<100_000;i++){
            resultVal[i] = Math.sqrt(i*i+123.4);
        }

        return resultVal[index];
    }
}
