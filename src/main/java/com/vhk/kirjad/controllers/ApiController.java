package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import com.vhk.kirjad.utils.LetterParams;
import com.vhk.kirjad.utils.email.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private MailSender mailSender;

    @GetMapping("/students")
    public List<Student> test(@RequestParam String klass, @RequestParam String tahis) {
        return new ArrayList<>(repository.findAllByKlass(klass + tahis));
    }

    @PostMapping("/sendmail")
    public String sendEmail(@RequestBody LetterParams params) throws InterruptedException, MessagingException, IOException {
        mailSender.sendEmail(params);
        return "ok";
    }

}
