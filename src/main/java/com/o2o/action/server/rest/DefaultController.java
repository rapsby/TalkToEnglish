package com.o2o.action.server.rest;

import com.o2o.action.server.app.DefaultApp;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
public class DefaultController {
    private final DefaultApp dchef;

    @Autowired
    private CategoryRepository categoryRepository;

    public DefaultController() {
        dchef = new DefaultApp();
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

    @RequestMapping(value = "/api/1.0/category", method = RequestMethod.GET)
    public @ResponseBody
    List<Category> getCategory(@RequestParam(value = "parentId", required = false) Long id) {
        return categoryRepository.findByParent(id);
    }
}
