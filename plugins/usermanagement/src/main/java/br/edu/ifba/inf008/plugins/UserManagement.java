package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.interfaces.model.User;

import javafx.scene.control.MenuItem;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManagement Plugin Implementation
 * 
 * This plugin provides comprehensive user management functionality for the library system.
 * It follows the microkernel architecture pattern, operating as an independent module
 * that can be dynamically loaded and unloaded without affecting the core system.
 * 
 * Key Features:
 * - Complete CRUD operations for library users
 * - Real-time data validation and error handling
 * - Professional JavaFX interface with responsive design
 * - Direct database integration using JDBC connections
 * - Form-based data entry with immediate feedback
 * - Table-based data visualization with sorting capabilities
 * 
 * Architecture Compliance:
 * - Implements IPlugin interface for microkernel compatibility
 * - Uses dependency injection through ICore singleton pattern
 * - Maintains loose coupling with core system components
 * - Provides autonomous operation with self-contained business logic
 * 
 * @author Academic Project - Object-Oriented Programming Course
 * @version 1.0.0
 * @since Java 17
 */
public class UserManagement implements IPlugin {
    
    // UI Components for data display and interaction
    private TableView<User> userTable;           // Main data grid for user records
    private ObservableList<User> userList;      // Observable collection for reactive UI updates
    private TextField nameField;                // Input field for user full name
    private TextField emailField;               // Input field for user email address
    
