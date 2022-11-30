package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Population;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.slf4j.LoggerFactory.getLogger;

public class InMemory implements MealDao {
    private static final Logger log = getLogger(InMemory.class);
    private static InMemory INSTANCE;

    private InMemory() {
    }

    public static InMemory getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new InMemory();
            INSTANCE.makePopulation();
        }
        return INSTANCE;
    }

    private void makePopulation() {
        log.debug("Populate InMemory DB");
        warehouse.addAll(Population.getMealList());
    }

    private final List<Meal> warehouse = new CopyOnWriteArrayList<>();

    public Meal create(Meal meal) {
        if (meal.getUuid() == null) {
            meal.setUuid(UUID.randomUUID().clockSequence());
        }
        warehouse.add(meal);
        return meal;
    }

    public Meal read(int uuid) {
        for (Meal meal : warehouse) {
            if (meal.getUuid() == uuid) {
                return meal;
            }
        }
        return null;
    }

    public List<Meal> readAll() {
        log.debug("readAll from InmemoryDB");
        return warehouse;
    }

    public Meal update(Meal meal) {
        for (Meal tempMeal :
                warehouse) {
            if (tempMeal.getUuid().equals(meal.getUuid())) {
                warehouse.remove(tempMeal);
                warehouse.add(meal);
                return meal;
            }
        }
        return null;
    }

    public boolean delete(int uuid) {
        for (Meal tempMeal :
                warehouse) {
            if (tempMeal.getUuid().equals(uuid)) {
                warehouse.remove(tempMeal);
                return true;
            }
        }
        return false;
    }
}
