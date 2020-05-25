package com.vhk.kirjad.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("main");
        registry.addViewController("/").setViewName("main");
        registry.addViewController("/error").setViewName("login");
//        registry.addViewController("/login").setViewName("login");
    }

}