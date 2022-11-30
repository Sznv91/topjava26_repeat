package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    Meal create (Meal meal);
    Meal read (int uuid);
    List<Meal> readAll ();
    Meal update (Meal meal);
    boolean delete (int uuid);
}
