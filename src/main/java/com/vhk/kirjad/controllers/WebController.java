package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import com.vhk.kirjad.utils.FileManager;
import com.vhk.kirjad.utils.LetterParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RequestMapping("/")
@Controller
@Slf4j
public class WebController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/kiituskiri")
    public String kiituskiri(LetterParams params, Model model) {
        Student student = studentRepository.findById(params.getStudentId()).orElse(null);

        if (student == null) {
            throw new NotFoundException();
        }

        model.addAttribute("student", student);

        model.addAttribute("msg", URLDecoder.decode(params.getMsg(), StandardCharsets.UTF_8)
                .replace("<", "%3C")
                .replace(">", "%3E")
                .replace("\n", "<br />"));

        String nimi = URLDecoder.decode(params.getSignature(), StandardCharsets.UTF_8);

        if (nimi.length() > 13) {
            nimi = nimi.replace("<", "%3C")
                    .replace(">", "%3E")
                    .replaceFirst(" ", "<br />");
        }

        model.addAttribute("signature", nimi);
        model.addAttribute("type", URLDecoder.decode(params.getType(), StandardCharsets.UTF_8));

        model.addAttribute("date", params.getDate());

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
