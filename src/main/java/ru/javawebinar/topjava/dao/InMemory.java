package ru.javawebinar.topjava.dao;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Population;

import java.util.Comparator;
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
        Population.getMealList().forEach(this::create);
    }

    private final List<Meal> warehouse = new CopyOnWriteArrayList<>();

    public Meal create(Meal meal) {
        if (meal.getUuid() == null) {
            meal.setUuid(getUuid());
        }
        for (Meal mealTemp : warehouse) {
            if (mealTemp.getUuid().equals(meal.getUuid())) {
                log.error("can't create meal.UUID=" + mealTemp.getUuid() + " Already exist.");
                throw new IllegalArgumentException("The meal with uuid " + mealTemp.getUuid() + " is already exist in the list.");
            }
        }
        warehouse.add(meal);
        counter = counter + 1;
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
        List<Meal> result = new CopyOnWriteArrayList<>(warehouse);
        result.sort(Comparator.comparing(Meal::getDateTime));
        return result;
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

    int counter = 0;

    private int getUuid() {
        return counter + 1;
    }
}