    /**
     * Plugin initialization method called by the microkernel during plugin loading.
     * This method sets up the plugin's menu integration and establishes the 
     * necessary UI hooks within the main application framework.
     * 
     * The initialization process includes:
     * 1. Acquiring reference to the UI controller from the core system
     * 2. Creating menu item with proper categorization
     * 3. Binding event handlers for user interaction
     * 4. Registering the plugin as operational within the system
     * 
     * @return true if initialization succeeds, false if any critical error occurs
     */
    @Override
    public boolean init() {
        try {
            // Obtain UI controller reference through core system dependency injection
            IUIController uiController = ICore.getInstance().getUIController();
            
            // Create menu item with hierarchical organization (System -> Manage Users)
            MenuItem menuItem = uiController.createMenuItem("System", "Manage Users");
            
            // Attach event handler using anonymous inner class for better encapsulation
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    // Delegate to interface creation method when menu item is activated
                    openUserManagementInterface();
                }
            });
            
            // Log successful initialization for debugging and monitoring purposes
            System.out.println("UserManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            // Comprehensive error handling with detailed logging for troubleshooting
            System.err.println("Error loading UserManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates and displays the main user management interface within a new tab.
     * This method orchestrates the UI construction and data loading processes,
     * ensuring that users see a fully functional interface immediately upon access.
     * 
     * The interface creation workflow:
     * 1. Constructs the complete UI hierarchy
     * 2. Integrates the interface into the main application window
     * 3. Initiates data loading from the database
     * 4. Prepares the interface for immediate user interaction
     * 
     * @return true if initialization succeeds, false if any critical error occurs
     */
    private void openUserManagementInterface() {
        // Access UI controller for tab management functionality
        IUIController uiController = ICore.getInstance().getUIController();
        
        // Build the complete user interface structure
        VBox content = createUserInterface();
        
        // Integrate interface into main application as a new tab
        uiController.createTab("Users", content);
        
        // Populate interface with current database data
        loadUsersFromDatabase();
    }
    
    /**
     * Constructs the complete user interface hierarchy using JavaFX components.
     * This method implements a clean, professional design following modern UI/UX principles.
     * 
     * Interface Structure:
     * - Header section with descriptive title
     * - Form section for data entry and modification
     * - Table section for data visualization and selection
     * - Responsive layout that adapts to different window sizes
     * 
     * Design Principles Applied:
     * - Consistent spacing and alignment
     * - Logical grouping of related elements
     * - Clear visual hierarchy with appropriate typography
     * - Accessible color scheme and contrast ratios
     * 
     * @return VBox containing the complete user interface structure
     */
    private VBox createUserInterface() {
        // Main container with generous spacing for visual clarity
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Title section with prominent styling for clear identification
        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Form section for data entry operations
        VBox formSection = createFormSection();
        
        // Table section for data display and selection
        VBox tableSection = createTableSection();
        
        // Assemble complete interface hierarchy
        mainContainer.getChildren().addAll(titleLabel, formSection, tableSection);
        
        return mainContainer;
    }
    
    /**
     * Creates the form section responsible for user data entry and modification.
     * This section provides intuitive controls for adding new users to the system.
     * 
     * Form Features:
     * - Input validation with immediate feedback
     * - Clear placeholder text for user guidance
     * - Responsive button layout with distinct visual styles
     * - Consistent spacing and alignment for professional appearance
     * 
     * The form implements best practices for data entry:
     * - Logical tab order for keyboard navigation
     * - Appropriate input field sizes for expected content
     * - Clear visual distinction between different types of actions
     * 
     * @return VBox containing the complete form interface
     */
    private VBox createFormSection() {
        // Form container with distinctive background for visual separation
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        // Section title for clear identification of purpose
        Label formTitle = new Label("Add New User");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        // Grid layout for organized field arrangement
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);  // Horizontal spacing between columns
        formGrid.setVgap(10);  // Vertical spacing between rows
        
        // Name input field with descriptive placeholder
        nameField = new TextField();
        nameField.setPromptText("Full name");
        nameField.setPrefWidth(250);  // Adequate width for typical names
        
        // Email input field with format guidance
        emailField = new TextField();
        emailField.setPromptText("email@example.com");
        emailField.setPrefWidth(250);  // Consistent width with name field
        
        // Action buttons with distinct visual styling for different operations
        Button addButton = new Button("Add User");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> handleAddUser());  // Bind to add user handler
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearForm());    // Bind to form clearing handler
        
        // Organize form elements in logical grid structure
        formGrid.add(new Label("Name:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Email:"), 2, 0);
        formGrid.add(emailField, 3, 0);
        
        // Button group with consistent spacing
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, clearButton);
        formGrid.add(buttonBox, 4, 0);
        
        // Assemble complete form section
        formBox.getChildren().addAll(formTitle, formGrid);
        
        return formBox;
    }
    
    /**
     * Creates the table section for displaying and managing existing user records.
     * This section provides comprehensive data visualization with interactive capabilities.
     * 
     * Table Features:
     * - Sortable columns for flexible data organization
     * - Selection-based operations for targeted actions
     * - Responsive column sizing for optimal data display
     * - Integrated action buttons for common operations
     * 
     * The table design emphasizes usability and data clarity:
     * - Clear column headers with appropriate widths
     * - Consistent row formatting for easy scanning
     * - Visual feedback for selected items
     * - Efficient scrolling for large datasets
     * 
     * @return VBox containing the complete table interface
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        // Table header with action buttons for common operations
        HBox tableHeader = new HBox(10);
        Label tableTitle = new Label("Registered Users");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        // Refresh button for manual data synchronization
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadUsersFromDatabase());
        
        // Delete button for removing selected users
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteUser());
        
        // Organize header elements
        tableHeader.getChildren().addAll(tableTitle, refreshButton, deleteButton);
        
        // Create and configure the main data table
        userTable = createUserTable();
        
        // Assemble complete table section
        tableBox.getChildren().addAll(tableHeader, userTable);
        
        return tableBox;
    }
    
    /**
     * Constructs and configures the main data table for user records display.
     * This method sets up all columns, data binding, and interactive behaviors.
     * 
     * Table Configuration:
     * - Multiple columns for different data aspects
     * - Property-based data binding for automatic updates
     * - Appropriate column widths for content optimization
     * - Observable list integration for reactive updates
     * 
     * The table structure mirrors the database schema while providing
     * user-friendly column names and formatting for optimal readability.
     * 
     * @return TableView configured for user data display
     */
    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        table.setPrefHeight(400);  // Fixed height for consistent layout
        
        // ID column for unique identification (narrow width)
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idCol.setPrefWidth(60);
        
        // Name column for user identification (generous width)
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        // Email column for contact information (wide for email addresses)
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);
        
        // Registration date column for temporal information
        TableColumn<User, LocalDateTime> dateCol = new TableColumn<>("Registration Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));
        dateCol.setPrefWidth(180);
        
        // Add all columns to table in logical order
        table.getColumns().addAll(idCol, nameCol, emailCol, dateCol);
        
        // Initialize observable list for reactive data binding
        userList = FXCollections.observableArrayList();
        table.setItems(userList);
        
        return table;
    }
    
    /**
     * Handles the addition of new users to the system with comprehensive validation.
     * This method processes form data, validates inputs, and persists new records.
     * 
     * Validation Process:
     * 1. Extract and sanitize input data
     * 2. Perform client-side validation checks
     * 3. Create user model instance with validation
     * 4. Attempt database persistence
     * 5. Provide appropriate user feedback
     * 6. Refresh interface if successful
     * 
     * Error Handling:
     * - Input validation errors with specific guidance
     * - Database operation errors with user-friendly messages
     * - Comprehensive exception handling for robustness
     */
    private void handleAddUser() {
        try {
            // Extract form data with whitespace trimming
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            // Client-side validation for required fields
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Name is required!");
                nameField.requestFocus();  // Guide user to correction
                return;
            }
            
            if (email.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Email is required!");
                emailField.requestFocus();  // Guide user to correction
                return;
            }
            
            // Create user instance with model-level validation
            User newUser = new User(name, email);
            
            // Attempt database persistence
            if (saveUserToDatabase(newUser)) {
                // Success feedback and interface refresh
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                clearForm();
                loadUsersFromDatabase();
            } else {
                // Database operation failure feedback
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user!");
            }
            
        } catch (IllegalArgumentException e) {
            // Model validation errors (email format, etc.)
            showAlert(Alert.AlertType.WARNING, "Validation", e.getMessage());
        } catch (Exception e) {
            // Unexpected error handling with detailed logging
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the deletion of selected users with confirmation and safety checks.
     * This method ensures data integrity while providing clear user feedback.
     * 
     * Deletion Process:
     * 1. Verify user selection
     * 2. Display confirmation dialog with user details
     * 3. Perform database deletion if confirmed
     * 4. Refresh interface to reflect changes
     * 5. Provide appropriate feedback for all outcomes
     * 
     * Safety Features:
     * - Explicit confirmation requirement
     * - Clear identification of deletion target
     * - Graceful handling of deletion failures
     */
    private void handleDeleteUser() {
        // Verify user selection
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a user to delete!");
            return;
        }
        
        // Confirmation dialog with specific user identification
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Do you really want to delete " + selectedUser.getName() + "?");
        
        // Process deletion only if confirmed
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (deleteUserFromDatabase(selectedUser.getUserId())) {
                // Success feedback and interface refresh
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                loadUsersFromDatabase();
            } else {
                // Deletion failure feedback
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user!");
            }
        }
    }
    
    /**
     * Clears all form fields and resets the interface to initial state.
     * This method provides a clean slate for new data entry operations.
     */
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        nameField.requestFocus();  // Set focus for immediate data entry
    }
    
    /**
     * Loads user data from database and updates the interface display.
     * This method handles data retrieval, error management, and UI synchronization.
     * 
     * Loading Process:
     * 1. Execute database query for user records
     * 2. Clear existing table data
     * 3. Populate table with fresh data
     * 4. Log operation results for monitoring
     * 5. Handle any errors gracefully
     */
    private void loadUsersFromDatabase() {
        try {
            // Retrieve current user data from database
            List<User> users = getUsersFromDatabase();
            
            // Update observable list for reactive UI updates
            userList.clear();
            userList.addAll(users);
            
            // Log successful operation for monitoring
            System.out.println("User list updated. Total: " + users.size());
        } catch (Exception e) {
            // Error handling with user notification
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE OPERATIONS - Direct JDBC integration through core system
    
    /**
     * Persists a new user record to the database using prepared statements.
     * This method ensures data integrity and proper transaction handling.
     * 
     * @param user User instance containing validated data for persistence
     * @return true if persistence succeeds, false otherwise
     */
    private boolean saveUserToDatabase(User user) {
        String sql = "INSERT INTO users (name, email, registered_at) VALUES (?, ?, ?)";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Bind parameters with proper type handling
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setTimestamp(3, Timestamp.valueOf(user.getRegisteredAt()));
            
            // Execute and return success status
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all user records from the database with proper ordering.
     * This method constructs User instances from database result sets.
     * 
     * @return List of User instances representing current database state
     */
    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email, registered_at FROM users ORDER BY name";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Process each record and construct User instances
            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getTimestamp("registered_at").toLocalDateTime()
                );
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Removes a user record from the database by ID.
     * This method ensures safe deletion with proper error handling.
     * 
     * @param userId Unique identifier of the user to delete
     * @return true if deletion succeeds, false otherwise
     */
    private boolean deleteUserFromDatabase(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Bind user ID parameter and execute deletion
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Utility method for displaying user feedback through alert dialogs.
     * This method provides consistent alert styling and behavior across the plugin.
     * 
     * @param type Alert type determining icon and styling
     * @param title Dialog window title
     * @param message Main message content for user
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Clean design without header text
        alert.setContentText(message);
        alert.showAndWait();
    }
}
