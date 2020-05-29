package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private StudentRepository repository;

    @GetMapping("/students")
    public List<Student> test(@RequestParam String klass, @RequestParam String tahis) {
        return new ArrayList<>(repository.findAllByKlass(klass + tahis));
    }
}
