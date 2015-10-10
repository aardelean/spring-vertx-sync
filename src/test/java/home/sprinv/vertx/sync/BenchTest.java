package home.sprinv.vertx.sync;

import home.spring.vertx.sync.rest.RestClient;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

/**
 * Created by alex on 10/6/2015.
 */
public class BenchTest {

    private static final String RESULT_BODY="\"{name: 'Max, lastname: 'Mustermann', occupation: 'developer'}\"";

    private RestClient restClient;

    @Before
    public void setup() throws NoSuchAlgorithmException, KeyManagementException {
        restClient = new RestClient();
        restClient.setUp();
    }

    @Test
    public void testJson() throws UnsupportedEncodingException, ExecutionException, InterruptedException {
        restClient.targetWithParams("http://localhost:8080/json/check").request().async().get(String.class);
    }
}
