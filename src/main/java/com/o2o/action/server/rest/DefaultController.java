package com.o2o.action.server.rest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.o2o.action.server.app.DefaultApp;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class DefaultController {
    private final DefaultApp defaultApp;

    @Autowired
    private CategoryRepository categoryRepository;

    public DefaultController() {
        defaultApp = new DefaultApp();
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
        if (id == null)
            return categoryRepository.findByParentOrderByDispOrderAsc(null);

        Category category = categoryRepository.findById(id).get();
        if (category != null)
            return category.getChildren();
        return null;
    }

    @RequestMapping(value = "/api/1.0/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody
    byte[] generateQRCode(@RequestParam(value = "url", required = true) String url) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 720, 480);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "png", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public @ResponseBody
    String processActions(@RequestBody String body, HttpServletRequest request,
                          HttpServletResponse response) {
        defaultApp.setCategoryRepository(categoryRepository);
        String jsonResponse = null;
        try {
            System.out.println("request : " + body + "," + categoryRepository);
            jsonResponse = defaultApp.handleRequest(body, getHeadersMap(request)).get();
            System.out.println("response : " + jsonResponse);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jsonResponse;
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
