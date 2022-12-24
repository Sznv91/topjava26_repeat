package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {
    private final CrudMealRepository crudMealRepository;
    private final CrudUserRepository crudUserRepository;

    public DataJpaMealRepository(CrudMealRepository crudMealRepository, CrudUserRepository crudUserRepository) {
        this.crudMealRepository = crudMealRepository;
        this.crudUserRepository = crudUserRepository;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public Meal save(Meal meal, int userId) {
        User user = crudUserRepository.getReferenceById(userId);
        if (meal.isNew()) {
            meal.setUser(user);
            return crudMealRepository.save(meal);
        } else {
            assert meal.getId() != null;
            Meal mealFromDb = crudMealRepository.findById(meal.getId()).orElse(null);
            if (mealFromDb != null && mealFromDb.getUser().getId().equals(userId)) {
                meal.setUser(user);
                return crudMealRepository.save(meal);
            }
        }
        return null;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal mealFromDb = crudMealRepository.findById(id).orElse(null);
        if (mealFromDb != null && mealFromDb.getUser().getId().equals(userId)) {
            crudMealRepository.deleteById(id);
            return true;
        } else return false;
    }

    @Override
    public Meal get(int id, int userId) {
        return crudMealRepository.findMealByIdAndUserId(id, userId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudMealRepository.findAllByUserIdOrderByDateTimeDesc(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return crudMealRepository.findAllByUserIdAndDateTimeIsAfterAndDateTimeBeforeOrderByDateTimeDesc(userId, startDateTime, endDateTime);
    }
}
