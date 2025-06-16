package observer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service principal de gestion des notifications et des abonnés.
 * Gère les employés, l'envoi de notifications et la persistance des données.
 */
public class NotificationService {
    private List<Subscriber> subscribers;  // Liste des abonnés au service
    private final Scanner scanner;        // Pour lire les entrées utilisateur
    private final EmailService emailService;  // Service d'envoi d'emails

    /**
     * Constructeur initialisant les services et chargeant les données.
     */
    public NotificationService() {
        this.scanner = new Scanner(System.in);
        this.emailService = new EmailService();
        loadInitialData();  // Charge les données au démarrage
    }

    /**
     * Charge les données initiales depuis le fichier JSON.
     */
    private void loadInitialData() {
        this.subscribers = new ArrayList<>();
        Map<String, Object> data = JsonDataManager.loadData();

        if (data != null && data.containsKey("employees")) {
            Object employeesData = data.get("employees");
            if (employeesData instanceof List) {
                Gson gson = new Gson();
                Type employeeListType = new TypeToken<List<Employee>>(){}.getType();
                List<Employee> employees = gson.fromJson(gson.toJson(employeesData), employeeListType);
                subscribers.addAll(employees);  // Ajoute les employés chargés
            }
        }
    }

    /**
     * Sauvegarde les données des abonnés dans un fichier JSON.
     */
    private void saveData() {
        Map<String, Object> data = new HashMap<>();
        data.put("employees", subscribers.stream()
                .filter(s -> s instanceof Employee)
                .map(s -> (Employee) s)
                .collect(Collectors.toList()));

        JsonDataManager.saveData(data);  // Persiste les données
    }

    /**
     * Lance l'interface console principale.
     */
    public void startConsole() {
        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1. Gérer les employés");
            System.out.println("2. Envoyer notification");
            System.out.println("3. Quitter");
            System.out.print("Choix: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> manageEmployees();  // Gestion des employés
                case 2 -> sendNotification(); // Envoi de notifications
                case 3 -> { saveData(); return; }  // Quitte et sauvegarde
                default -> System.out.println("Option invalide !");
            }
        }
    }

    /**
     * Menu de gestion des employés.
     */
    private void manageEmployees() {
        while (true) {
            System.out.println("\n=== Gestion Employés ===");
            System.out.println("1. Lister les employés");
            System.out.println("2. Ajouter un employé");
            System.out.println("3. Retirer un employé");
            System.out.println("4. Afficher notifications d'un employé");
            System.out.println("5. Modifier statut d'abonnement");
            System.out.println("6. Retour au menu principal");
            System.out.print("Choix: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> listEmployees();       // Liste les employés
                case 2 -> addEmployee();        // Ajoute un employé
                case 3 -> removeEmployee();     // Supprime un employé
                case 4 -> showEmployeeNotifications();  // Affiche les notifications
                case 5 -> toggleSubscriptionStatus();  // Change statut abonnement
                case 6 -> { return; }          // Retour au menu principal
                default -> System.out.println("Option invalide !");
            }
        }
    }

    /**
     * Affiche la liste des employés avec leur statut.
     */
    private void listEmployees() {
        System.out.println("\n=== Employés ===");
        subscribers.forEach(e ->
                System.out.printf("- %s (%s) | %s%n",
                        e.getName(),
                        e.getEmail(),
                        e.isSubscribed() ? "ACTIF" : "INACTIF"));
    }

    /**
     * Ajoute un nouvel employé à la liste des abonnés.
     */
    private void addEmployee() {
        System.out.print("Nom: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        // Vérifie si l'email existe déjà
        if (subscribers.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(email))) {
            System.out.println("Erreur: Email existe déjà !");
            return;
        }

        Employee emp = new Employee(name, email);
        subscribers.add(emp);
        saveData();  // Sauvegarde les modifications
        System.out.printf("%s ajouté(e) !%n", name);
    }

    /**
     * Supprime un employé de la liste des abonnés.
     */
    private void removeEmployee() {
        System.out.print("Email de l'employé à supprimer: ");
        String email = scanner.nextLine();

        boolean removed = subscribers.removeIf(e -> e.getEmail().equalsIgnoreCase(email));
        if (removed) {
            saveData();  // Sauvegarde les modifications
            System.out.println("Employé supprimé !");
        } else {
            System.out.println("Aucun employé trouvé avec cet email.");
        }
    }

    /**
     * Affiche les notifications reçues par un employé.
     */
    private void showEmployeeNotifications() {
        System.out.print("Email de l'employé: ");
        String email = scanner.nextLine();

        subscribers.stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .ifPresentOrElse(
                        e -> {
                            System.out.printf("\n=== Notifications de %s ===\n", e.getName());
                            List<Notification> notifications = e.getReceivedNotifications();
                            if (notifications.isEmpty()) {
                                System.out.println("Aucune notification reçue.");
                            } else {
                                notifications.forEach(notif ->
                                        System.out.printf("- [%s] De %s: %s\n",
                                                notif.getType(),
                                                notif.getSenderName(),
                                                notif.getMessage()));
                            }
                        },
                        () -> System.out.println("Employé non trouvé !")
                );
    }

    /**
     * Bascule le statut d'abonnement d'un employé (actif/inactif).
     */
    private void toggleSubscriptionStatus() {
        System.out.print("Email de l'employé: ");
        String email = scanner.nextLine();

        subscribers.stream()
                .filter(e -> e.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .ifPresentOrElse(
                        e -> {
                            boolean newStatus = !e.isSubscribed();
                            e.setSubscribed(newStatus);
                            saveData();  // Sauvegarde les modifications
                            System.out.printf("Statut d'abonnement de %s mis à jour: %s\n",
                                    e.getName(),
                                    newStatus ? "ACTIF" : "INACTIF");
                        },
                        () -> System.out.println("Employé non trouvé !")
                );
    }

    /**
     * Envoie une notification à tous les abonnés actifs.
     */
    private void sendNotification() {
        System.out.print("Votre nom: ");
        String sender = scanner.nextLine();
        System.out.print("Message: ");
        String message = scanner.nextLine();
        System.out.print("Type (EMAIL/CONSOLE): ");
        NotificationType type = NotificationType.valueOf(scanner.nextLine().toUpperCase());

        Notification notif = new Notification(sender, message, type);
        subscribers.stream()
                .filter(Subscriber::isSubscribed)  // Filtre les abonnés actifs
                .forEach(s -> {
                    if (type == NotificationType.EMAIL) {
                        emailService.sendEmail(s.getEmail(), "Notification de " + sender, message);
                    }
                    s.update(notif);  // Notifie l'abonné
                });

        saveData();  // Sauvegarde les notifications
        System.out.println("Notification envoyée à tous les abonnés !");
    }
}