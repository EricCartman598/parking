package com.epam.parking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationService {

    private static final String CHANGE_STATUS = "Parking on Zastavskaya 22 Request updated";

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public void sendNotification(String email, String status, String comment) throws MessagingException {
        MimeMessage mail = javaMailSender.createMimeMessage();
        Context context = new Context();
        context.setVariable("name", email.substring(0, email.indexOf("_")).toUpperCase());
        context.setVariable("status", status);
        if (comment != null) {
            context.setVariable("comment", "Comment:" + comment);
        }
        else {
            context.setVariable("comment", "");
        }
        context.setVariable("logo", "logo");
        String html = templateEngine.process("emailMessage", context);
        createHelper(mail, email, CHANGE_STATUS, html);
        javaMailSender.send(mail);
    }

    private void createHelper(MimeMessage mail, String email, String subject, String html) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mail,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(email);
        helper.setFrom(from);
        helper.setSubject(subject);
        helper.setText(html, true);
        helper.addInline("logo", new ClassPathResource("images/tplg.png"), "image/png");
    }
}
