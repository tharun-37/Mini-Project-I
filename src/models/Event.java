package models;

import java.util.ArrayList;
import java.util.Map;

/** Represents a single Event. */
public class Event {
    public int id; // <-- NEW FIELD to store the database ID
    public String name;
    public ArrayList<Integer> assignedAlumniIds = new ArrayList<>();

    public Event(String name) { this.name = name; }

    public void display(Map<Integer, Alumnus> alumniMap) {
        System.out.println("\nEvent: " + name + " (ID: " + id + ")");
        if (assignedAlumniIds.isEmpty()) {
            System.out.println("  - No alumni assigned.");
        } else {
            System.out.println("  - Assigned Alumni:");
            for (int id : assignedAlumniIds) {
                // Handle case where alumnus might have been deleted
                Alumnus a = alumniMap.get(id);
                String alumnusName = (a != null) ? a.name : "Unknown/Deleted";
                System.out.println("    - " + alumnusName + " (ID: " + id + ")");
            }
        }
    }
}