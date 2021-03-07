package com.merchant.controller;

import com.merchant.model.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute("book", new Book());
        model.addAttribute("message", "Thank you for visiting.");

        // return view name
        return "index";
    }
}
