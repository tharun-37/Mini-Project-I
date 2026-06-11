package services;

import models.Alumnus;
import models.Event;
import models.Message;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:alumni.db";
    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }

    /**
     * Creates all necessary tables if they don't exist
     * and inserts initial data if the alumni table is empty.
     */
    public void initDatabase() {
        String createAlumniTable = "CREATE TABLE IF NOT EXISTS alumni (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "batch INTEGER NOT NULL," +
                "department TEXT," +
                "contact TEXT," +
                "email TEXT," +
                "currentJob TEXT," +
                "isWillingToMentor BOOLEAN" +
                ");";

        String createCareerHistoryTable = "CREATE TABLE IF NOT EXISTS career_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "alumnus_id INTEGER NOT NULL," +
                "job_title TEXT NOT NULL," +
                "FOREIGN KEY (alumnus_id) REFERENCES alumni(id) ON DELETE CASCADE" +
                ");";

        String createDonationsTable = "CREATE TABLE IF NOT EXISTS donations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "alumnus_id INTEGER NOT NULL," +
                "cause TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "FOREIGN KEY (alumnus_id) REFERENCES alumni(id) ON DELETE CASCADE" +
                ");";

        String createEventsTable = "CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL" +
                ");";

        String createEventAssignmentsTable = "CREATE TABLE IF NOT EXISTS event_assignments (" +
                "event_id INTEGER NOT NULL," +
                "alumnus_id INTEGER NOT NULL," +
                "PRIMARY KEY (event_id, alumnus_id)," +
                "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE," +
                "FOREIGN KEY (alumnus_id) REFERENCES alumni(id) ON DELETE CASCADE" +
                ");";

        String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender TEXT NOT NULL," +
                "recipient TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "timestamp TEXT NOT NULL" +
                ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createAlumniTable);
            stmt.execute(createCareerHistoryTable);
            stmt.execute(createDonationsTable);
            stmt.execute(createEventsTable);
            stmt.execute(createEventAssignmentsTable);
            stmt.execute(createMessagesTable);
            
            // Insert initial data only if alumni table is empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM alumni");
            if (rs.getInt(1) == 0) {
                System.out.println("Database is empty. Populating with initial data...");
                insertInitialData(stmt);
            }
        } catch (SQLException e) {
            System.out.println("Error initializing database tables: " + e.getMessage());
        }
    }

    private void insertInitialData(Statement stmt) throws SQLException {
        // This single, large INSERT statement is more efficient than 30 individual ones.
        stmt.executeUpdate("INSERT INTO alumni (name, batch, department, contact, email, currentJob, isWillingToMentor) VALUES " +
            "('Arjun Balakrishnan', 2023, 'CSE', '9840000001', 'arjun.23cse@kongu.edu', 'Software Engineer @ Zoho', 1)," +
            "('Priya Murugan', 2022, 'ECE', '9840000002', 'priya.22ece@kongu.edu', 'Embedded Engineer @ Bosch', 0)," +
            "('Karthik Raja', 2024, 'MECH', '9840000003', 'karthik.24mech@kongu.edu', 'Design Engineer @ L&T', 1)," +
            "('Deepa Iyer', 2023, 'IT', '9840000004', 'deepa.23it@kongu.edu', 'Data Analyst @ TCS', 1)," +
            "('Suresh Menon', 2022, 'CIVIL', '9840000005', 'suresh.22civil@kongu.edu', 'Site Engineer @ RAMCO', 0)," +
            "('Ananya Sharma', 2021, 'CSE', '9840000006', 'ananya.21cse@kongu.edu', 'Cloud Engineer @ Infosys', 1)," +
            "('Vijay Natarajan', 2023, 'MECH', '9840000007', 'vijay.23mech@kongu.edu', 'QA Engineer @ TVS', 0)," +
            "('Lakshmi Prasanna', 2022, 'IT', '9840000008', 'lakshmi.22it@kongu.edu', 'Cybersecurity Analyst @ Wipro', 1)," +
            "('Ravi Chandran', 2024, 'ECE', '9840000009', 'ravi.24ece@kongu.edu', 'VLSI Trainee @ Qualcomm', 1)," +
            "('Kavitha Sundaram', 2021, 'CSE', '9840000010', 'kavitha.21cse@kongu.edu', 'AI Engineer @ Freshworks', 0)," +
            "('Mohan Kumar', 2022, 'MECH', '9840000011', 'mohan.22mech@kongu.edu', 'Product Engineer @ Ashok Leyland', 1)," +
            "('Geetha Srinivasan', 2023, 'ECE', '9840000012', 'geetha.23ece@kongu.edu', 'Network Engineer @ Cisco', 0)," +
            "('Balaji Venkatesh', 2021, 'IT', '9840000013', 'balaji.21it@kongu.edu', 'Database Admin @ HCL', 1)," +
            "('Meera Krishnan', 2024, 'CSE', '9840000014', 'meera.24cse@kongu.edu', 'Web Developer @ Cognizant', 0)," +
            "('Rajesh Pillai', 2022, 'CIVIL', '9840000015', 'rajesh.22civil@kongu.edu', 'Structural Designer @ Arup', 1)," +
            "('Saranya Devi', 2023, 'CSE', '9840000016', 'saranya.23cse@kongu.edu', 'Software Tester @ Capgemini', 0)," +
            "('Praveen Rao', 2021, 'MECH', '9840000017', 'praveen.21mech@kongu.edu', 'R&D Engineer @ Saint-Gobain', 1)," +
            "('Nithya Selvam', 2022, 'ECE', '9840000018', 'nithya.22ece@kongu.edu', 'Hardware Engineer @ Intel', 1)," +
            "('Dinesh Gupta', 2023, 'IT', '9840000019', 'dinesh.23it@kongu.edu', 'Business Analyst @ Deloitte', 0)," +
            "('Shanti Priya', 2021, 'CIVIL', '9840000020', 'shanti.21civil@kongu.edu', 'Project Coordinator @ L&T', 1)," +
            "('Vignesh Kumar', 2024, 'CSE', '9840000021', 'vignesh.24cse@kongu.edu', 'Trainee @ Microsoft', 1)," +
            "('Sandhya Ramesh', 2022, 'MECH', '9840000022', 'sandhya.22mech@kongu.edu', 'Supply Chain Analyst @ MRF', 0)," +
            "('Hari Prasad', 2023, 'ECE', '9840000023', 'hari.23ece@kongu.edu', 'RF Engineer @ Samsung', 1)," +
            "('Divya Bharathi', 2021, 'IT', '9840000024', 'divya.21it@kongu.edu', 'UX/UI Designer @ Adobe', 1)," +
            "('Gopinath Iyer', 2022, 'CSE', '9840000025', 'gopinath.22cse@kongu.edu', 'DevOps Engineer @ TCS', 0)," +
            "('Malini Subramanian', 2024, 'CIVIL', '9840000026', 'malini.24civil@kongu.edu', 'Graduate Engineer @ Tata Projects', 1)," +
            "('Ashok Selvan', 2021, 'MECH', '9840000027', 'ashok.21mech@kongu.edu', 'Product Manager @ Hyundai', 0)," +
            "('Revathi Nair', 2023, 'CSE', '9840000028', 'revathi.23cse@kongu.edu', 'AI Researcher @ LatentView', 1)," +
            "('Chandru Mohan', 2022, 'IT', '9840000029', 'chandru.22it@kongu.edu', 'System Administrator @ Zoho', 0)," +
            "('Fatima Beevi', 2024, 'ECE', '9840000030', 'fatima.24ece@kongu.edu', 'Firmware Intern @ Visteon', 1);");
        
        // Add initial career history for all 30 alumni
        // These IDs (1-30) correspond to the order they were inserted above.
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (1, 'Software Engineer @ Zoho');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (2, 'Embedded Engineer @ Bosch');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (3, 'Design Engineer @ L&T');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (4, 'Data Analyst @ TCS');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (5, 'Site Engineer @ RAMCO');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (6, 'Cloud Engineer @ Infosys');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (7, 'QA Engineer @ TVS');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (8, 'Cybersecurity Analyst @ Wipro');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (9, 'VLSI Trainee @ Qualcomm');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (10, 'AI Engineer @ Freshworks');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (11, 'Product Engineer @ Ashok Leyland');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (12, 'Network Engineer @ Cisco');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (13, 'Database Admin @ HCL');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (14, 'Web Developer @ Cognizant');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (15, 'Structural Designer @ Arup');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (16, 'Software Tester @ Capgemini');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (17, 'R&D Engineer @ Saint-Gobain');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (18, 'Hardware Engineer @ Intel');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (19, 'Business Analyst @ Deloitte');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (20, 'Project Coordinator @ L&T');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (21, 'Trainee @ Microsoft');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (22, 'Supply Chain Analyst @ MRF');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (23, 'RF Engineer @ Samsung');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (24, 'UX/UI Designer @ Adobe');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (25, 'DevOps Engineer @ TCS');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (26, 'Graduate Engineer @ Tata Projects');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (27, 'Product Manager @ Hyundai');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (28, 'AI Researcher @ LatentView');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (29, 'System Administrator @ Zoho');");
        stmt.executeUpdate("INSERT INTO career_history (alumnus_id, job_title) VALUES (30, 'Firmware Intern @ Visteon');");
    }

    // --- ALUMNI METHODS ---

    public Map<Integer, Alumnus> loadAlumni() {
        Map<Integer, Alumnus> alumniMap = new HashMap<>();
        String sql = "SELECT * FROM alumni";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Alumnus a = new Alumnus(
                    rs.getInt("id"), rs.getString("name"), rs.getInt("batch"),
                    rs.getString("department"), rs.getString("contact"),
                    rs.getString("email"), rs.getString("currentJob"),
                    rs.getBoolean("isWillingToMentor")
                );
                // Now, populate the related data
                a.careerHistory = loadCareerHistory(a.id);
                a.donations = loadDonations(a.id);
                alumniMap.put(a.id, a);
            }
        } catch (SQLException e) {
            System.out.println("Error loading alumni: " + e.getMessage());
        }
        return alumniMap;
    }

    private LinkedList<String> loadCareerHistory(int alumnusId) {
        LinkedList<String> history = new LinkedList<>();
        String sql = "SELECT job_title FROM career_history WHERE alumnus_id = ? ORDER BY id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, alumnusId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.add(rs.getString("job_title"));
            }
        } catch (SQLException e) {
            System.out.println("Error loading career history: " + e.getMessage());
        }
        return history;
    }

    private HashMap<String, Double> loadDonations(int alumnusId) {
        HashMap<String, Double> donations = new HashMap<>();
        String sql = "SELECT cause, amount FROM donations WHERE alumnus_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, alumnusId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                donations.put(rs.getString("cause"), rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            System.out.println("Error loading donations: " + e.getMessage());
        }
        return donations;
    }

    public Alumnus addAlumnus(String name, int batch, String dept, String contact, String email, String job, boolean isWilling) {
        String sql = "INSERT INTO alumni(name, batch, department, contact, email, currentJob, isWillingToMentor) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name); pstmt.setInt(2, batch); pstmt.setString(3, dept);
            pstmt.setString(4, contact); pstmt.setString(5, email); pstmt.setString(6, job);
            pstmt.setBoolean(7, isWilling);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) return null;

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    // Also add the first job to career history
                    addCareerHistory(id, job);
                    return new Alumnus(id, name, batch, dept, contact, email, job, isWilling);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding alumnus: " + e.getMessage());
        }
        return null;
    }

    public void updateAlumnus(int id, String name, String contact, boolean isWilling) {
        String sql = "UPDATE alumni SET name = ?, contact = ?, isWillingToMentor = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.setBoolean(3, isWilling);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating alumnus: " + e.getMessage());
        }
    }

    public void deleteAlumnus(int id) {
        // ON DELETE CASCADE will handle career_history, donations, and event_assignments
        String sql = "DELETE FROM alumni WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting alumnus: " + e.getMessage());
        }
    }

    public void addCareerHistory(int alumnusId, String newJob) {
        // 1. Update the currentJob in the alumni table
        String sqlUpdate = "UPDATE alumni SET currentJob = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, newJob);
            pstmt.setInt(2, alumnusId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating current job: " + e.getMessage());
        }

        // 2. Add the new job to the career_history table
        String sqlInsert = "INSERT INTO career_history(alumnus_id, job_title) VALUES(?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setInt(1, alumnusId);
            pstmt.setString(2, newJob);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding career history: " + e.getMessage());
        }
    }

    public void addDonation(int alumnusId, String cause, double amount) {
        // This simple version just adds a new record.
        // A more complex version would check if the 'cause' exists and sum the amounts.
        String sql = "INSERT INTO donations(alumnus_id, cause, amount) VALUES(?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, alumnusId);
            pstmt.setString(2, cause);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding donation: " + e.getMessage());
        }
    }
    
    public Map<String, Long> getReportByDepartment() {
        Map<String, Long> report = new HashMap<>();
        String sql = "SELECT department, COUNT(*) as count FROM alumni GROUP BY department";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                report.put(rs.getString("department"), rs.getLong("count"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
        return report;
    }

    public long getMentorCount() {
        String sql = "SELECT COUNT(*) FROM alumni WHERE isWillingToMentor = 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Error getting mentor count: " + e.getMessage());
        }
        return 0;
    }

    // --- EVENT METHODS ---

    public List<Event> loadEvents() {
        List<Event> events = new ArrayList<>();
        // This query joins events with their assignments
        String sql = "SELECT e.id as event_id, e.name, ea.alumnus_id " +
                     "FROM events e " +
                     "LEFT JOIN event_assignments ea ON e.id = ea.event_id";
        
        Map<Integer, Event> eventMap = new HashMap<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                Event event = eventMap.get(eventId);
                
                if (event == null) {
                    event = new Event(rs.getString("name"));
                    event.id = eventId; // Set the ID from the DB
                    eventMap.put(eventId, event);
                    events.add(event);
                }
                
                int alumnusId = rs.getInt("alumnus_id");
                if (alumnusId > 0) { // alumnus_id will be 0 if there's no match (LEFT JOIN)
                    event.assignedAlumniIds.add(alumnusId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading events: " + e.getMessage());
        }
        return events;
    }

    public void createEvent(String name) {
        String sql = "INSERT INTO events(name) VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating event: " + e.getMessage());
        }
    }

    public void assignAlumnusToEvent(int eventId, int alumnusId) {
        String sql = "INSERT INTO event_assignments(event_id, alumnus_id) VALUES(?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, eventId);
            pstmt.setInt(2, alumnusId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error assigning alumnus: " + e.getMessage());
        }
    }

    // --- MESSAGE METHODS ---

    public List<Message> loadMessages() {
        List<Message> messages = new LinkedList<>();
        String sql = "SELECT * FROM messages ORDER BY timestamp";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                messages.add(new Message(
                    rs.getString("sender"), rs.getString("recipient"),
                    rs.getString("content"), rs.getString("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error loading messages: " + e.getMessage());
        }
        return messages;
    }

    public void saveMessage(Message message) {
        String sql = "INSERT INTO messages(sender, recipient, content, timestamp) VALUES(?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message.sender);
            pstmt.setString(2, message.recipient);
            pstmt.setString(3, message.content);
            pstmt.setString(4, message.timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }
}