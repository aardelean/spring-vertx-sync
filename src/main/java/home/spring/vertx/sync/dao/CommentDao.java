package home.spring.vertx.sync.dao;

import home.spring.vertx.sync.entities.Comment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by alex on 9/20/2015.
 */
public interface CommentDao extends CrudRepository<Comment, Long> {
}
