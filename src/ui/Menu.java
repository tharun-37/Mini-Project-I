package ui;

public class Menu {
    public static void displayAdminMenu() {
        System.out.println("\n--- Admin Menu ---");
        System.out.println("1. Add Alumnus");
        System.out.println("2. View All Alumni");
        System.out.println("3. Update Alumnus");
        System.out.println("4. Delete Alumnus");
        System.out.println("5. Search Alumni");
        System.out.println("6. Manage Events");
        System.out.println("7. Track Career/Donation");
        System.out.println("8. View Reports");
        System.out.println("9. View Activity Log");
        System.out.println("10. Communication");
        System.out.println("11. Save and Exit");
        System.out.print("Enter your choice: ");
    }

    public static void displayAlumniMenu() {
        System.out.println("\n--- Alumni Menu ---");
        System.out.println("1. Search for an Alumnus");
        System.out.println("2. View My Profile");
        System.out.println("3. Communication");
        System.out.println("4. Exit");
        System.out.print("Enter your choice: ");
    }
}