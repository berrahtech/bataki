package observer;

import java.util.List;

/**
 * Interface représentant un abonné capable de recevoir des notifications.
 * Suit le pattern Observer.
 */
public interface Subscriber {

    /**
     * Met à jour l'abonné avec une nouvelle notification.
     * @param notification La notification reçue.
     */
    void update(Notification notification);

    /**
     * @return Le nom de l'abonné.
     */
    String getName();

    /**
     * @return L'email de l'abonné.
     */
    String getEmail();

    /**
     * Vérifie si l'abonné est actif.
     * @return true si l'abonné est actif, sinon false.
     */
    boolean isSubscribed();

    /**
     * @return La liste des notifications reçues.
     */
    List<Notification> getReceivedNotifications();

    /**
     * Modifie le statut d'abonnement.
     * @param subscribed true pour activer, false pour désactiver.
     */
    void setSubscribed(boolean subscribed);
}