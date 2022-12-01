package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.controller.MealController;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
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
        String action = req.getParameter("action");
        if (action == null) {
            action = "read_all";
        }

        Meal meal;
        switch (action) {
            case "delete":
                controller.delete(Integer.parseInt(req.getParameter("uuid")));
                resp.sendRedirect("meals");
                break;
            case "update":
                req.setAttribute("action", action);
                meal = controller.read(Integer.parseInt(req.getParameter("uuid")));
                req.setAttribute("meal", meal);
                req.getRequestDispatcher("/meal.jsp").forward(req, resp);
                break;
            case "create":
                req.setAttribute("action", action);
                meal = new Meal(null, LocalDateTime.now().withNano(0), "", 0);
                req.setAttribute("meal", meal);
                req.getRequestDispatcher("/meal.jsp").forward(req, resp);
                break;
            default:
                List<MealTo> mealToList = MealsUtil
                        .filteredByStreams(controller.readAll()
                                , LocalTime.of(0, 0), LocalTime.of(23, 59)
                                , CALORIES_PER_DAY);
                req.setAttribute("mealToList", mealToList);
                req.setAttribute("formatter", formatter);
                req.getRequestDispatcher("/meals.jsp").forward(req, resp);
                log.debug("forward to meals");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("make doPost");
        req.setCharacterEncoding("UTF-8");
        Integer uuid = null;
        try {
            uuid = Integer.parseInt(req.getParameter("uuid"));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));
        Meal meal = new Meal(uuid
                , dateTime
                , description
                , calories);
        String action = req.getParameter("action");
        if (action.equals("update")) {
            controller.update(meal);
        } else {
            controller.create(meal);
        }

        resp.sendRedirect("meals");
    }
}
