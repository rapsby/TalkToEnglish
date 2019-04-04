package com.o2o.action.server.rest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.o2o.action.server.app.ShoppingApp;
import com.o2o.action.server.repo.CategoryRepository;
import com.o2o.action.server.repo.ChannelRepository;
import com.o2o.action.server.repo.ScheduleRepository;

@RestController
public class ShoppingController {
	private final ShoppingApp shoppingApp;

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;

	public ShoppingController() {
		shoppingApp = new ShoppingApp();
	}

	@RequestMapping(value = "/shopping", method = RequestMethod.POST)
	public @ResponseBody String processActions(@RequestBody String body, HttpServletRequest request,
			HttpServletResponse response) {
		String jsonResponse = null;
		shoppingApp.setCategoryRepository(categoryRepository);
		shoppingApp.setChannelRepository(channelRepository);
		shoppingApp.setScheduleRepository(scheduleRepository);

		try {
			System.out.println("request : " + body + "," + categoryRepository);
			jsonResponse = shoppingApp.handleRequest(body, getHeadersMap(request)).get();
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
