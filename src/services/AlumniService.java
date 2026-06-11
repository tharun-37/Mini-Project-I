package services;

import models.Alumnus;
import java.util.*;
import java.util.stream.Collectors;

public class AlumniService {
    private final DatabaseManager dbManager;

    public AlumniService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    private Map<Integer, Alumnus> getAlumniRecords() {
        return dbManager.loadAlumni();
    }
    
    public Alumnus getAlumnusById(int id) {
        return getAlumniRecords().get(id);
    }
    
    public void viewAlumnusProfile(int id) {
        Alumnus alumnus = getAlumnusById(id);
        if (alumnus != null) {
            alumnus.display();
        } else {
            System.out.println("Could not find profile for ID: " + id);
        }
    }

    private void displayAlumniAsTable(Collection<Alumnus> alumniList) {
        if (alumniList.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        String border = "+----+------------------+-------+---------+----------------------------+--------------------------------+----------+";
        System.out.println(border);
        System.out.printf("| %-2s | %-16s | %-5s | %-7s | %-26s | %-30s | %-8s |\n", "ID", "Name", "Batch", "Dept", "Email", "Current Job", "Mentor?");
        System.out.println(border);

        for (Alumnus a : alumniList) {
            String name = a.name.length() > 16 ? a.name.substring(0, 15) + "." : a.name;
            String email = a.email.length() > 26 ? a.email.substring(0, 25) + "." : a.email;
            
            String job;
            if (a.currentJob == null || a.currentJob.isEmpty()) {
                job = "N/A";
            } else {
                job = a.currentJob.length() > 30 ? a.currentJob.substring(0, 29) + "." : a.currentJob;
            }
            
            String mentor = a.isWillingToMentor ? "Yes" : "No";
            System.out.printf("| %-2d | %-16s | %-5d | %-7s | %-26s | %-30s | %-8s |\n", a.id, name, a.batch, a.department, email, job, mentor);
        }
        System.out.println(border);
    }

    public void viewAllAlumni(Scanner scanner) {
        System.out.println("\n--- All Alumni Records ---");
        displayAlumniAsTable(getAlumniRecords().values());
        
        System.out.print("\nDo you want to view full career history for an ID? (yes/no): ");
        String choice = scanner.nextLine().trim().toLowerCase();

        if (choice.equals("yes") || choice.equals("y")) {
            try {
                System.out.print("Enter Alumnus ID to view history: ");
                int id = Integer.parseInt(scanner.nextLine());
                Alumnus alumnus = getAlumnusById(id); 
                
                if (alumnus == null) {
                    System.out.println("Alumnus with ID " + id + " not found.");
                    return;
                }

                System.out.println("\n--- Career History for " + alumnus.name + " (ID: " + id + ") ---");
                if (alumnus.careerHistory.isEmpty()) {
                    System.out.println("No career history found.");
                } else {
                    System.out.println("Career History: " + String.join(" -> ", alumnus.careerHistory));
                }
                System.out.println("----------------------------------------");

            } catch (NumberFormatException e) {
                System.out.println("Invalid ID. Please enter a number.");
            }
        }
    }

    public void addAlumnus(Scanner scanner) {
        try {
            System.out.print("Enter Name: "); String name = scanner.nextLine();
            System.out.print("Enter Batch (Year): "); int batch = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Department: "); String dept = scanner.nextLine();
            System.out.print("Enter Contact Number: "); String contact = scanner.nextLine();
            System.out.print("Enter Email: "); String email = scanner.nextLine();
            System.out.print("Enter Current Job: "); String job = scanner.nextLine();
            System.out.print("Willing to mentor? (true/false): ");
            boolean isWilling = Boolean.parseBoolean(scanner.nextLine());

            Alumnus newAlumnus = dbManager.addAlumnus(name, batch, dept, contact, email, job, isWilling);
            
            if (newAlumnus != null) {
                System.out.println("Alumnus added successfully with ID: " + newAlumnus.id);
            } else {
                System.out.println("Failed to add alumnus.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid batch year. Please enter a number.");
        }
    }
    
    public void updateAlumnus(Scanner scanner) {
        try {
            System.out.print("Enter Alumnus ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());
            Alumnus alumnus = getAlumnusById(id);

            if (alumnus == null) {
                System.out.println("Alumnus with ID " + id + " not found.");
                return;
            }

            System.out.println("Updating details for: " + alumnus.name + ". Press Enter to skip a field.");
            System.out.print("New Name ("+alumnus.name+"): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) alumnus.name = name;

            System.out.print("New Contact ("+alumnus.contact+"): ");
            String contact = scanner.nextLine();
            if (!contact.isEmpty()) alumnus.contact = contact;

            System.out.print("New Mentorship Status ("+alumnus.isWillingToMentor+"): ");
            String mentorStatus = scanner.nextLine();
            if (!mentorStatus.isEmpty()) alumnus.isWillingToMentor = Boolean.parseBoolean(mentorStatus);
            
            dbManager.updateAlumnus(id, alumnus.name, alumnus.contact, alumnus.isWillingToMentor);
            System.out.println("Update successful.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }

    public void deleteAlumnus(Scanner scanner) {
         try {
            System.out.print("Enter Alumnus ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());
            
            if (getAlumnusById(id) == null) {
                System.out.println("Alumnus not found.");
                return;
            }
            
            dbManager.deleteAlumnus(id);
            System.out.println("Alumnus deleted successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number.");
        }
    }
    
    public void searchAlumni(Scanner scanner) {
        System.out.print("Search by (1) Name, (2) Department, (3) Batch Year, or (4) Find Mentors: ");
        String choice = scanner.nextLine();
        
        Collection<Alumnus> allAlumni = getAlumniRecords().values();
        List<Alumnus> results;

        if (choice.equals("4")) {
            results = allAlumni.stream()
                .filter(a -> a.isWillingToMentor)
                .collect(Collectors.toList());
            System.out.println("\n--- All Available Mentors ---");

        } else {
            System.out.print("Enter search term: ");
            String term = scanner.nextLine().toLowerCase();
            
            results = allAlumni.stream().filter(a -> {
                switch (choice) {
                    case "1": return a.name.toLowerCase().contains(term);
                    case "2": return a.department.toLowerCase().contains(term);
                    case "3": return String.valueOf(a.batch).equals(term);
                    default: return false;
                }
            }).collect(Collectors.toList());
            System.out.println("\n--- Search Results ---");
        }
        
        displayAlumniAsTable(results);
    }
    
    public void trackCareerAndDonation(Scanner scanner) {
        try {
            System.out.print("Enter Alumnus ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            if (getAlumnusById(id) == null) {
                System.out.println("Alumnus not found.");
                return;
            }
            
            System.out.print("(1) Update Career, (2) Add Donation: ");
            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                System.out.print("Enter new job (e.g., 'Senior Developer at Amazon'): ");
                String newJob = scanner.nextLine();
                dbManager.addCareerHistory(id, newJob);
                System.out.println("Career updated.");
            } else if (choice.equals("2")) {
                System.out.print("Enter donation cause (e.g., 'Annual Meet 2025'): ");
                String cause = scanner.nextLine();
                System.out.print("Enter amount: ");
                double amount = Double.parseDouble(scanner.nextLine());
                dbManager.addDonation(id, cause, amount);
                System.out.println("Donation recorded.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    public void viewReports() {
        System.out.println("\n--- System Reports ---");
        System.out.println("Total Alumni Registered: " + getAlumniRecords().size());
        
        Map<String, Long> countByDept = dbManager.getReportByDepartment();
        System.out.println("Alumni Count by Department: " + countByDept);
        
        long mentorCount = dbManager.getMentorCount();
        System.out.println("Alumni Willing to Mentor: " + mentorCount);
        
        System.out.println("--- End of Report ---");
    }
}