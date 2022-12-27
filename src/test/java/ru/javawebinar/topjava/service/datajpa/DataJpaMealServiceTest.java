package ru.javawebinar.topjava.service.datajpa;

import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.service.AbstractMealServiceTest;

@ActiveProfiles(profiles = {"postgres", "datajpa"})
public class DataJpaMealServiceTest extends AbstractMealServiceTest {
}
