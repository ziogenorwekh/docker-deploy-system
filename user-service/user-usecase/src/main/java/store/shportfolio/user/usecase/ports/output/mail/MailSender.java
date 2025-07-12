package store.shportfolio.user.usecase.ports.output.mail;

public interface MailSender {

    void sendMail(String email);

    Boolean verifyMail(String email, String code);
}
