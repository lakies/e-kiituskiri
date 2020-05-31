package com.vhk.kirjad.utils.email;
import org.springframework.stereotype.Component;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Properties;

@Component
public class EmailCredentialProvider {
    private final String username = "adrian@colleduc.ee";
    private final String address = "adrian@colleduc.ee";

    public String getUsername() {
        return username;
    }

    public Properties getProperties() {
        Properties properties = new Properties();

//        properties.put("mail.smtp.auth", true);
//        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "kiire.colleduc.ee");
        properties.put("mail.smtp.port", "25");
//        properties.put("mail.smtp.ssl.trust", "smtp.zoho.com");

        return properties;
    }

    public InternetAddress getInternetAddress() {
        try {
            return new InternetAddress(address);
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }
}