package home.spring.vertx.sync.services;

import io.advantageous.qbit.annotation.RequestMapping;
import io.advantageous.qbit.annotation.RequestMethod;

/**
 * Created by alex on 10/2/2015.
 */
@RequestMapping("/json")
public class JsonService {

    private static final  String responseString = "{name: 'Max, lastname: 'Mustermann', occupation: 'developer'}";

    @RequestMapping(value = "/check",  method = RequestMethod.GET)
    public String check(){
        return responseString;
    }
}
