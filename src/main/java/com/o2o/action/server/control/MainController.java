package com.o2o.action.server.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String mainIndex(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("/channel")
    public String channel(Model model) {
        return "channel";
    }
    
    @GetMapping("/product")
    public String product(Model model) {
        return "product";
    }
}
