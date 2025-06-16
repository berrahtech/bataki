package observer;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
/**
 * Service pour envoyer des emails via SMTP.
 * Configure les propriétés SMTP et fournit une méthode pour envoyer des emails.
 */
public class EmailService {
    private final Properties smtpProps;
    /**
     * Constructeur : initialise les propriétés SMTP pour Gmail.
     */
    public EmailService() {
        smtpProps = new Properties();
        smtpProps.put("mail.smtp.host", "smtp.gmail.com");
        smtpProps.put("mail.smtp.port", "587");
        smtpProps.put("mail.smtp.auth", "true");
        smtpProps.put("mail.smtp.starttls.enable", "true");
    }
    /**
     * Envoie un email à un destinataire.
     * @param to Destinataire de l'email.
     * @param subject Sujet de l'email.
     * @param body Corps de l'email.
     */
    public void sendEmail(String to, String subject, String body) {
        Session session = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "berrahtech1995@gmail.com",
                        "jpby gdqr jbzm zmhl"
                );
            }
        });

        try {
            // Crée et configure le message email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("votre.email@gmail.com"));// Expéditeur
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));// Destinataire
            message.setSubject(subject);// Sujet
            message.setText(body);// Corps du message

            Transport.send(message);// Envoie l'email
            System.out.printf("[SMTP] Email envoyé à %s%n", to);// Confirmation
        } catch (MessagingException e) {
            System.err.println("Erreur dans smtp" );// Gestion des erreurs
        }
    }
}