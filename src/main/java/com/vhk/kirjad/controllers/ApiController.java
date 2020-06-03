package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Kiitus;
import com.vhk.kirjad.jpa.KiitusRepository;
import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import com.vhk.kirjad.utils.LetterParams;
import com.vhk.kirjad.utils.email.MailSender;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ApiController {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private KiitusRepository kiitusRepository;

    @Autowired
    private MailSender mailSender;

    @GetMapping("/students")
    public Collection<Student> test(@RequestParam String klass, @RequestParam String tahis) {
        Collection<Student> allByKlass = repository.findAllByKlassOrderByPerekonnanimi(klass + tahis);
        return allByKlass;
    }

    @PostMapping("/sendmail")
    public String sendEmail(@RequestBody LetterParams params) throws InterruptedException, MessagingException, IOException {
        log.info(String.format("Request to send email: %s", params.toString()));

        Student student = repository.findById(params.getStudentId()).orElse(null);
        if (student == null) {
            throw new NotFoundException();
        }

        Kiitus kiitus = kiitusRepository.findByStudent(student);

        if (kiitus == null) {
            kiitus = new Kiitus();
        }

        kiitus.setStudent(student);
        
        mailSender.sendEmail(params);

        kiitus.setSaadetud(true);
        kiitus.setKiituskiri(params.getMsg());
        kiitus.setSaatja(params.getSignature());
        kiitusRepository.save(kiitus);

        return "ok";
    }

}
