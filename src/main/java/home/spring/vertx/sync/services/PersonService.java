package home.spring.vertx.sync.services;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import home.spring.vertx.sync.endpoint.Endpoint;
import home.spring.vertx.sync.endpoint.Paths;
import home.spring.vertx.sync.endpoint.VertxEndpoint;
import io.vertx.core.eventbus.Message;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alex on 9/27/2015.
 */
@VertxEndpoint(path= Paths.MONGO)
public class PersonService implements Endpoint{

    private final static String id="eefa89c4-ec21-11e4-b08b-b75697636679-8e488775";

    @Autowired
    private MongoDatabase mongoDatabase;

    @Override
    public void processRequest(Message<Object> request) throws Exception {
        MongoCollection<Document> collection = mongoDatabase.getCollection("Person");
        collection.find().filter(Filters.eq("_id", id)).first((p, throwable) ->request.reply(((Document) p).toJson()));
    }
}
