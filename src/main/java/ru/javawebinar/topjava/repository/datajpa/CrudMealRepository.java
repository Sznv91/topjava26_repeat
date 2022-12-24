package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> findAllByUserIdOrderByDateTimeDesc(int userId);

    Meal findMealByIdAndUserId(int id, int userId);

    List<Meal> findAllByUserIdAndDateTimeIsAfterAndDateTimeBeforeOrderByDateTimeDesc(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
