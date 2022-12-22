package ru.javawebinar.topjava.repository.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
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
    @Transactional
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            User user = em.getReference(User.class, userId);
            meal.setUser(user);
            //EntityExistsException - if the entity already exists.
            em.persist(meal);
            return meal;
        } else {
            Meal fromDb = em.find(Meal.class, meal.getId());
            if (fromDb.getUser().getId().equals(userId)){
                meal.setUser(em.getReference(User.class, userId));
                return em.merge(meal);
            }
        }
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
        List<Meal> meals = em.createQuery(
                        "SELECT m FROM Meal m " +
                                "WHERE m.id=:id AND m.user.id=:user_id", Meal.class)
                .setParameter("id", id)
                .setParameter("user_id", userId).getResultList();
        if (meals.size() > 0) {
            return meals.get(0);
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return em.createQuery("SELECT m FROM Meal m WHERE m.user.id=:user_id ORDER BY m.dateTime DESC ", Meal.class)
                .setParameter("user_id", userId).getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return em.createQuery(
                        "SELECT m FROM Meal m WHERE m.user.id=:user_id AND m.dateTime >= :start_date_time AND m.dateTime < :end_date_time ORDER BY m.dateTime DESC"
                        , Meal.class)
                .setParameter("user_id", userId)
                .setParameter("start_date_time", startDateTime)
                .setParameter("end_date_time", endDateTime)
                .getResultList();
    }
}