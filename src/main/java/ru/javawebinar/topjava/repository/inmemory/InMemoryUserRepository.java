package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.AbstractNamedEntity;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, User> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    @Override
    public User save(User user) {
        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            repository.put(user.getId(), user);
            log.info("new user. ID: " + user.getId() +
                    ";Name: " + user.getName() +
                    ";Email: " + user.getEmail());
            return user;
        }
        log.info("update user {}", user);
        return repository.computeIfPresent(user.getId(), (id, oldUser) -> user);
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public List<User> getAll() { //sort by Name
        log.info("getAll");
        return repository.
                values().stream().
                sorted(Comparator.comparing(AbstractNamedEntity::getName)).collect(Collectors.toList());
    }

    @Override
    public User getByEmail(String email) {
        return repository.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .collect(Collectors.collectingAndThen(Collectors.toList()
                        , list -> {
                            if (list.size() != 1) {
                                throw new NotFoundException("User with email: " + email + " not found in DB");
                            }
                            log.info("getByEmail {}", email);
                            return list.get(0);
                        }));
    }
}
