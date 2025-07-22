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

public class LoanManagement implements IPlugin {
    private TableView<Loan> loanTable;
    private ObservableList<Loan> loanList;
    private ComboBox<User> userComboBox;
    private ComboBox<Book> bookComboBox;
    private CheckBox activeOnlyCheckBox;
    
    @Override
    public boolean init() {
        try {
            IUIController uiController = ICore.getInstance().getUIController();
            
            MenuItem menuItem = uiController.createMenuItem("System", "Manage Loans");
            
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
    
    private void openLoanManagementInterface() {
        IUIController uiController = ICore.getInstance().getUIController();
        VBox content = createLoanInterface();
        uiController.createTab("Loans", content);
        
        loadDataFromDatabase();
    }
    
    private VBox createLoanInterface() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Loan Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Form section
        VBox formSection = createFormSection();
        
        // Filters section
        HBox filterSection = createFilterSection();
        
        // Table section
        VBox tableSection = createTableSection();
        
        mainContainer.getChildren().addAll(titleLabel, formSection, filterSection, tableSection);
        
        return mainContainer;
    }
    
    private VBox createFormSection() {
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label formTitle = new Label("New Loan");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(10);
        
        // ComboBoxes
        userComboBox = new ComboBox<>();
        userComboBox.setPromptText("Select User");
        userComboBox.setPrefWidth(200);
        
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
        
        // Layout
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
    
    private HBox createFilterSection() {
        HBox filterBox = new HBox(15);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 5;");
        
        Label filterLabel = new Label("Filters:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        activeOnlyCheckBox = new CheckBox("Show only active loans");
        activeOnlyCheckBox.setSelected(true);
        activeOnlyCheckBox.setOnAction(e -> filterLoans());
        
        Button showAllButton = new Button("Show All");
        showAllButton.setOnAction(e -> {
            activeOnlyCheckBox.setSelected(false);
            filterLoans();
        });
        
        filterBox.getChildren().addAll(filterLabel, activeOnlyCheckBox, showAllButton);
        
        return filterBox;
    }
    
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        Label tableTitle = new Label("Loan History");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        // Create table
        loanTable = createLoanTable();
        
        tableBox.getChildren().addAll(tableTitle, loanTable);
        
        return tableBox;
    }
    
    private TableView<Loan> createLoanTable() {
        TableView<Loan> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Configure columns
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
        
        // Configure data
        loanList = FXCollections.observableArrayList();
        table.setItems(loanList);
        
        return table;
    }
    
    private void handleCreateLoan() {
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();
        
        if (selectedUser == null || selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select both user and book!");
            return;
        }
        
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
            // Return book in database
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
    
    private void filterLoans() {
        List<Loan> allLoans = getLoansFromDatabase();
        
        if (activeOnlyCheckBox.isSelected()) {
            loanList.clear();
            loanList.addAll(allLoans.stream().filter(Loan::isActive).toList());
        } else {
            loanList.clear();
            loanList.addAll(allLoans);
        }
    }
    
    private void loadDataFromDatabase() {
        try {
            // Load users for combo box
            List<User> users = getUsersFromDatabase();
            userComboBox.getItems().clear();
            userComboBox.getItems().addAll(users);
            
            // Load available books for combo box
            List<Book> books = getAvailableBooksFromDatabase();
            bookComboBox.getItems().clear();
            bookComboBox.getItems().addAll(books);
            
            // Load loans for table
            filterLoans();
            
            System.out.println("Loan data loaded. Users: " + users.size() + ", Books: " + books.size());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE METHODS
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
    
    private boolean returnBookInDatabase(int loanId) {
        String sql = "UPDATE loans SET return_date = ? WHERE loan_id = ? AND return_date IS NULL";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, loanId);
            
            boolean success = stmt.executeUpdate() > 0;
            
            if (success) {
                // Update book availability
                updateBookAvailabilityOnReturn(loanId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
            return false;
        }
    }
    
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
                    "", // userEmail - campo vazio
                    ""  // bookAuthor - campo vazio
                );
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading loans: " + e.getMessage());
        }
        
        return loans;
    }
    
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
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}