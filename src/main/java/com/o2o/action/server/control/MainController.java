package com.o2o.action.server.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	@GetMapping("/")
	public String mainIndex(Model model) {
		System.out.println("Haha.");

		return "index";
	}

	@GetMapping("/manager")
	public String managerIndex(Model model) {
		return "manager";
	}

	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}

	@GetMapping("/junghyo")
	public String junghyo(Model model) {
		return "junghyo";
	}
}
