import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import services.*; // Assumes your services are here
import models.*;   // Assumes your models are here

public class AlumniPortalGUI extends JFrame {

    private final AlumniService alumniService;
    private final EventService eventService;
    private final CommunicationService communicationService;
    private final int loggedInAlumnusId = 1; // Default login ID for Alumni profile view

    // GUI Components
    private JTabbedPane mainTabs;
    private JTextArea messageLogArea;
    private JTable alumniTable;
    private DefaultTableModel alumniTableModel;

    // Table Column Names based on Alumnus model
    private static final String[] COLUMN_NAMES = {"ID", "Name", "Batch", "Dept", "Email", "Current Job", "Mentor?"};

    public AlumniPortalGUI(DatabaseManager dbManager, AlumniService alumniService, 
                           EventService eventService, CommunicationService commService) {
        
        // --- Dependency Injection ---
        this.alumniService = alumniService;
        this.eventService = eventService;
        this.communicationService = commService;
        
        // --- Frame Setup ---
        setTitle("Alumni Management Portal (GUI)");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Main Tabbed Pane ---
        mainTabs = new JTabbedPane();
        mainTabs.addTab("👤 Alumni Management", createAlumniManagementPanel());
        mainTabs.addTab("🔍 Search & Reports", createSearchReportsPanel());
        mainTabs.addTab("📅 Events", createEventsPanel());
        mainTabs.addTab("💼 Career & Donations", createCareerDonationPanel());
        mainTabs.addTab("💬 Communication", createCommunicationPanel());
        
        add(mainTabs, BorderLayout.CENTER);

        // --- Status/Log Bar ---
        messageLogArea = new JTextArea("System Log: Ready.");
        messageLogArea.setEditable(false);
        messageLogArea.setRows(4);
        add(new JScrollPane(messageLogArea), BorderLayout.SOUTH);
        
        // Initial data load for the table
        refreshAlumniTable();

        setVisible(true);
    }
    
