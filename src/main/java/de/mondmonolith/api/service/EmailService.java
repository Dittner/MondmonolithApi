package de.mondmonolith.api.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") private String sender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sender);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        mailSender.send(msg);
    }

    public void sendVerificationCode(String recipientEmail, String code) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();

        log.info("Email sender: "+ sender);
        msg.setFrom(new InternetAddress(sender));
        msg.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
        msg.setSubject("Email Verification");

        String htmlContent = "<span>" +
                "<h1>Mondmonolith</h1>" +
                "<p>Thanks for starting the new account creation process. Please enter the following verification code when prompted.</p>" +
                "<p><b>Verification code:</b></p>" +
                "<h1>"+code+"</h1></span>";
        msg.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(msg);
    }
}
