package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.controller.MealController;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.Population.CALORIES_PER_DAY;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    MealController controller = new MealController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("forward to meals");
        List<MealTo> mealToList = MealsUtil
                .filteredByStreams(controller.readAll()
                        , LocalTime.of(0, 0), LocalTime.of(23, 59)
                        , CALORIES_PER_DAY);
        req.setAttribute("mealToList", mealToList);
        req.setAttribute("formatter", formatter);
        req.getRequestDispatcher("/meals.jsp").forward(req, resp);
    }
}
