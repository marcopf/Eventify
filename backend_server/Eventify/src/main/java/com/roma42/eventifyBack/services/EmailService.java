package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

//    private final Logger log = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "givannipanico@gmail.com";
        String senderName = "Eventify";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Eventify.";


//        log.debug("content: " + content);
//        log.debug("email: " + toAddress);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName());
        String verifyURL = siteURL + "/api/v1/credential/verify?token=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

//        log.debug(content);

        helper.setText(content, true);

        javaMailSender.send(message);
        System.out.println("Email sent to " + toAddress );
    }

    @Async
    public void sendPasswordEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "givannipanico@gmail.com";
        String senderName = "Eventify";
        String subject = "Recovery password";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to recover your password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Eventify.";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName());
        String verifyURL = siteURL + "/api/v1/credential/newPassword?token=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsyncThread::");
        executor.initialize();
        return executor;
    }
}
