package com.vhk.kirjad.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    private final static String PATH = "/error";
    @Override
    @RequestMapping(PATH)
    public String getErrorPath() {
        return "login";
    }

}