package wqyeo.justsendthemail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@RestController
public class MailingController {

    @PostMapping("/send")
    public ResponseEntity<SendMailResult> index(
            @RequestHeader(name="Authorization", required = false, defaultValue = "") String authHeader,
            @RequestParam(required = false, defaultValue = "") String content,
            @RequestParam(required = false, defaultValue = "") String subject,
            @RequestParam(name="target", required = false, defaultValue = "") String targetAddress
    ) {
        if (!authHeader.equals(System.getenv("SERVICE_AUTH_KEY"))) {
            return new ResponseEntity<>(new SendMailResult(false, "Authorization failed."), HttpStatus.UNAUTHORIZED);
        }

        if (content.isEmpty() || content.isBlank()) {
            return new ResponseEntity<>(new SendMailResult(false, "Content is empty. (`content` param)"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (subject.isEmpty() || subject.isBlank()) {
            return new ResponseEntity<>(new SendMailResult(false, "Subject is empty. (`subject` param)"), HttpStatus.NOT_ACCEPTABLE);
        }

        if (targetAddress.isEmpty() || targetAddress.isBlank()) {
            return new ResponseEntity<>(new SendMailResult(false, "Target address is not set. (`target` param)"), HttpStatus.NOT_ACCEPTABLE);
        }

        Properties prop = new Properties();
        prop.put("mail.smtp.host", System.getenv("SMTP_HOST"));
        prop.put("mail.smtp.port", System.getenv("SMTP_PORT"));
        prop.put("mail.smtp.auth", System.getenv("SMTP_AUTH"));
        prop.put("mail.smtp.starttls.enable", System.getenv("SMTP_START_TLS"));
        prop.put("mail.smtp.ssl.enable", System.getenv("SMTP_SSL"));

        try {
            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(System.getenv("SMTP_USERNAME"), System.getenv("SMTP_PASSWORD"));
                        }
                    });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(System.getenv("SMTP_USERNAME")));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(targetAddress));
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);

            return new ResponseEntity<>(new SendMailResult(true, "Email sent successfully."), HttpStatus.OK);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
            return new ResponseEntity<>(new SendMailResult(false, "Failed to send email! Bad target address or configuration?"), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed due to unknown error: " + e.getMessage());
            return new ResponseEntity<>(new SendMailResult(false, "Failed to send email! Bad configuration or check service logs! Send report to https://github.com/wqyeo/Just-Send-The-Mail/issues if codebase issue."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}