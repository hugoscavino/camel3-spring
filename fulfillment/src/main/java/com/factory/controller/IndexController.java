package com.factory.controller;

import com.factory.entity.BookEntity;
import com.factory.entity.EntityUtils;
import com.factory.model.Book;
import com.factory.repository.BookRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    private final BookRepository repository;

    public IndexController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {

        // add `message` attribute
        model.addAttribute("message", "Thank you for Ordering a Book.");

        // return view name
        return "index";
    }

    @GetMapping("/books-ordered")
    @ResponseBody
    public List<Book> booksOrdered() {
        List<Book> listOfBooks = new ArrayList<>();

        for (BookEntity bookEntity : repository.findAll()) {
            listOfBooks.add(EntityUtils.toEntityDto(bookEntity));
        }
        return  listOfBooks;
    }}
