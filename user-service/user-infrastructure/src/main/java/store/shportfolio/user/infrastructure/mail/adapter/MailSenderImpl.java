package store.shportfolio.user.infrastructure.mail.adapter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import store.shportfolio.user.application.ports.output.mail.MailSender;
import store.shportfolio.user.infrastructure.exception.AlreadyMailSendException;
import store.shportfolio.user.infrastructure.mail.CustomCacheManager;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
public class MailSenderImpl implements MailSender {

    private final JavaMailSender javaMailSender;
    private final CustomCacheManager customCacheManager;

    public MailSenderImpl(JavaMailSender javaMailSender, CustomCacheManager customCacheManager) {
        this.javaMailSender = javaMailSender;
        this.customCacheManager = customCacheManager;
    }

    @Override
    public void sendMail(String email) {
        String code = generateRandomCode();
        log.info("Sending mail to " + email);
        log.info("Sending code " + code);
        if (!customCacheManager.save(email, code)) {
            throw new AlreadyMailSendException(String.format("%s already sent", email));
        }
        sendEmailCode(email, code);
    }

    @Override
    public Boolean verifyMail(String email, String code) {
        Optional<String> optionalCode = customCacheManager.getCode(email);
        return optionalCode.map(s -> s.equals(code)).orElse(false);
    }

    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(900000) + 100000); // 6자리 랜덤 숫자
    }

    private void sendEmailCode(String email, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Account Activation");
            helper.setText(buildEmailContent(code), true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to send email", e);
        }
    }

    private String buildEmailContent(String code) {
        return "<div style=\"font-family: Arial, sans-serif; margin: 20px;\">"
                + "<h2 style=\"color: #333;\">Activate Your Account</h2>"
                + "<p>Thank you for registering with our service. Please use the following code to activate your account:</p>"
                + "<h3 style=\"color: #007bff;\">" + code + "</h3>"
                + "<p>If you did not register for this service, please ignore this email.</p>"
                + "<p>Best regards,<br>Your Company</p>"
                + "</div>";
    }
}
