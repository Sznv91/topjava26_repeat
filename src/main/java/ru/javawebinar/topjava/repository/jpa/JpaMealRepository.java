package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Meal save(Meal meal, int userId) {
        return null;
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        return em.createQuery("DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:user_id")
                .setParameter("id", id)
                .setParameter("user_id", userId)
                .executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
//        Meal meal = em.find(Meal.class, id);
//        Meal meal = em.createQuery("SELECT m FROM Meal m LEFT JOIN FETCH m.user WHERE m.id=:id", Meal.class).setParameter("id", id)/*.setParameter("user_id", userId)*/.getSingleResult();
        Meal meal = em.createQuery("SELECT m FROM Meal m WHERE m.id=:id AND m.user.id=:user_id", Meal.class).setParameter("id", id).setParameter("user_id", userId).getSingleResult();
        return meal;
        /*if (meal.getUser().getId().equals(userId)) {
            return meal;
        }*/
//        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return null;
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return null;
    }
}