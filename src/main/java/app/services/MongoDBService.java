package app.services;

import app.model.Todo;
import app.repos.TodoMongoDBRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


//@Service, indicates that it is a Spring-managed component
@Service
@Slf4j
public class MongoDBService {

    @Autowired
    @Getter
    private TodoMongoDBRepository repo;

    @PostConstruct
    void setUp() {
        log.info("+++ Mongo DB is populated");
        repo.deleteAll();

        for (int i = 1; i <= 8; i ++){
            Todo todo = new Todo();
            todo.setTitle("Todo number " + i);
            todo.setBody("Body number " + i);
            save(todo);
        }
    }

    public void save(Todo todo) {
        repo.save(todo);
    }

    public List<Todo> getAllItems() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void removeItems(List<Todo> toDosToRemove) {
        repo.deleteAll(toDosToRemove);
    }
}
