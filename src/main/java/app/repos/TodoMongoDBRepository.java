package app.repos;

import app.model.Todo;
import org.springframework.data.repository.CrudRepository;

//Spring Data for Mongo DB
public interface TodoMongoDBRepository extends CrudRepository<Todo, String> {
}
