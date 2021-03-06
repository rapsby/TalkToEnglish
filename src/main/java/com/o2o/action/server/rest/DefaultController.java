package com.o2o.action.server.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.o2o.action.server.app.English;
import com.o2o.action.server.app.English_k;
import com.o2o.action.server.app.English_y;
import com.o2o.action.server.app.DefaultApp;
import com.o2o.action.server.app.GogumaApp;
import com.o2o.action.server.app.MyTestApp;
import com.o2o.action.server.app.OnionApp;
import com.o2o.action.server.db.Category;
import com.o2o.action.server.repo.CategoryRepository;

@RestController
public class DefaultController {

	private final DefaultApp defaultApp;

	private final GogumaApp gogumaApp;
	
	private final MyTestApp myTestApp;
	
	private final OnionApp onionApp;
	
	private final English english;
	private final English_k english_k;
	private final English_y english_y;

	@Autowired
	private CategoryRepository categoryRepository;

	public DefaultController() {
		defaultApp = new DefaultApp();
		gogumaApp = new GogumaApp();
		myTestApp = new MyTestApp();
		onionApp = new OnionApp();
		english = new English();
		english_k = new English_k();
		english_y = new English_y();
		
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

	@Transactional
	@RequestMapping(value = "/api/1.0/category", method = RequestMethod.GET)
	public @ResponseBody Category getCategory(@RequestParam(value = "id", required = false) Long id) {
		if (id != null) {
			Category category = categoryRepository.findById(id).get();
			return category;
		}

		return null;
	}

	@Transactional
	@RequestMapping(value = "/api/1.0/category/child", method = RequestMethod.GET)
	public @ResponseBody List<Category> getCategory(@RequestParam(value = "parentId", required = false) Long id,
			@RequestParam(value = "keycode", required = false) String keycode) {
		List<Category> categories = null;

		if (id != null) {
			Category category = categoryRepository.findById(id).get();
			categories = category.getChildren();
		} else if (keycode != null) {
			List<Category> tmpCategories = categoryRepository.findByKeycodeOrderByDispOrderAsc(keycode);
			if (tmpCategories != null && tmpCategories.size() > 0) {
				categories = tmpCategories.get(0).getChildren();
			}
		} else {
			categories = categoryRepository.findByParentOrderByDispOrderAsc(null);
		}

		if (categories != null) {
			// for (Category category : categories) {
			// category.setChildren(null);
			// }

			return categories;
		}
		return null;
	}

	@RequestMapping(value = "/api/1.0/qrcode", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] generateQRCode(@RequestParam(value = "url", required = true) String url) {
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
	public @ResponseBody String processActions(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) {
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

    @RequestMapping(value = "/no1", method = RequestMethod.POST)
    public @ResponseBody
    String processNo1(@RequestBody String body, HttpServletRequest request,
                      HttpServletResponse response) {
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


	
	@RequestMapping(value = "/englishing", method = RequestMethod.POST)
	   public @ResponseBody String processEnglish(@RequestBody String body, HttpServletRequest request,
	         HttpServletResponse response) {
	      String jsonResponse = null;
	      try {
	         System.out.println("request : " + body + "," + categoryRepository);
	         jsonResponse = english_y.handleRequest(body, getHeadersMap(request)).get();
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
