package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController {

    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService service;

    @GetMapping
    public String get(Model model, HttpServletRequest request) {
        String action = request.getParameter("action");
        int userId = SecurityUtil.authUserId();

        switch (action == null ? "all" : action) {
            case "delete" -> {
                int id = Integer.parseInt(request.getParameter("id"));
                log.info("delete meal {} for user {}", id, userId);
                service.delete(id, userId);
                return "redirect:meals";
            }
            case "create", "update" -> {
                int id = -1;
                if (request.getParameter("id") != null && !request.getParameter("id").isEmpty()) {
                    id = Integer.parseInt(request.getParameter("id"));
                }

                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        service.get(id, userId);
                model.addAttribute("meal", meal);
                log.info(action + " meal {} for user {}", meal, userId);
                return "mealForm";
            }
            case "filter" -> {
                LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
                LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
                LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
                LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
                log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, userId);
                List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, userId);
                model.addAttribute("meals", MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime));
                return "meals";
            }

            default -> {
                log.info("getAll meal for user {}", userId);
                model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
                return "meals";
            }
        }
    }

    @PostMapping
    public String post(Model model, HttpServletRequest request) {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            meal.setId(Integer.parseInt(request.getParameter("id")));
            service.update(meal, SecurityUtil.authUserId());
            log.info("update for meal {} and user {}", meal.getId(), SecurityUtil.authUserId());
        } else {
            Meal createdMeal = service.create(meal, SecurityUtil.authUserId());
            log.info("create for meal {} and user {}", createdMeal, SecurityUtil.authUserId());
        }
        return "redirect:meals";
    }
}
