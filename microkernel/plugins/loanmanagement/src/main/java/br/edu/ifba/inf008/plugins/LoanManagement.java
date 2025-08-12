package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.interfaces.model.Loan;
import br.edu.ifba.inf008.interfaces.model.User;
import br.edu.ifba.inf008.interfaces.model.Book;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * LoanManagement Plugin - Library Loan and Return System
 * 
 * This plugin manages book loans and returns for the library system.
 * It allows librarians to check out books to patrons, process returns,
 * and track all loan activities with proper inventory management.
 * 
 * Features:
 * - Create new book loans for library patrons
 * - Process book returns and update inventory
 * - View and filter loan history
 * - Track active loans and overdue items
 * - Automatic inventory updates on loan/return
 * 
 * @author Jorge Dário Costa de Santana (20241160003)
 * @course INF008 - Programação Orientada a Objetos
 * @professor Sandro Santos Andrade
 * @institution IFBA - Instituto Federal da Bahia
 * @version 1.0
 * @since Java 17
 */
public class LoanManagement implements IPlugin {
    
    // UI components for loan management
    private TableView<Loan> loanTable;           // Table to display loans
    private ObservableList<Loan> loanList;       // List of loans for table binding
    private ComboBox<User> userComboBox;          // Dropdown for selecting users
    private ComboBox<Book> bookComboBox;          // Dropdown for selecting books
    private CheckBox activeOnlyCheckBox;          // Filter for active loans only
    
