package services;

import models.Alumnus;
import models.Message;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommunicationService {
    private DatabaseManager dbManager;

    public CommunicationService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    // Helper to get fresh data
    public List<Message> getMessageLog() { 
        return dbManager.loadMessages(); 
    }

    // This needs the alumni map to get the sender's name
    public void manageCommunication(Scanner scanner, String userRole, int alumnusId) {
        boolean isAdmin = "ADMIN".equals(userRole);
        System.out.println("\n--- Communication Center ---");
        System.out.print(isAdmin ? "(1) Send to Alumnus, (2) Broadcast, (3) View All: " : "(1) Send to Admin, (2) View My Messages: ");
        
        String choice = scanner.nextLine();
        if (isAdmin) {
            switch (choice) {
                case "1":
                    System.out.print("Enter Alumnus ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter message: ");
                    Message msg1 = new Message("Admin", "Alumnus ID: " + id, scanner.nextLine());
                    dbManager.saveMessage(msg1);
                    System.out.println("Message sent.");
                    break;
                case "2":
                    System.out.print("Enter broadcast message: ");
                    Message msg2 = new Message("Admin", "Broadcast", scanner.nextLine());
                    dbManager.saveMessage(msg2);
                    System.out.println("Broadcast sent.");
                    break;
                case "3":
                    getMessageLog().forEach(Message::display);
                    break;
                default: System.out.println("Invalid choice.");
            }
        } else { // Alumni View
            // Need to get the alumni's name for sending messages
            Alumnus self = dbManager.loadAlumni().get(alumnusId);
            String myName = (self != null) ? self.name : "Alumnus " + alumnusId;

            switch (choice) {
                case "1":
                    System.out.print("Enter message for Admin: ");
                    Message msg = new Message("Alumnus: " + myName, "Admin", scanner.nextLine());
                    dbManager.saveMessage(msg);
                    System.out.println("Message sent.");
                    break;
                case "2":
                    String myRecipientId = "Alumnus ID: " + alumnusId;
                    getMessageLog().stream()
                        .filter(m -> m.recipient.equals(myRecipientId) || m.recipient.equals("Broadcast") || m.sender.contains(myName))
                        .forEach(Message::display);
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
    }
}