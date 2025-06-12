package observer;
/**
 * Point d'entrée de l'application.
 * Lance le service de notification en mode console.
 */
public class Main {
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        service.startConsole();
    }
}