    /**
     * Initializes the plugin and sets up the menu integration.
     * 
     * @return true if initialization succeeds, false otherwise
     */
    @Override
    public boolean init() {
        try {
            // Get UI controller from core system
            IUIController uiController = ICore.getInstance().getUIController();
            
            // Create menu item for loan management
            MenuItem menuItem = uiController.createMenuItem("System", "Manage Loans");
            
            // Set action handler for menu item
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    openLoanManagementInterface();
                }
            });
            
            System.out.println("LoanManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading LoanManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates and displays the loan management interface.
     */
    private void openLoanManagementInterface() {
        IUIController uiController = ICore.getInstance().getUIController();
        VBox content = createLoanInterface();
        uiController.createTab("Loans", content);
        loadDataFromDatabase();
    }
    
    /**
     * Builds the complete loan management interface.
     * 
     * @return VBox containing the loan interface
     */
    private VBox createLoanInterface() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Title for the interface
        Label titleLabel = new Label("Loan Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Form section for creating loans
        VBox formSection = createFormSection();
        
        // Filter section for loan display
        HBox filterSection = createFilterSection();
        
        // Table section for loan history
        VBox tableSection = createTableSection();
        
        mainContainer.getChildren().addAll(titleLabel, formSection, filterSection, tableSection);
        return mainContainer;
    }
    
    /**
     * Creates the form section for loan operations.
     * 
     * @return VBox containing the loan form
     */
    private VBox createFormSection() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label formTitle = new Label("New Loan");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(10);
        
        // User selection dropdown
        userComboBox = new ComboBox<>();
        userComboBox.setPromptText("Select User");
        userComboBox.setPrefWidth(200);
        
        // Book selection dropdown
        bookComboBox = new ComboBox<>();
        bookComboBox.setPromptText("Select Available Book");
        bookComboBox.setPrefWidth(200);
        
        // Action buttons
        Button loanButton = new Button("Create Loan");
        loanButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        loanButton.setOnAction(e -> handleCreateLoan());
        
        Button returnButton = new Button("Return Selected");
        returnButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
        returnButton.setOnAction(e -> handleReturnBook());
        
        Button refreshButton = new Button("Refresh Data");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadDataFromDatabase());
        
        // Organize form elements
        formGrid.add(new Label("User:"), 0, 0);
        formGrid.add(userComboBox, 1, 0);
        formGrid.add(new Label("Book:"), 2, 0);
        formGrid.add(bookComboBox, 3, 0);
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(loanButton, returnButton, refreshButton);
        formGrid.add(buttonBox, 0, 1, 4, 1);
        
        formBox.getChildren().addAll(formTitle, formGrid);
        return formBox;
    }
    
    /**
     * Creates the filter section for loan display.
     * 
     * @return HBox containing filter controls
     */
    private HBox createFilterSection() {
        HBox filterBox = new HBox(15);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 5;");
        
        Label filterLabel = new Label("Filters:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        // Checkbox to show only active loans
        activeOnlyCheckBox = new CheckBox("Show only active loans");
        activeOnlyCheckBox.setSelected(true);
        activeOnlyCheckBox.setOnAction(e -> filterLoans());
        
        // Button to show all loans
        Button showAllButton = new Button("Show All");
        showAllButton.setOnAction(e -> {
            activeOnlyCheckBox.setSelected(false);
            filterLoans();
        });
        
        filterBox.getChildren().addAll(filterLabel, activeOnlyCheckBox, showAllButton);
        return filterBox;
    }
    
    /**
     * Creates the table section for displaying loans.
     * 
     * @return VBox containing the loan table
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        Label tableTitle = new Label("Loan History");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        // Create the loan table
        loanTable = createLoanTable();
        
        tableBox.getChildren().addAll(tableTitle, loanTable);
        return tableBox;
    }
    
    /**
     * Creates and configures the loan display table.
     * 
     * @return TableView for displaying loans
     */
    private TableView<Loan> createLoanTable() {
        TableView<Loan> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Table columns
        TableColumn<Loan, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Loan, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userCol.setPrefWidth(150);
        
        TableColumn<Loan, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookCol.setPrefWidth(200);
        
        TableColumn<Loan, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        loanDateCol.setPrefWidth(100);
        
        TableColumn<Loan, LocalDate> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        returnDateCol.setPrefWidth(100);
        
        TableColumn<Loan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        table.getColumns().addAll(idCol, userCol, bookCol, loanDateCol, returnDateCol, statusCol);
        
        // Initialize data list
        loanList = FXCollections.observableArrayList();
        table.setItems(loanList);
        
        return table;
    }
    
    /**
     * Handles creating a new loan.
     */
    private void handleCreateLoan() {
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();
        
        // Validate selections
        if (selectedUser == null || selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select both user and book!");
            return;
        }
        
        // Check book availability
        if (!selectedBook.isAvailable()) {
            showAlert(Alert.AlertType.WARNING, "Book Unavailable", "This book has no available copies!");
            return;
        }
        
        try {
            // Create loan in database
            if (createLoanInDatabase(selectedUser.getUserId(), selectedBook.getBookId())) {
                // Update book availability
                selectedBook.borrowCopy();
                updateBookAvailability(selectedBook);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Loan created successfully!");
                loadDataFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create loan!");
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handles returning a book.
     */
    private void handleReturnBook() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        
        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a loan to return!");
            return;
        }
        
        if (!selectedLoan.isActive()) {
            showAlert(Alert.AlertType.WARNING, "Already Returned", "This book has already been returned!");
            return;
        }
        
        try {
            if (returnBookInDatabase(selectedLoan.getLoanId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
                loadDataFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return book!");
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Filters loans based on active status.
     */
    private void filterLoans() {
        List<Loan> allLoans = getLoansFromDatabase();
        
        if (activeOnlyCheckBox.isSelected()) {
            // Show only active loans
            loanList.clear();
            loanList.addAll(allLoans.stream().filter(Loan::isActive).toList());
        } else {
            // Show all loans
            loanList.clear();
            loanList.addAll(allLoans);
        }
    }
    
    /**
     * Loads all data from database and updates the interface.
     */
    private void loadDataFromDatabase() {
        try {
            // Load users for dropdown
            List<User> users = getUsersFromDatabase();
            userComboBox.getItems().clear();
            userComboBox.getItems().addAll(users);
            
            // Load available books for dropdown
            List<Book> books = getAvailableBooksFromDatabase();
            bookComboBox.getItems().clear();
            bookComboBox.getItems().addAll(books);
            
            // Load and filter loans for table
            filterLoans();
            
            System.out.println("Loan data loaded. Users: " + users.size() + ", Books: " + books.size());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE OPERATIONS
    
    /**
     * Creates a new loan in the database.
     * 
     * @param userId ID of the user
     * @param bookId ID of the book
     * @return true if successful, false otherwise
     */
    private boolean createLoanInDatabase(int userId, int bookId) {
        String sql = "INSERT INTO loans (user_id, book_id, loan_date) VALUES (?, ?, ?)";

        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating loan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Processes a book return in the database.
     * 
     * @param loanId ID of the loan to return
     * @return true if successful, false otherwise
     */
    private boolean returnBookInDatabase(int loanId) {
        String sql = "UPDATE loans SET return_date = ? WHERE loan_id = ? AND return_date IS NULL";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, loanId);
            
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                updateBookAvailabilityOnReturn(loanId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates book availability after a loan.
     * 
     * @param book Book to update
     */
    private void updateBookAvailability(Book book) {
        String sql = "UPDATE books SET copies_available = ? WHERE book_id = ?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, book.getCopiesAvailable());
            stmt.setInt(2, book.getBookId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
        }
    }
    
    /**
     * Updates book availability when a book is returned.
     * 
     * @param loanId ID of the returned loan
     */
    private void updateBookAvailabilityOnReturn(int loanId) {
        String sql = """
            UPDATE books 
            SET copies_available = copies_available + 1 
            WHERE book_id = (SELECT book_id FROM loans WHERE loan_id = ?)
            """;
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating book availability on return: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves all loans from the database.
     * 
     * @return List of loans
     */
    private List<Loan> getLoansFromDatabase() {
        List<Loan> loans = new ArrayList<>();
        String sql = """
            SELECT l.loan_id, l.user_id, l.book_id, l.loan_date, l.return_date,
                   u.name as user_name, b.title as book_title
            FROM loans l
            JOIN users u ON l.user_id = u.user_id
            JOIN books b ON l.book_id = b.book_id
            ORDER BY l.loan_date DESC
            """;
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Date returnDateSql = rs.getDate("return_date");
                LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;
                
                Loan loan = new Loan(
                    rs.getInt("loan_id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getDate("loan_date").toLocalDate(),
                    returnDate,
                    rs.getString("user_name"),
                    rs.getString("book_title"),
                    "", // userEmail
                    ""  // bookAuthor
                );
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading loans: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Retrieves all users from the database.
     * 
     * @return List of users
     */
    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email FROM users ORDER BY name";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User(
                    rs.getString("name"),
                    rs.getString("email")
                );
                user.setUserId(rs.getInt("user_id"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Retrieves available books from the database.
     * 
     * @return List of available books
     */
    private List<Book> getAvailableBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, title, author, isbn, published_year, copies_available FROM books WHERE copies_available > 0 ORDER BY title";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("published_year"),
                    rs.getInt("copies_available")
                );
                book.setBookId(rs.getInt("book_id"));
                books.add(book);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Shows an alert dialog to the user.
     * 
     * @param type Type of alert
     * @param title Title of the dialog
     * @param message Message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}