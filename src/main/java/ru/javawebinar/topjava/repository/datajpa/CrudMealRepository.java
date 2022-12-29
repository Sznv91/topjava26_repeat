package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    //    @Query(name = Meal.ALL_SORTED)
    List<Meal> getAll(@Param("userId") int userId);

    List<Meal> getBetween(@Param("userId") int userId, @Param("startDateTime") LocalDateTime start, @Param("endDateTime") LocalDateTime end);

    @Transactional
    @Modifying
    int delete(@Param("id") int id, @Param("userId") int userId);

    Meal findMealByIdAndUserId(int id, int userId);

    @Transactional
    @Modifying
    @Query("UPDATE Meal m SET m.calories=:calories, m.dateTime=:date_time, m.description=:description, m.user.id=:user_id WHERE m.id=:id AND m.user.id=:user_id")
    int updateMealByUserId(@Param("calories") int calories, @Param("date_time") LocalDateTime dateTime, @Param("description") String description, @Param("id") int id, @Param("user_id") int userId);

    @Query("SELECT m FROM Meal m JOIN FETCH m.user u JOIN FETCH u.roles WHERE m.id=:id AND m.user.id=:user_id")
    Meal getWithUser(@Param("id") int id, @Param("user_id") int userId);
}