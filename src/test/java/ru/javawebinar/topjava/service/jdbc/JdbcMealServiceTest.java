package ru.javawebinar.topjava.service.jdbc;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

@ActiveProfiles(profiles = {"postgres", "jdbc"})
public class JdbcMealServiceTest extends AbstractMealServiceTest {
}
