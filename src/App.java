import services.AlumniService;
import services.CommunicationService;
import services.EventService;
import services.DatabaseManager; 
import javax.swing.SwingUtilities; // Import for starting the GUI

// Note: The imports for models are no longer strictly needed in App.java 
// but are kept for completeness or future utility.
// import models.Alumnus; 
// import ui.Menu; // Menu class is no longer used in the main app loop
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Queue;
// import java.util.LinkedList;
// import java.util.Scanner; // No longer used

public class App {
    
    // Removed: private final Scanner scanner = new Scanner(System.in);
    // Removed: private final Map<String, String> credentials = new HashMap<>();
    // Removed: private String loggedInUserRole;
    // Removed: private int loggedInAlumnusId = -1; // This ID is now handled by the GUI for profile view
    // Removed: private final Queue<String> activityLog = new new LinkedList<>();

    // Services
    private final AlumniService alumniService;
    private final EventService eventService;
    private final CommunicationService communicationService;
    private final DatabaseManager databaseManager; 

    public static void main(String[] args) {
        new App().run();
    }

    public App() {
        // Removed hardcoded authentication credentials setup

        // --- Database and Service Initialization ---
        this.databaseManager = new DatabaseManager(); 
        this.databaseManager.initDatabase(); 
        
        this.alumniService = new AlumniService(this.databaseManager);
        this.eventService = new EventService(this.databaseManager);
        this.communicationService = new CommunicationService(this.databaseManager);
    }

    private void run() {
        System.out.println("===== Welcome to the Alumni Management System (GUI Mode) =====");
        
        // --- Start the GUI on the Event Dispatch Thread (EDT) ---
        SwingUtilities.invokeLater(() -> {
            new AlumniPortalGUI(
                this.databaseManager, 
                this.alumniService, 
                this.eventService, 
                this.communicationService
            );
        });
        
        // The application remains running until the GUI frame is closed.
        // The call to databaseManager.close() should be handled by a WindowListener in the GUI frame.
        // System.out.println("Goodbye! (Connection closure handled by GUI listener)");
    }
    
    // All CLI-related methods (authenticate, adminLoop, alumniLoop, logActivity, viewActivityLog) are removed.
}