package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String get(Model model) {
        int userId = SecurityUtil.authUserId();
        log.info("getAll meal for user {}", userId);
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping(value = "/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        int userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping(value = "/{id}/update")
    public String update(@PathVariable("id") int id, Model model) {
        Meal meal = service.get(id, SecurityUtil.authUserId());
        model.addAttribute("meal", meal);
        log.info("update meal {} for user {}", meal, SecurityUtil.authUserId());
        return "/mealForm";
    }

    @GetMapping(value = "/create")
    public String create(Model model) {
        Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
        model.addAttribute("meal", meal);
        log.info("create form");
        return "/mealForm";
    }

    @GetMapping(value = "/filter")
    public String filtered(Model model
            , @RequestParam("startDate") String startDateSt
            , @RequestParam("endDate") String endDateSt
            , @RequestParam("startTime") String startTimeSt
            , @RequestParam("endTime") String endTimeSt) {
        LocalDate startDate = parseLocalDate(startDateSt);
        LocalDate endDate = parseLocalDate(endDateSt);
        LocalTime startTime = parseLocalTime(startTimeSt);
        LocalTime endTime = parseLocalTime(endTimeSt);
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, SecurityUtil.authUserId());
        List<Meal> mealsDateFiltered = service.getBetweenInclusive(startDate, endDate, SecurityUtil.authUserId());
        model.addAttribute("meals", MealsUtil.getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime));
        return "/meals";
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
