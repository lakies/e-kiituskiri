package com.vhk.kirjad.utils.email;

import com.vhk.kirjad.controllers.NotFoundException;
import com.vhk.kirjad.jpa.Parent;
import com.vhk.kirjad.jpa.Student;
import com.vhk.kirjad.jpa.StudentRepository;
import com.vhk.kirjad.utils.FileManager;
import com.vhk.kirjad.utils.LetterParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MailSender {

    @Autowired
    private EmailCredentialProvider credentialProvider;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FileManager fileManager;

    public void sendEmail(LetterParams params) throws MessagingException, IOException, InterruptedException {
        Session session = Session.getInstance(credentialProvider.getProperties());

        Student student = studentRepository.findById(params.getStudentId()).orElse(null);
        if (student == null) {
            throw new NotFoundException();
        }

        List<String> emails = student.getParents().stream().map(Parent::getEmail).collect(Collectors.toList());
        emails.add(student.getEmail());

        File pdf = fileManager.createPdf(params);
        File png = fileManager.createPng(pdf);

        for (String email : emails) {

            Message message = new MimeMessage(session);
            message.setFrom(credentialProvider
                    .getInternetAddress(student.getKlass().endsWith("pmp") || student.getKlass().endsWith("pmt") ?
                            "pmk-tunnistus@pmk.edu.ee" : "vhk-tunnistus@vhk.ee"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

            message.setSubject(params.getType());

            Multipart multipart = new MimeMultipart();

            String htmlMessage = "<html>";
            htmlMessage += "<img src=\"cid:hpahvanptq23456ht34n\" />";
            htmlMessage += "</html>";

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(htmlMessage, "text/html");

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setHeader("Content-ID", "<hpahvanptq23456ht34n>");
            imagePart.setDisposition(MimeBodyPart.INLINE);

            imagePart.attachFile(png);

            multipart.addBodyPart(bodyPart);
            multipart.addBodyPart(imagePart);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(pdf);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(pdf.getName());
            multipart.addBodyPart(attachmentPart);


            message.setContent(multipart);

            log.info(String.format("Sending email from %s to %s", message.getFrom()[0].toString(), message.getAllRecipients()[0].toString()));

            try {
//                send(message);

                log.info("Email sent");
            } catch (Exception e) {
                log.error("Email failed to send.");
                e.printStackTrace();
            }
        }


        pdf.delete();
        png.delete();

    }

    private void send(Message message) throws MessagingException {
        Transport.send(message);
    }
}