package home.spring.vertx.sync.endpoint.direct;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.Lists;
import home.spring.vertx.sync.dao.CommentDao;
import home.spring.vertx.sync.entities.Comment;
import io.advantageous.boon.json.ObjectMapper;
import io.advantageous.boon.json.implementation.ObjectMapperImpl;
import io.vertx.core.eventbus.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alex on 10/11/2015.
 */
@VertxEndpoint(path = Paths.COMMENT)
public class CommentHandler implements Endpoint{

    @Autowired
    private CommentDao commentDao;

    private ObjectMapper objectMapper = new ObjectMapperImpl();

    @Override
    @Suspendable
    public void processGet(Message<Object> request) throws Exception {
        Iterable<Comment> comments = commentDao.findAll();
        request.reply(objectMapper.writeValueAsString(Lists.newArrayList(comments)));
        request.headers().add("ContentType", "application/json");
    }

    @Override
    @Suspendable
    public void processPost(Message<Object> request) throws Exception {
        Comment comment = objectMapper.fromJson(request.body().toString(), Comment.class);
        commentDao.save(comment);
    }
}
