package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.mail.manager;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.MailConfiguration;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.util.*;

public class EmailSender {

    public static void send(
            String toAddress,
            String subject,
            String htmlBody,
            Map<String, String> mapInlineFiles
    ) throws Exception {
        MailConfiguration mailConf = new MailConfiguration();
        if(!mailConf.isConfigured()) return;

        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailConf.getSmtpHost());
        properties.put("mail.smtp.port", mailConf.getSmtpPort());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", String.valueOf(mailConf.isMailSmtpSslEnabled()));
        properties.put("mail.user", mailConf.getSmtpUser());
        properties.put("mail.password", mailConf.getSmtpPassword());
        properties.put("mail.smtp.socketFactory.port", mailConf.getSmtpPort());
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConf.getSmtpUser(), mailConf.getSmtpPassword());
            }
        };

        Session session = Session.getInstance(properties, auth);
        // session.setDebug(true);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(mailConf.getSmtpAddress()));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlBody, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds inline image attachments
        if (mapInlineFiles != null && !mapInlineFiles.isEmpty()) {
            Set<String> setImageID = mapInlineFiles.keySet();

            for (String contentId : setImageID) {
                MimeBodyPart filePart = new MimeBodyPart();
                filePart.setHeader("Content-ID", "<" + contentId + ">");
                filePart.setDisposition(MimeBodyPart.ATTACHMENT);
                String filePath = mapInlineFiles.get(contentId);
                try {
                    // TODO: add file to mail
                    // filePart.attachFile();
                } catch (Exception ex) {
                    EmailErrorLogger.getInstance().log(ex);
                    ex.printStackTrace();
                }
                multipart.addBodyPart(filePart);
            }
        }

        msg.setContent(multipart);
        // Thread.currentThread().setContextClassLoader( EmailSender.class.getClassLoader() );
        Transport.send(msg);
    }

}
