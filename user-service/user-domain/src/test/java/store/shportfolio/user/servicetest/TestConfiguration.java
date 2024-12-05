//package store.shportfolio.user.servicetest;
//
//import org.mockito.Mock;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import store.shportfolio.user.application.UserApplicationService;
//import store.shportfolio.user.application.UserApplicationServiceImpl;
//import store.shportfolio.user.application.ports.output.repository.UserRepository;
//import store.shportfolio.user.domain.UserDomainService;
//import store.shportfolio.user.domain.UserDomainServiceImpl;
//
//@org.springframework.boot.test.context.TestConfiguration
//public class TestConfiguration {
//
//    @Mock
//    public UserRepository userRepository;
//
//    @Bean
//    public UserApplicationService userApplicationService() {
//        return new UserApplicationServiceImpl(userRepository,passwordEncoder(),userDomainService());
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDomainService userDomainService() {
//        return new UserDomainServiceImpl();
//    }
//
//}
