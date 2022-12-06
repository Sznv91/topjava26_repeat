package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal, int userId) {
        if (meal.getUserId().equals(userId)) {
            return repository.save(meal);
        }
        throw new NotFoundException("Meal doesn't belong to userId" + userId);
    }

    public boolean delete(int id, int userId) {
        Meal meal = repository.get(id);
        if (meal.getUserId().equals(userId)) {
            return repository.delete(id);
        }
        throw new NotFoundException("Meal doesn't belong to userId" + userId);
    }

    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        if (meal.getUserId().equals(userId)) {
            return meal;
        }
        throw new NotFoundException("Meal doesn't belong to userId" + userId);
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll().stream()
                .filter(meal -> meal.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}