package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private StudentRepository repository;

    @GetMapping("/students")
    public List<Student> test(@RequestParam String klass, @RequestParam String tahis) {
        ArrayList<Student> students = new ArrayList<>();

        for (Student student : repository.findAll()) {
            students.add(student);
        }

        return students;
    }
}
