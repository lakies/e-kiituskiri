package com.vhk.kirjad.controllers;

import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
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
    public String kiituskiri(@RequestParam String studentId, @RequestParam String msg, @RequestParam String signature, Model model) {
        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null) {
            throw new NotFoundException();
        }

        model.addAttribute("student", student);

        model.addAttribute("msg", msg);

        model.addAttribute("signature", signature);

        return "kiituskiri";
    }

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/kiituspng")
    public @ResponseBody
    byte[] getImage(@RequestParam String studentId, @RequestParam String msg, @RequestParam String signature) throws IOException, InterruptedException {

        Cookie accessCookie = Arrays.stream(request.getCookies()).filter(cookie -> "accessJwt".equals(cookie.getName())).findFirst().orElse(null);

        if (accessCookie == null) {
            return null;
        }

        File directory = new File("./tmp");
        if (! directory.exists()){
            directory.mkdir();
        }

        String filename = Long.toString(Instant.now().toEpochMilli());

        String filepath = String.format("%s%s%s", directory.getAbsolutePath(), File.separator, filename);

//        String requestURI = request.getRequestURI();

        String htmltopdf = String.format("wkhtmltopdf " +
                        "--disable-smart-shrinking" +
                        " -L 0 -R 0 -B 0 -T 0" +
                        " --cookie accessJwt %s " +
                        "\"http://localhost:8080/kiituskiri?studentId=%s&msg=%s&signature=%s\" " +
                        "%s.pdf", accessCookie.getValue(),
                URLEncoder.encode(studentId, StandardCharsets.UTF_8),
                URLEncoder.encode(msg, StandardCharsets.UTF_8),
                URLEncoder.encode(signature, StandardCharsets.UTF_8),
                filepath);

        Runtime runtime = Runtime.getRuntime();
        long l = System.currentTimeMillis();
        runtime.exec(htmltopdf).waitFor();

        System.out.println(System.currentTimeMillis() - l);

        String convert = String.format("magick -density 150 %s.pdf -quality 90 -resize 700x700 %s.png", filepath, filepath);
        l = System.currentTimeMillis();

        runtime.exec(convert).waitFor();
        System.out.println(System.currentTimeMillis() - l);
        File png = new File(filepath + ".png");

        byte[] bytes = Files.readAllBytes(png.toPath());

        new File(filepath + ".pdf").delete();
        png.delete();

        return bytes;
    }

}
