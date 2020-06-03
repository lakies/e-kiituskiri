package com.vhk.kirjad.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

@Slf4j
@Component
public class FileManager {

    @Autowired
    private HttpServletRequest request;

    public File createPng(File pdf) throws InterruptedException, IOException {
        String convert = String.format("convert -density 150 %s -quality 90 %s.png", pdf.getAbsolutePath(), pdf.getAbsolutePath());

        Runtime runtime = Runtime.getRuntime();
        runtime.exec(convert).waitFor();
        File file = new File(pdf.getAbsolutePath() + ".png");

        log.info(String.format("Png %s created", file.getName()));

        return file;
    }

    public File createPdf(LetterParams params) throws InterruptedException, IOException {
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

        String htmltopdf = String.format("wkhtmltopdf " +
                        "--disable-smart-shrinking" +
                        " -L 0 -R 0 -B 0 -T 0 --page-size A5" +
                        " --cookie accessJwt %s " +
                        "\"http://localhost:8080/kiituskiri?studentId=%s&msg=%s&signature=%s&type=%s&date=%s\" " +
                        "%s.pdf", accessCookie.getValue(),
                URLEncoder.encode(params.getStudentId(), StandardCharsets.UTF_8),
                URLEncoder.encode(params.getMsg(), StandardCharsets.UTF_8),
                URLEncoder.encode(params.getSignature(), StandardCharsets.UTF_8),
                URLEncoder.encode(params.getType(), StandardCharsets.UTF_8),
                URLEncoder.encode(params.getDate(), StandardCharsets.UTF_8),
                filepath);

        Runtime runtime = Runtime.getRuntime();
        runtime.exec(htmltopdf).waitFor();

        File pdf = new File(filepath + ".pdf");


        log.info(String.format("Pdf %s created", pdf.getName()));

        return pdf;
    }
}
