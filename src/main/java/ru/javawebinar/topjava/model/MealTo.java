package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;

public class MealTo {
    private final Integer uuid;
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final boolean excess;

    public MealTo(Integer uuid, LocalDateTime dateTime, String description, int calories, boolean excess) {
        this.uuid = uuid;
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    public MealTo(LocalDateTime dateTime, String description, int calories, boolean excess) {
        this(null, dateTime, description, calories, excess);
    }

    public Integer getUuid() {
        return uuid;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public boolean isExcess() {
        return excess;
    }

    @Override
    public String toString() {
        return "MealTo{" +
                "uuid=" + uuid +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess +
                '}';
    }
}
