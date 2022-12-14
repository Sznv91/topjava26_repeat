package ru.javawebinar.topjava.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.javawebinar.topjava.MealTestData.OWNER;
import static ru.javawebinar.topjava.MealTestData.START_SEQ;

@ContextConfiguration({
        "classpath:/spring/spring-app.xml",
        "classpath:/spring/spring-db.xml"
})
@ActiveProfiles(profiles = "springJdbc")
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:/db/populateDB.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal actual = service.get(MealTestData.START_SEQ, OWNER);
        Meal expect = MealTestData.meals.get(0);
        Assert.assertEquals(expect, actual);
    }

    @Test(expected = NotFoundException.class)
    public void getByNotOwner() {
        service.get(START_SEQ, UserTestData.ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void delete() {
        service.delete(START_SEQ, OWNER);
        service.get(START_SEQ, OWNER);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByNotOwner() {
        service.delete(START_SEQ, UserTestData.ADMIN_ID);
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> expect = Stream.of(
                        MealTestData.meals.get(3),
                        MealTestData.meals.get(4),
                        MealTestData.meals.get(5),
                        MealTestData.meals.get(6)
                ).sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
        List<Meal> actual = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31), LocalDate.of(2020, Month.JANUARY, 31), OWNER);
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void getBetweenInclusiveNotOwner() {
        List<Meal> unexpect = Stream.of(
                        MealTestData.meals.get(3),
                        MealTestData.meals.get(4),
                        MealTestData.meals.get(5),
                        MealTestData.meals.get(6)
                ).sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
        List<Meal> actual = service.getBetweenInclusive(LocalDate.of(2020, Month.JANUARY, 31), LocalDate.of(2020, Month.JANUARY, 31), UserTestData.ADMIN_ID);
        Assert.assertNotEquals(unexpect, actual);
    }

    @Test
    public void getAll() {
        List<Meal> actual = service.getAll(OWNER);
        List<Meal> expect = MealTestData.meals
                .stream().sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void getAllByNotOwner() {
        List<Meal> actual = service.getAll(UserTestData.ADMIN_ID);
        List<Meal> unexpect = MealTestData.meals
                .stream().sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
        Assert.assertNotEquals(unexpect, actual);
    }

    @Test
    public void update() {
        Meal expect = MealTestData.meals.get(0);
        expect.setDescription("New Description");
        service.update(expect, OWNER);
        Meal actual = service.get(expect.getId(), OWNER);
        Assert.assertEquals(expect, actual);
    }

    @Test(expected = NotFoundException.class)
    public void updateByNotOwner() {
        Meal unexpect = MealTestData.meals.get(0);
        unexpect.setDescription("New Description");
        service.update(unexpect, UserTestData.ADMIN_ID);
    }

    @Test
    public void create() {
        Meal expect = MealTestData.getNew();
        Meal actual = service.create(expect, OWNER);
        expect.setId(MealTestData.END_SEQ);
        Assert.assertEquals(expect, actual);
    }
}