package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import com.vhk.kirjad.utils.FileManager;
import com.vhk.kirjad.utils.LetterParams;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@RequestMapping("/")
@Controller
public class WebController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/kiituskiri")
    public String kiituskiri(@RequestParam String studentId, @RequestParam String msg, @RequestParam String type, @RequestParam String date, @RequestParam String signature, Model model) {
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            throw new NotFoundException();
        }

        model.addAttribute("student", student);

        model.addAttribute("msg", URLDecoder.decode(msg, StandardCharsets.UTF_8)
                .replace("<", "%3C")
                .replace(">", "%3E")
                .replace("\n", "<br />"));

        model.addAttribute("signature", signature);

        model.addAttribute("type", URLDecoder.decode(type, StandardCharsets.UTF_8));

        model.addAttribute("date", date);

        return "kiituskiri";
    }

    @Autowired
    private FileManager fileManager;

    @GetMapping("/kiitus.png")
    public @ResponseBody
    byte[] getImage(LetterParams params) throws IOException, InterruptedException {

        File pdf = fileManager.createPdf(params);
        if (pdf == null) return null;

        File png = fileManager.createPng(pdf);

        byte[] bytes = Files.readAllBytes(png.toPath());

        pdf.delete();
        png.delete();

        return bytes;
    }

}
