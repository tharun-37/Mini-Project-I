package models;

import java.util.HashMap;
import java.util.LinkedList;

/** Represents a single Alumnus. */
public class Alumnus {
    public int id;
    public String name, department, contact, email, currentJob;
    public int batch;
    public boolean isWillingToMentor;
    public LinkedList<String> careerHistory = new LinkedList<>();
    public HashMap<String, Double> donations = new HashMap<>(); // Changed from TreeMap

    // Constructor is now simpler. Lists are populated by DatabaseManager
    public Alumnus(int id, String name, int batch, String dept, String contact, String email, String job, boolean isWilling) {
        this.id = id; this.name = name; this.batch = batch; this.department = dept;
        this.contact = contact; this.email = email; this.currentJob = job;
        this.isWillingToMentor = isWilling;
        // careerHistory and donations are loaded separately by DatabaseManager
    }

    public void display() {
        System.out.println("----------------------------------------");
        System.out.println("ID: " + id + " | Name: " + name);
        System.out.println("Batch: " + batch + " | Department: " + department);
        System.out.println("Contact: " + contact + " | Email: " + email);
        System.out.println("Current Job: " + currentJob);
        System.out.println("Willing to Mentor: " + (isWillingToMentor ? "Yes" : "No"));
        System.out.println("Career History: " + String.join(" -> ", careerHistory));
        System.out.println("Donations: " + donations);
        System.out.println("----------------------------------------");
    }
}