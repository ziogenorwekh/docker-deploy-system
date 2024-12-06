package store.shportfolio.user.infrastructure.test.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import store.shportfolio.user.infrastructure.exception.AlreadyMailSendException;
import store.shportfolio.user.infrastructure.mail.CacheConfig;
import store.shportfolio.user.infrastructure.mail.CustomCacheManager;
import store.shportfolio.user.infrastructure.mail.MailConfig;
import store.shportfolio.user.infrastructure.mail.MailConfigData;
import store.shportfolio.user.infrastructure.mail.adapter.MailSenderImpl;

@ActiveProfiles("test")
@EnableConfigurationProperties(MailConfigData.class)
@SpringBootTest(classes = {CacheConfig.class, MailConfig.class})
public class MailSenderTest {


    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private CustomCacheManager customCacheManager;

    private MailSenderImpl mailSender;

    @BeforeEach
    public void setUp() {
        mailSender = new MailSenderImpl(javaMailSender, customCacheManager);
    }

    @Test
    @DisplayName("mail send test")
    public void mailSendTestAndIsSaveCaChe() {

        // given
        String email = "ziogenorwekh@gmail.com";

        // when
        mailSender.sendMail(email);
        String code = customCacheManager.getCode(email).get();

        // then
        Assertions.assertNotNull(code);
    }

    @Test
    @DisplayName("mail verify")
    public void mailVerifyTest() {

        // given
        String email = "ziogenorwekh@gmail.com";
        mailSender.sendMail(email);
        String code = customCacheManager.getCode(email).get();

        // when

        Boolean isVerify = mailSender.verifyMail(email, code);

        // then
        Assertions.assertTrue(isVerify);
    }

    @Test
    @DisplayName("already mail send -> can not resend mail")
    public void alreadyMailSendTest() {

        // given
        String email = "ziogenorwekh@gmail.com";
        mailSender.sendMail(email);

        // when
        AlreadyMailSendException alreadyMailSendException = Assertions.assertThrows(AlreadyMailSendException.class,
                () -> mailSender.sendMail(email));

        // then
        Assertions.assertNotNull(alreadyMailSendException);
        Assertions.assertEquals("ziogenorwekh@gmail.com already sent",
                alreadyMailSendException.getMessage());
    }
}
