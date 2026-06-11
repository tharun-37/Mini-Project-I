package services;

import models.Alumnus;
import models.Event;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EventService {
    private DatabaseManager dbManager;

    public EventService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    // Helper to get fresh data from DB
    private List<Event> getEventList() { 
        return dbManager.loadEvents(); 
    }
    
    // Helper to get alumni map (could also come from AlumniService)
    private Map<Integer, Alumnus> getAlumniMap() {
        return dbManager.loadAlumni();
    }

    public void manageEvents(Scanner scanner) {
        System.out.print("(1) Create Event, (2) List Events, (3) Assign Alumnus to Event: ");
        switch (scanner.nextLine()) {
            case "1":
                System.out.print("Enter event name: ");
                dbManager.createEvent(scanner.nextLine());
                System.out.println("Event created.");
                break;
            case "2":
                List<Event> eventList = getEventList();
                Map<Integer, Alumnus> alumniMap = getAlumniMap(); // Get map for display
                if (eventList.isEmpty()) System.out.println("No events.");
                else eventList.forEach(e -> e.display(alumniMap));
                break;
            case "3":
                assignAlumnusToEvent(scanner);
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void assignAlumnusToEvent(Scanner scanner) {
        List<Event> eventList = getEventList();
        Map<Integer, Alumnus> alumniRecords = getAlumniMap();

        if (eventList.isEmpty() || alumniRecords.isEmpty()) {
            System.out.println("Cannot assign. Create events and alumni first.");
            return;
        }
        System.out.println("Select an event:");
        for (int i = 0; i < eventList.size(); i++) {
            System.out.println((i + 1) + ". " + eventList.get(i).name);
        }
        try {
            System.out.print("Enter event number: ");
            int eventIdx = Integer.parseInt(scanner.nextLine()) - 1;
            System.out.print("Enter Alumnus ID to assign: ");
            int alumnusId = Integer.parseInt(scanner.nextLine());

            if (eventIdx >= 0 && eventIdx < eventList.size() && alumniRecords.containsKey(alumnusId)) {
                // Get the actual Event ID from the object
                int eventId = eventList.get(eventIdx).id;
                dbManager.assignAlumnusToEvent(eventId, alumnusId);
                System.out.println("Assignment successful.");
            } else {
                System.out.println("Invalid event or Alumnus ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }
}