    // --- Utility Methods ---

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageLogArea.append("\n" + java.time.LocalTime.now().withNano(0) + " - " + message);
        });
    }

    private void refreshAlumniTable() {
        Collection<Alumnus> alumniList = alumniService.getAlumniRecords().values();
        Object[][] data = alumniList.stream()
                .map(a -> new Object[]{
                        a.id, a.name, a.batch, a.department, a.email, a.currentJob, a.isWillingToMentor ? "Yes" : "No"
                })
                .toArray(Object[][]::new);

        alumniTableModel = new DefaultTableModel(data, COLUMN_NAMES);
        alumniTable.setModel(alumniTableModel);
        alumniTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Widen email column
        logMessage("Alumni list refreshed. Total: " + alumniList.size());
    }
    
    // --- 1. Alumni Management Tab ---

    private JPanel createAlumniManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Center: Alumni List (JTable)
        alumniTable = new JTable();
        panel.add(new JScrollPane(alumniTable), BorderLayout.CENTER);

        // North: Action Buttons (Matches CLI Menu 1, 3, 4)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton addButton = new JButton("1. Add New Alumnus");
        addButton.addActionListener(e -> showAddAlumnusDialog());
        
        JButton updateButton = new JButton("3. Update Selected Alumnus");
        updateButton.addActionListener(e -> showUpdateAlumnusDialog());
        
        JButton deleteButton = new JButton("4. Delete Selected Alumnus");
        deleteButton.addActionListener(e -> showDeleteAlumnusDialog());
        
        JButton refreshButton = new JButton("2. View/Refresh All Alumni");
        refreshButton.addActionListener(e -> refreshAlumniTable());
        
        buttonPanel.add(refreshButton); // Replaces Menu 2
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        return panel;
    }
    
    // --- 2. Search & Reports Tab ---

    private JPanel createSearchReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // North: Search Fields (Matches CLI Menu 5)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchTerm = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{"Name", "Department", "Batch Year"});
        JButton searchButton = new JButton("Search Alumni");
        
        JTable searchResultsTable = new JTable(); // Separate table for results

        searchButton.addActionListener(e -> performSearch(searchTerm.getText(), (String) searchType.getSelectedItem(), searchResultsTable));
        
        JButton mentorButton = new JButton("Find Mentors");
        mentorButton.addActionListener(e -> performMentorSearch(searchResultsTable));
        
        JButton reportButton = new JButton("8. View System Reports");
        reportButton.addActionListener(e -> showSystemReports());

        searchPanel.add(new JLabel("Search By:"));
        searchPanel.add(searchType);
        searchPanel.add(searchTerm);
        searchPanel.add(searchButton);
        searchPanel.add(mentorButton);
        searchPanel.add(reportButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Center: Search Results Table
        panel.add(new JScrollPane(searchResultsTable), BorderLayout.CENTER);
        
        return panel;
    }

    // --- 3. Events Tab ---

    private JPanel createEventsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Left: Event Actions (Matches CLI Menu 6)
        JPanel eventActions = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton createEventButton = new JButton("6. Create New Event");
        JButton listEventsButton = new JButton("6. List Events (Refresh)");
        JButton assignButton = new JButton("6. Assign Alumnus to Event");
        
        JTextArea eventDisplayArea = new JTextArea();
        eventDisplayArea.setEditable(false);

        createEventButton.addActionListener(e -> showCreateEventDialog());
        listEventsButton.addActionListener(e -> refreshEventList(eventDisplayArea));
        assignButton.addActionListener(e -> showAssignAlumnusDialog());
        
        eventActions.add(createEventButton);
        eventActions.add(listEventsButton);
        eventActions.add(assignButton);
        
        panel.add(eventActions);
        panel.add(new JScrollPane(eventDisplayArea));
        return panel;
    }

    // --- 4. Career & Donations Tab ---

    private JPanel createCareerDonationPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Top: Career Tracking (Matches CLI Menu 7 - Option 1)
        JPanel careerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField careerIdField = new JTextField(5);
        JTextField newJobField = new JTextField(30);
        JButton trackCareerButton = new JButton("7. Update Career");
        
        trackCareerButton.addActionListener(e -> updateAlumnusCareer(careerIdField.getText(), newJobField.getText()));

        careerPanel.add(new JLabel("Alumnus ID:"));
        careerPanel.add(careerIdField);
        careerPanel.add(new JLabel("New Job:"));
        careerPanel.add(newJobField);
        careerPanel.add(trackCareerButton);
        panel.add(careerPanel);
        
        // Bottom: Donation Tracking (Matches CLI Menu 7 - Option 2)
        JPanel donationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField donationIdField = new JTextField(5);
        JTextField causeField = new JTextField(20);
        JTextField amountField = new JTextField(10);
        JButton addDonationButton = new JButton("7. Add Donation");
        
        addDonationButton.addActionListener(e -> addAlumnusDonation(donationIdField.getText(), causeField.getText(), amountField.getText()));

        donationPanel.add(new JLabel("Alumnus ID:"));
        donationPanel.add(donationIdField);
        donationPanel.add(new JLabel("Cause:"));
        donationPanel.add(causeField);
        donationPanel.add(new JLabel("Amount:"));
        donationPanel.add(amountField);
        donationPanel.add(addDonationButton);
        panel.add(donationPanel);
        
        return panel;
    }

    // --- 5. Communication Tab ---

    private JPanel createCommunicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Center: Dedicated Message Viewer
        JTextArea commView = new JTextArea("Use 'View All Messages' to load the log.");
        commView.setEditable(false);
        panel.add(new JScrollPane(commView), BorderLayout.CENTER);
        
        // South: Communication Actions (Matches CLI Menu 10 + Alumni Menu 2)
        JPanel commButtons = new JPanel(new GridLayout(2, 3, 10, 10));
        
        JButton sendAlumnus = new JButton("10. Send to Alumnus");
        JButton broadcast = new JButton("10. Broadcast Message");
        JButton viewAll = new JButton("10. View All Messages");
        JButton alumniProfile = new JButton("2. View My Profile (ID " + loggedInAlumnusId + ")");
        JButton adminMessage = new JButton("Send Message to Admin");

        sendAlumnus.addActionListener(e -> showSendMessageDialog("ADMIN", false));
        broadcast.addActionListener(e -> showSendMessageDialog("ADMIN", true));
        viewAll.addActionListener(e -> viewAllMessages(commView));
        alumniProfile.addActionListener(e -> showAlumnusProfile());
        adminMessage.addActionListener(e -> showSendMessageDialog("ALUMNI", false));

        commButtons.add(sendAlumnus);
        commButtons.add(broadcast);
        commButtons.add(viewAll);
        commButtons.add(alumniProfile);
        commButtons.add(adminMessage);
        
        panel.add(commButtons, BorderLayout.SOUTH);
        return panel;
    }

    // --- Dialog and Action Logic ---

    private void showAddAlumnusDialog() {
        // Collect 7 fields of data using custom input panels or a sequence of JOptionPane.showInputDialog
        JTextField name = new JTextField();
        JTextField batch = new JTextField();
        JTextField dept = new JTextField();
        JTextField contact = new JTextField();
        JTextField email = new JTextField();
        JTextField job = new JTextField();
        JCheckBox isWilling = new JCheckBox("Willing to Mentor?");

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(name);
        inputPanel.add(new JLabel("Batch (Year):"));
        inputPanel.add(batch);
        inputPanel.add(new JLabel("Department:"));
        inputPanel.add(dept);
        inputPanel.add(new JLabel("Contact:"));
        inputPanel.add(contact);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(email);
        inputPanel.add(new JLabel("Current Job:"));
        inputPanel.add(job);
        inputPanel.add(new JLabel("Mentorship:"));
        inputPanel.add(isWilling);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "1. Add New Alumnus", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int batchYear = Integer.parseInt(batch.getText().trim());
                // The AlumniService method takes a Scanner, we must adapt it or pass null
                // Since the service method is public void addAlumnus(Scanner scanner), we adapt the call:
                
                // NOTE: The CLI service methods are designed for Scanner input. 
                // In a proper conversion, the service method would be replaced with a version 
                // that takes parameters directly (e.g., alumniService.addAlumnus(name, batchYear, ...)).
                // For direct porting, we manually call the DBManager:
                
                Alumnus newAlumnus = alumniService.dbManager.addAlumnus(
                    name.getText(), batchYear, dept.getText(), contact.getText(), 
                    email.getText(), job.getText(), isWilling.isSelected()
                );
                
                if (newAlumnus != null) {
                    logMessage("Alumnus added successfully with ID: " + newAlumnus.id);
                    refreshAlumniTable();
                } else {
                    throw new Exception("Database operation failed or email duplicate.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Batch Year or Contact Number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to add alumnus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showUpdateAlumnusDialog() {
        int selectedRow = alumniTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an alumnus from the list first.", "Update Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) alumniTable.getValueAt(selectedRow, 0);
        Alumnus alumnus = alumniService.getAlumnusById(id);

        if (alumnus == null) {
            JOptionPane.showMessageDialog(this, "Alumnus not found in memory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // GUI for Update (simulating the CLI prompts)
        JTextField nameField = new JTextField(alumnus.name);
        JTextField contactField = new JTextField(alumnus.contact);
        JCheckBox mentorCheck = new JCheckBox("Willing to Mentor?", alumnus.isWillingToMentor);

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(new JLabel("ID (Fixed):"));
        inputPanel.add(new JLabel(String.valueOf(id)));
        inputPanel.add(new JLabel("New Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("New Contact:"));
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("Mentorship Status:"));
        inputPanel.add(mentorCheck);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "3. Update Alumnus Profile", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            alumniService.dbManager.updateAlumnus(id, nameField.getText(), contactField.getText(), mentorCheck.isSelected());
            logMessage("Update successful for ID: " + id);
            refreshAlumniTable();
        }
    }
    
    private void showDeleteAlumnusDialog() {
        int selectedRow = alumniTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an alumnus from the list first.", "Delete Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) alumniTable.getValueAt(selectedRow, 0);
        String name = (String) alumniTable.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Alumnus ID " + id + " (" + name + ")? \nThis action is permanent.", 
            "4. Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            alumniService.dbManager.deleteAlumnus(id);
            logMessage("Alumnus ID " + id + " (" + name + ") deleted successfully.");
            refreshAlumniTable();
        }
    }
    
    private void performSearch(String term, String type, JTable resultsTable) {
        // This is a simplification; the service search method needs to be adapted to return a List<Alumnus>
        Collection<Alumnus> allAlumni = alumniService.getAlumniRecords().values();
        
        Collection<Alumnus> results = allAlumni.stream().filter(a -> {
            switch (type) {
                case "Name": return a.name.toLowerCase().contains(term.toLowerCase());
                case "Department": return a.department.toLowerCase().contains(term.toLowerCase());
                case "Batch Year": return String.valueOf(a.batch).contains(term.toLowerCase());
                default: return false;
            }
        }).collect(Collectors.toList());

        updateResultsTable(results, resultsTable, "Search");
    }
    
    private void performMentorSearch(JTable resultsTable) {
        Collection<Alumnus> allAlumni = alumniService.getAlumniRecords().values();
        Collection<Alumnus> results = allAlumni.stream()
                .filter(a -> a.isWillingToMentor)
                .collect(Collectors.toList());
        
        updateResultsTable(results, resultsTable, "Mentor Search");
    }

    private void updateResultsTable(Collection<Alumnus> results, JTable resultsTable, String searchType) {
        Object[][] data = results.stream()
                .map(a -> new Object[]{a.id, a.name, a.batch, a.department, a.email, a.currentJob, a.isWillingToMentor ? "Yes" : "No"})
                .toArray(Object[][]::new);

        DefaultTableModel model = new DefaultTableModel(data, COLUMN_NAMES);
        resultsTable.setModel(model);
        logMessage(searchType + " completed. Found " + results.size() + " results.");
    }

    private void showSystemReports() {
        Map<String, Long> countByDept = alumniService.dbManager.getReportByDepartment();
        long mentorCount = alumniService.dbManager.getMentorCount();
        long totalAlumni = alumniService.getAlumniRecords().size();
        
        String report = String.format(
            "--- System Reports ---\n" +
            "Total Alumni Registered: %d\n" +
            "Alumni Willing to Mentor: %d\n" +
            "Alumni Count by Department: %s\n" +
            "--- End of Report ---",
            totalAlumni, mentorCount, countByDept.toString()
        );
        
        JOptionPane.showMessageDialog(this, report, "8. System Reports", JOptionPane.INFORMATION_MESSAGE);
        logMessage("Reports viewed.");
    }
    
    private void updateAlumnusCareer(String idStr, String newJob) {
        try {
            int id = Integer.parseInt(idStr);
            if (alumniService.getAlumnusById(id) == null) {
                JOptionPane.showMessageDialog(this, "Alumnus ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newJob.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Job cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            alumniService.dbManager.addCareerHistory(id, newJob);
            logMessage("Career updated for ID " + id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addAlumnusDonation(String idStr, String cause, String amountStr) {
        try {
            int id = Integer.parseInt(idStr);
            double amount = Double.parseDouble(amountStr);
            if (alumniService.getAlumnusById(id) == null) {
                JOptionPane.showMessageDialog(this, "Alumnus ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            alumniService.dbManager.addDonation(id, cause, amount);
            logMessage("Donation recorded for ID " + id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID or Amount format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showSendMessageDialog(String role, boolean isBroadcast) {
        String recipientStr = isBroadcast ? "Broadcast" : JOptionPane.showInputDialog(this, "Enter Recipient Alumnus ID (e.g., 2):");
        if (recipientStr == null || (!isBroadcast && recipientStr.trim().isEmpty())) return;
        
        String content = JOptionPane.showInputDialog(this, "Enter Message Content:");
        if (content == null || content.trim().isEmpty()) return;

        String senderName = (role.equals("ADMIN") ? "Admin" : "Alumnus ID: " + loggedInAlumnusId);
        String recipientDisplay = isBroadcast ? "Broadcast" : "Alumnus ID: " + recipientStr;
        
        Message msg = new Message(senderName, recipientDisplay, content);
        communicationService.dbManager.saveMessage(msg);
        logMessage("Message sent: " + (isBroadcast ? "Broadcast" : "to ID " + recipientStr));
    }
    
    private void viewAllMessages(JTextArea commView) {
        java.util.List<Message> messages = communicationService.getMessageLog();
        StringBuilder sb = new StringBuilder();
        messages.forEach(m -> sb.append(String.format("[%s] From: %s | To: %s\n  > %s\n", m.timestamp, m.sender, m.recipient, m.content)));
        commView.setText(sb.toString());
        commView.setCaretPosition(0); // Scroll to top
        logMessage("Communication log refreshed. Total messages: " + messages.size());
    }

    private void showAlumnusProfile() {
        Alumnus self = alumniService.getAlumnusById(loggedInAlumnusId);
        if (self == null) {
            JOptionPane.showMessageDialog(this, "Logged-in Alumnus ID 1 not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Reuse the text display logic from the CLI Alumnus model for simplicity
        String profile = String.format(
            "--- My Profile (ID: %d) ---\n" +
            "Name: %s\n" +
            "Batch: %d | Dept: %s\n" +
            "Email: %s\n" +
            "Job: %s\n" +
            "Willing to Mentor: %s\n" +
            "Career History: %s\n" +
            "Donations: %s",
            self.id, self.name, self.batch, self.department, self.email, self.currentJob,
            self.isWillingToMentor ? "Yes" : "No", String.join(" -> ", self.careerHistory), self.donations
        );
        
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(profile)), "Alumnus Profile", JOptionPane.INFORMATION_MESSAGE);
        logMessage("Profile viewed for ID " + loggedInAlumnusId);
    }
    
    private void refreshEventList(JTextArea eventDisplayArea) {
        java.util.List<Event> eventList = eventService.getEventList();
        Map<Integer, Alumnus> alumniMap = alumniService.getAlumniRecords();
        
        StringBuilder sb = new StringBuilder("--- Current Events ---\n");
        if (eventList.isEmpty()) {
            sb.append("No events found.");
        } else {
            eventList.forEach(e -> {
                sb.append(String.format("Event ID: %d | Name: %s\n", e.id, e.name));
                sb.append("  Assigned Alumni: ");
                if (e.assignedAlumniIds.isEmpty()) {
                    sb.append("None\n");
                } else {
                    e.assignedAlumniIds.forEach(id -> {
                        Alumnus a = alumniMap.get(id);
                        sb.append(String.format("%s (ID %d); ", (a != null ? a.name : "Unknown"), id));
                    });
                    sb.append("\n");
                }
            });
        }
        eventDisplayArea.setText(sb.toString());
        logMessage("Event list refreshed.");
    }
    
    private void showCreateEventDialog() {
        String eventName = JOptionPane.showInputDialog(this, "Enter new event name:");
        if (eventName != null && !eventName.trim().isEmpty()) {
            eventService.dbManager.createEvent(eventName);
            logMessage("Event created: " + eventName);
        }
    }
    
    private void showAssignAlumnusDialog() {
        String eventIdStr = JOptionPane.showInputDialog(this, "Enter Event ID:");
        String alumnusIdStr = JOptionPane.showInputDialog(this, "Enter Alumnus ID to assign:");
        
        try {
            int eventId = Integer.parseInt(eventIdStr);
            int alumnusId = Integer.parseInt(alumnusIdStr);
            
            if (alumniService.getAlumnusById(alumnusId) == null) {
                JOptionPane.showMessageDialog(this, "Alumnus ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            eventService.dbManager.assignAlumnusToEvent(eventId, alumnusId);
            logMessage(String.format("Alumnus %d assigned to Event %d.", alumnusId, eventId));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}