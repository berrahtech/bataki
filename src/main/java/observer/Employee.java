package observer;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente un employé qui peut recevoir des notifications.
 * Implémente l'interface Subscriber pour le pattern Observer.
 */
public class Employee implements Subscriber {
    @Expose private String name; // Nom de l'employé (sérialisé avec Gson)
    @Expose private String email; // Email de l'employé
    @Expose private boolean subscribed; // Statut d'abonnement
    @Expose private List<Notification> receivedNotifications = new ArrayList<>(); // Notifications reçues

    /**
     * Constructeur par défaut pour Gson.
     */
    public Employee() {} // Pour GSON

    /**
     * Constructeur avec nom et email.
     * @param name Nom de l'employé.
     * @param email Email de l'employé.
     */
    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
        this.subscribed = true;
    }

    /**
     * Met à jour l'employé avec une nouvelle notification.
     * @param notification Notification reçue.
     */
    @Override
    public void update(Notification notification) {
        if (!notification.getSenderName().equals(this.name)) {
            receivedNotifications.add(notification);
            if (notification.getType() == NotificationType.CONSOLE) {
                System.out.printf("[CONSOLE] %s reçoit: %s (de %s)%n",
                        name, notification.getMessage(), notification.getSenderName());
            }
        }
    }

    // Getters/Setters
    @Override
    public String getName() { return name; }

    @Override
    public String getEmail() { return email; }

    @Override
    public boolean isSubscribed() { return subscribed; }

    @Override
    public List<Notification> getReceivedNotifications() { return receivedNotifications; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }

    public void setReceivedNotifications(List<Notification> notifications) {
        this.receivedNotifications = notifications;
    }
}