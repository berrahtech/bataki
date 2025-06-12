package observer;

import com.google.gson.annotations.Expose;
/**
 * Représente une notification avec un expéditeur, un message et un type.
 */
public class Notification {
    @Expose private final String senderName;
    @Expose private final String message;
    @Expose private final NotificationType type;
    /**
     * Constructeur.
     * @param senderName Expéditeur.
     * @param message Contenu.
     * @param type Type de notification.
     */
    public Notification(String senderName, String message, NotificationType type) {
        this.senderName = senderName;
        this.message = message;
        this.type = type;
    }

    // Getters
    public String getSenderName() { return senderName; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
}