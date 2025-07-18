package store.shportfolio.user.infrastructure.email.test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import store.shportfolio.user.infrastructure.email.CacheConfig;
import store.shportfolio.user.infrastructure.email.CustomCacheManager;
import store.shportfolio.user.infrastructure.email.MailConfig;
import store.shportfolio.user.infrastructure.email.MailConfigData;
import store.shportfolio.user.infrastructure.email.adapter.MailSenderImpl;
import store.shportfolio.user.usecase.exception.AlreadyMailSendException;

@ActiveProfiles("test")
@EnableConfigurationProperties(MailConfigData.class)
@SpringBootTest(classes = {CacheConfig.class, MailConfig.class,CustomCacheManager.class},
        useMainMethod = SpringBootTest.UseMainMethod.NEVER)
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

    @AfterEach
    public void tearDown() {
        customCacheManager.clear();
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

//    @Test
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

//    @Test
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
