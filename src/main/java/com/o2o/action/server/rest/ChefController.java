package com.o2o.action.server.rest;

import com.google.actions.api.App;
import com.o2o.action.server.app.ChefApp;
import com.o2o.action.server.app.GogumaApp;
import com.o2o.action.server.db.MealDetail;
import com.o2o.action.server.db.MealDetail.MealType;
import com.o2o.action.server.db.MealMenu;
import com.o2o.action.server.repo.DateMenuRepository;
import com.o2o.action.server.repo.MealDetailRepository;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
public class ChefController {
    private final App goguma;
    private final ChefApp dchef;

    @Autowired
    private DateMenuRepository menuRepository;
    @Autowired
    private MealDetailRepository detailRepository;

    public ChefController() {
        goguma = new GogumaApp();
        dchef = new ChefApp();
    }

    private static <E> Collection<E> makeCollection(Iterable<E> iter) {
        Collection<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }

    @RequestMapping(value = "/simple", method = RequestMethod.POST)
    public @ResponseBody
    String simple(@RequestBody String body, HttpServletRequest request,
                  HttpServletResponse response) {
        String jsonResponse = null;
        try {
            System.out.println("request : " + body);
            jsonResponse = goguma.handleRequest(body, getHeadersMap(request)).get();
            System.out.println("response : " + jsonResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    @RequestMapping(value = "/dchef", method = RequestMethod.POST)
    public @ResponseBody
    String processDChef(@RequestBody String body, HttpServletRequest request,
                  HttpServletResponse response) {
        String jsonResponse = null;
        try {
            System.out.println("request : " + body);
            dchef.setMenuRepository(menuRepository);
            jsonResponse = dchef.handleRequest(body, getHeadersMap(request)).get();
            System.out.println("response : " + jsonResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    @RequestMapping(value = "/simple", method = RequestMethod.GET)
    public @ResponseBody
    String simpleGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Call!!");

        MealMenu menu = new MealMenu();
        menu.setServiceDate(Calendar.getInstance().getTime());
        menu.setDessert("디저트");
        menu.setMrice("잡곡밥");
        menu.setSalad1("셀러드1");
        menu.setSalad2("셀러드2");

        menu = menuRepository.save(menu);

        MealDetail detail = new MealDetail();
        detail.setMealMenu(menu);
        detail.setMealType(MealType.LAUNCH_1);
        detail.setMenu1("메뉴1");
        detail.setMenu2("메뉴2");
        detail.setMenu3("메뉴3");
        detail.setMenu4("메뉴4");

        detail = detailRepository.save(detail);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -10);

        Iterable<MealMenu> menus = menuRepository.findByServiceDateBetweenOrderByServiceDateAsc(c.getTime(),
                new Date());
        for (MealMenu dateMenu : menus) {
            System.out.println(dateMenu.getDessert());
        }
        return "Good";
    }

    @RequestMapping(value = "/api/1.0/mealmenus", method = RequestMethod.GET)
    public @ResponseBody
    Object getMealMenus(
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = ISO.DATE) Date fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = ISO.DATE) Date toDate,
            @RequestParam(value = "currentDate", required = false) @DateTimeFormat(iso = ISO.DATE) Date curDate,
            HttpServletRequest request, HttpServletResponse response) {
        Calendar c = Calendar.getInstance();
        if (curDate != null) {
            c.setTime(curDate);
            System.out.println(c.getTime().toString());
        }

        if (fromDate == null) {
            c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));
            fromDate = c.getTime();
        }
        fromDate = DateUtils.truncate(fromDate, Calendar.DATE);
        System.out.println(fromDate);
        if (toDate == null) {
            c.setTime(fromDate);
            c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));
            c.add(Calendar.DATE, 7);
            toDate = c.getTime();
        }
        toDate = DateUtils.truncate(toDate, Calendar.DATE);
        System.out.println(toDate);
        Iterable<MealMenu> menus = menuRepository.findByServiceDateBetweenOrderByServiceDateAsc(fromDate, toDate);

        return makeCollection(menus);
    }

    @RequestMapping(value = "/api/1.0/mealmenu", method = RequestMethod.GET)
    public @ResponseBody
    Object getMealMenu(@RequestParam(value = "currentDate") @DateTimeFormat(iso = ISO.DATE) Date curDate,
                       HttpServletRequest request, HttpServletResponse response) {
        if (curDate != null) {
            Iterable<MealMenu> menus = menuRepository.findByServiceDate(curDate);
            if (menus.iterator().hasNext()) {
                System.out.println("good");
                return menus.iterator().next();
            }
        }
        return null;
    }

    @RequestMapping(value = "/api/1.0/mealmenu", method = RequestMethod.PUT)
    public @ResponseBody
    Object updateMealMenu(@RequestBody MealMenu mealMenu,
                          HttpServletRequest request, HttpServletResponse response) {
        if (mealMenu != null) {
            MealMenu eMenus = menuRepository.findById(mealMenu.getId()).get();
            //TODO 이때 모든 값을 업데이트 하면 안되므로 추가 확인이 필요1
            if (eMenus != null) {
                eMenus.setSalad1(mealMenu.getSalad1());
                eMenus.setSalad2(mealMenu.getSalad2());
                eMenus.setMrice(mealMenu.getMrice());
                eMenus.setDessert(mealMenu.getDessert());
                menuRepository.save(eMenus).getId();
            } else {
                return null;
            }

            for (MealDetail mealDetail : mealMenu.getMeals()) {
                if (mealDetail.getId() == 0) {
                        mealDetail.setMealMenu(eMenus);
                        detailRepository.save(mealDetail);
                    } else {
                        MealDetail eMealDetail = detailRepository.findById(mealDetail.getId()).get();
                        //TODO 이때 모든 값을 업데이트 하면 안되므로 추가 확인이 필요2
                        if (eMealDetail != null) {
                            eMealDetail.setMenu1(mealDetail.getMenu1());
                            eMealDetail.setMenu2(mealDetail.getMenu2());
                            eMealDetail.setMenu3(mealDetail.getMenu3());
                            eMealDetail.setMenu4(mealDetail.getMenu4());
                            eMealDetail.setMenu5(mealDetail.getMenu5());
                            eMealDetail.setMenu6(mealDetail.getMenu6());
                            eMealDetail.setMenu7(mealDetail.getMenu7());
                        detailRepository.save(eMealDetail);
                    }
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/api/1.0/mealmenu", method = RequestMethod.POST)
    public @ResponseBody
    Object createMealMenu(@RequestBody MealMenu mealMenu,
                          HttpServletRequest request, HttpServletResponse response) {
        if (mealMenu != null) {
            List<MealDetail> tmpArray = new ArrayList<MealDetail>();
            tmpArray = mealMenu.getMeals();

            mealMenu.setMeals(null);
            MealMenu eMealMenu = menuRepository.save(mealMenu);

            for (MealDetail mealDetail : tmpArray) {
                mealDetail.setMealMenu(eMealMenu);
                detailRepository.save(mealDetail);
            }

            return eMealMenu.getId();
        }
        return null;
    }

    @RequestMapping(value = "/api/1.0/login", method = RequestMethod.POST)
    public void login(@RequestParam(value = "inputID", required = true) String id,
                      @RequestParam(value = "inputPassword", required = true) String passwd, HttpServletRequest request,
                      HttpServletResponse response) {
        if (id != null && passwd != null && id.length() > 0 && passwd.length() > 0) {
            if (id.trim().equalsIgnoreCase("admin") && passwd.trim().equalsIgnoreCase("1234")) {
                request.getSession().setAttribute("userId", "admin");
                try {
                    response.sendRedirect(request.getContextPath() + "/");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/api/1.0/logout", method = RequestMethod.POST)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession();
        if (httpSession != null) {
            httpSession.removeAttribute("userId");
        }
        try {
            response.sendRedirect(request.getContextPath() + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
