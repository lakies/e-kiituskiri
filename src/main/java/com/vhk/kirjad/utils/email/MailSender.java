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
import java.util.UUID;
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

        ArrayList<String> failedAddresses = new ArrayList<>();

        for (String email : emails) {

            if (email == null || "".equals(email)) {
                log.info("Skipping null email");
                continue;
            }

            Message message = new MimeMessage(session);
            String fromAddress = student.getKlass().endsWith("pmp") || student.getKlass().endsWith("pmt") ?
                    "pmk-tunnistus@pmk.edu.ee" : "vhk-tunnistus@vhk.ee";
            message.setFrom(credentialProvider
                    .getInternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

            message.setSubject(params.getType());

            Multipart multipart = new MimeMultipart("related");
            String cid = generateContentId("img");

            String htmlMessage = "<html><body>";
            htmlMessage += String.format("<img src=\"cid:%s\" />", cid);
            htmlMessage += "</body></html>";

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(htmlMessage, "text/html");

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setHeader("Content-ID", String.format("<%s>", cid));
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


            try {

                send(message);

                List<String> toAddresses = new ArrayList<>();
                Address[] recipients = message.getRecipients(Message.RecipientType.TO);
                for (Address address : recipients) {
                    toAddresses.add(address.toString());
                }

                log.info(String.format("Sent email from %s to %s", fromAddress, String.join(", ", toAddresses)));
            } catch (Exception e) {
                log.error(String.format("Email failed to send: %s", e.getMessage()));

                if (!"No recipient addresses".equals(e.getMessage()))
                    failedAddresses.add(email);
            }
        }


        if (failedAddresses.size() > 0) {
            Message message = new MimeMessage(session);
            String fromAddress = student.getKlass().endsWith("pmp") || student.getKlass().endsWith("pmt") ?
                    "pmk-tunnistus@pmk.edu.ee" : "vhk-tunnistus@vhk.ee";
            message.setFrom(credentialProvider
                    .getInternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("taniel@vhk.ee"));

            message.setSubject("Kirja saatmine ebaõnnestus");

            Multipart multipart = new MimeMultipart("related");
            String cid = generateContentId("img");

            String htmlMessage = "<html><body>";
            htmlMessage += String.format("<p>Õpilane: %s, pk: %s</p>", student.getEesnimi() + " " + student.getPerekonnanimi(), student.getId());
            htmlMessage += String.format("<p>Ebaõnnestunud aadressid: %s</p>", String.join(",", failedAddresses));
            htmlMessage += String.format("<img src=\"cid:%s\" />", cid);
            htmlMessage += "</body></html>";

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(htmlMessage, "text/html");

            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setHeader("Content-ID", String.format("<%s>", cid));
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

            log.info(String.format("Sending error email because the following addresses failed: %s", String.join(",", failedAddresses)));

            send(message);
        }



        pdf.delete();
        png.delete();

    }

    private void send(Message message) throws MessagingException {
        Transport.send(message);
    }

    String generateContentId(String prefix) {
        return String.format("%s-%s", prefix, UUID.randomUUID());
    }
}