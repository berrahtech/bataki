package observer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class NotificationService {
    private List<Subscriber> subscribers;
    private final Scanner scanner;
    private final EmailService emailService;

    public NotificationService() {
        this.scanner = new Scanner(System.in);
        this.emailService = new EmailService();
        loadInitialData();
    }

    private void loadInitialData() {
        this.subscribers = new ArrayList<>();
        Map<String, Object> data = JsonDataManager.loadData();

        if (data != null && data.containsKey("employees")) {
            Object employeesData = data.get("employees");
            if (employeesData instanceof List) {
                Gson gson = new Gson();
                Type employeeListType = new TypeToken<List<Employee>>(){}.getType();
                List<Employee> employees = gson.fromJson(gson.toJson(employeesData), employeeListType);
                subscribers.addAll(employees);
            }
        }
    }

    private void saveData() {
        Map<String, Object> data = new HashMap<>();
        data.put("employees", subscribers.stream()
                .filter(s -> s instanceof Employee)
                .map(s -> (Employee) s)
                .collect(Collectors.toList()));

        JsonDataManager.saveData(data);
    }

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
                case 1 -> manageEmployees();
                case 2 -> sendNotification();
                case 3 -> { saveData(); return; }
                default -> System.out.println("Option invalide !");
            }
        }
    }

    private void manageEmployees() {
        while (true) {
            System.out.println("\n=== Gestion Employés ===");
            System.out.println("1. Lister");
            System.out.println("2. Ajouter");
            System.out.println("3. Retirer");
            System.out.println("4. Afficher notifications d'un employé");
            System.out.println("5. Modifier statut d'abonnement");
            System.out.println("6. Retour");
            System.out.print("Choix: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> listEmployees();
                case 2 -> addEmployee();
                case 3 -> removeEmployee();
                case 4 -> showEmployeeNotifications();
                case 5 -> toggleSubscriptionStatus();
                case 6 -> { return; }
                default -> System.out.println("Option invalide !");
            }
        }
    }

    private void listEmployees() {
        System.out.println("\n=== Employés ===");
        subscribers.forEach(e ->
                System.out.printf("- %s (%s) | %s%n",
                        e.getName(),
                        e.getEmail(),
                        e.isSubscribed() ? "ACTIF" : "INACTIF"));
    }

    private void addEmployee() {
        System.out.print("Nom: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        if (subscribers.stream().anyMatch(e -> e.getEmail().equalsIgnoreCase(email))) {
            System.out.println("Erreur: Email existe déjà !");
            return;
        }

        Employee emp = new Employee(name, email);
        subscribers.add(emp);
        saveData();
        System.out.printf("%s ajouté(e) !%n", name);
    }

    private void removeEmployee() {
        System.out.print("Email de l'employé à supprimer: ");
        String email = scanner.nextLine();

        boolean removed = subscribers.removeIf(e -> e.getEmail().equalsIgnoreCase(email));
        if (removed) {
            saveData();
            System.out.println("Employé supprimé !");
        } else {
            System.out.println("Aucun employé trouvé avec cet email.");
        }
    }

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
                            saveData();
                            System.out.printf("Statut d'abonnement de %s mis à jour: %s\n",
                                    e.getName(),
                                    newStatus ? "ACTIF" : "INACTIF");
                        },
                        () -> System.out.println("Employé non trouvé !")
                );
    }
    private void sendNotification() {
        System.out.print("Votre nom: ");
        String sender = scanner.nextLine();
        System.out.print("Message: ");
        String message = scanner.nextLine();
        System.out.print("Type (EMAIL/CONSOLE): ");
        NotificationType type = NotificationType.valueOf(scanner.nextLine().toUpperCase());

        Notification notif = new Notification(sender, message, type);
        subscribers.stream()
                .filter(Subscriber::isSubscribed)
                .forEach(s -> {
                    if (type == NotificationType.EMAIL) {
                        emailService.sendEmail(s.getEmail(), "Notification de " + sender, message);
                    }
                    s.update(notif);
                });

        saveData();
        System.out.println("Notification envoyée à tous les abonnés !");
    }
}