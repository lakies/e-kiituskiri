package com.vhk.kirjad.utils.email;

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

@Slf4j
@Component
public class MailSender {

    @Autowired
    private EmailCredentialProvider credentialProvider;

    @Autowired
    private FileManager fileManager;

    public void sendEmail(LetterParams params) throws MessagingException, IOException, InterruptedException {
        Session session = Session.getInstance(credentialProvider.getProperties());

        Message message = new MimeMessage(session);
        message.setFrom(credentialProvider.getInternetAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("adriankirikal@gmail.com"));

        message.setSubject("Kiituskiri");

        Multipart multipart = new MimeMultipart();

        String htmlMessage = "<html>";
        htmlMessage += "<img src=\"cid:hpahvanptq23456ht34n\" />";
        htmlMessage += "</html>";

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(htmlMessage, "text/html");

        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.setHeader("Content-ID", "<hpahvanptq23456ht34n>");
        imagePart.setDisposition(MimeBodyPart.INLINE);

        File pdf = fileManager.createPdf(params);
        File png = fileManager.createPng(pdf);

        imagePart.attachFile(png);

        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(imagePart);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(pdf);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(pdf.getName());
        multipart.addBodyPart(attachmentPart);


        message.setContent(multipart);

        log.info(String.format("Sending email to %s", message.getAllRecipients()[0].toString()));

        try {
            send(message);

            log.info("Email sent");
        } catch (Exception e) {
            log.error("Email failed to send.");
            e.printStackTrace();
        } finally {
            pdf.delete();
            png.delete();
        }
    }

    private void send(Message message) throws MessagingException {
        Transport.send(message);
    }
}