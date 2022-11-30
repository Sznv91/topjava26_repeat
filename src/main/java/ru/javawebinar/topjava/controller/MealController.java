package ru.javawebinar.topjava.controller;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.InMemory;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.model.Meal;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealController {
    private static final Logger log = getLogger(MealController.class);
    MealDao dao = InMemory.getINSTANCE();

    public Meal create(Meal meal) {
        return dao.create(meal);
    }

    public Meal read(int uuid) {
        return dao.read(uuid);
    }

    public List<Meal> readAll() {
        log.debug("call MealController readAll");
        return dao.readAll();
    }

    public Meal update(Meal meal) {
        return dao.update(meal);
    }

    public boolean delete(int uuid) {
        return dao.delete(uuid);
    }

}
