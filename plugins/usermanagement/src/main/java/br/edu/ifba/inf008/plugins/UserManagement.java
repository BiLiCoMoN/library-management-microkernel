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

public class UserManagement implements IPlugin {
    private TableView<User> userTable;
    private ObservableList<User> userList;
    private TextField nameField;
    private TextField emailField;
    
    @Override
    public boolean init() {
        try {
            IUIController uiController = ICore.getInstance().getUIController();
            
            MenuItem menuItem = uiController.createMenuItem("System", "Manage Users");
            
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    openUserManagementInterface();
                }
            });
            
            System.out.println("UserManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading UserManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void openUserManagementInterface() {
        IUIController uiController = ICore.getInstance().getUIController();
        VBox content = createUserInterface();
        uiController.createTab("Users", content);
        
        loadUsersFromDatabase();
    }
    
    private VBox createUserInterface() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Form section
        VBox formSection = createFormSection();
        
        // Table section
        VBox tableSection = createTableSection();
        
        mainContainer.getChildren().addAll(titleLabel, formSection, tableSection);
        
        return mainContainer;
    }
    
    private VBox createFormSection() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label formTitle = new Label("Add New User");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(10);
        
        // Form fields
        nameField = new TextField();
        nameField.setPromptText("Full name");
        nameField.setPrefWidth(250);
        
        emailField = new TextField();
        emailField.setPromptText("email@example.com");
        emailField.setPrefWidth(250);
        
        // Action buttons
        Button addButton = new Button("Add User");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> handleAddUser());
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearForm());
        
        // Form layout
        formGrid.add(new Label("Name:"), 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(new Label("Email:"), 2, 0);
        formGrid.add(emailField, 3, 0);
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, clearButton);
        formGrid.add(buttonBox, 4, 0);
        
        formBox.getChildren().addAll(formTitle, formGrid);
        
        return formBox;
    }
    
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        // Table header
        HBox tableHeader = new HBox(10);
        Label tableTitle = new Label("Registered Users");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadUsersFromDatabase());
        
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteUser());
        
        tableHeader.getChildren().addAll(tableTitle, refreshButton, deleteButton);
        
        // Create table
        userTable = createUserTable();
        
        tableBox.getChildren().addAll(tableHeader, userTable);
        
        return tableBox;
    }
    
    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Configure columns
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        idCol.setPrefWidth(60);
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);
        
        TableColumn<User, LocalDateTime> dateCol = new TableColumn<>("Registration Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("registeredAt"));
        dateCol.setPrefWidth(180);
        
        table.getColumns().addAll(idCol, nameCol, emailCol, dateCol);
        
        // Configure data
        userList = FXCollections.observableArrayList();
        table.setItems(userList);
        
        return table;
    }
    
    private void handleAddUser() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            
            // Validations
            if (name.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Name is required!");
                nameField.requestFocus();
                return;
            }
            
            if (email.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Email is required!");
                emailField.requestFocus();
                return;
            }
            
            // Create user using existing model (reuses validation)
            User newUser = new User(name, email);
            
            // Save to database
            if (saveUserToDatabase(newUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                clearForm();
                loadUsersFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add user!");
            }
            
        } catch (IllegalArgumentException e) {
            // User.java validation - reuse existing validation
            showAlert(Alert.AlertType.WARNING, "Validation", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a user to delete!");
            return;
        }
        
        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Delete User");
        confirmation.setContentText("Do you really want to delete " + selectedUser.getName() + "?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (deleteUserFromDatabase(selectedUser.getUserId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully!");
                loadUsersFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user!");
            }
        }
    }
    
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        nameField.requestFocus();
    }
    
    private void loadUsersFromDatabase() {
        try {
            List<User> users = getUsersFromDatabase();
            userList.clear();
            userList.addAll(users);
            System.out.println("User list updated. Total: " + users.size());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE METHODS - Using IIOController
    private boolean saveUserToDatabase(User user) {
        String sql = "INSERT INTO users (name, email, registered_at) VALUES (?, ?, ?)";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setTimestamp(3, Timestamp.valueOf(user.getRegisteredAt()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }
    
    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email, registered_at FROM users ORDER BY name";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Reuse complete User constructor
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
    
    private boolean deleteUserFromDatabase(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    // Utility method for alerts
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
