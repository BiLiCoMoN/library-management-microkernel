package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
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
import java.util.ArrayList;
import java.util.List;

public class BookManagement implements IPlugin {
    private TableView<Book> bookTable;
    private ObservableList<Book> bookList;
    private TextField titleField;
    private TextField authorField;
    private TextField isbnField;
    private TextField yearField;
    private TextField copiesField;
    
    @Override
    public boolean init() {
        try {
            IUIController uiController = ICore.getInstance().getUIController();
            
            MenuItem menuItem = uiController.createMenuItem("System", "Manage Books");
            
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    openBookManagementInterface();
                }
            });
            
            System.out.println("BookManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading BookManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void openBookManagementInterface() {
        IUIController uiController = ICore.getInstance().getUIController();
        VBox content = createBookInterface();
        uiController.createTab("Books", content);
        
        loadBooksFromDatabase();
    }
    
    private VBox createBookInterface() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Book Management");
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
        
        Label formTitle = new Label("Add New Book");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(10);
        
        // Form fields
        titleField = new TextField();
        titleField.setPromptText("Book title");
        titleField.setPrefWidth(200);
        
        authorField = new TextField();
        authorField.setPromptText("Author name");
        authorField.setPrefWidth(200);
        
        isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        isbnField.setPrefWidth(150);
        
        yearField = new TextField();
        yearField.setPromptText("Year");
        yearField.setPrefWidth(80);
        
        copiesField = new TextField();
        copiesField.setPromptText("Copies");
        copiesField.setPrefWidth(80);
        
        // Action buttons
        Button addButton = new Button("Add Book");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> handleAddBook());
        
        Button updateButton = new Button("Update Selected");
        updateButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
        updateButton.setOnAction(e -> handleUpdateBook());
        
        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearForm());
        
        // Form layout - First row
        formGrid.add(new Label("Title:"), 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(new Label("Author:"), 2, 0);
        formGrid.add(authorField, 3, 0);
        
        // Second row
        formGrid.add(new Label("ISBN:"), 0, 1);
        formGrid.add(isbnField, 1, 1);
        formGrid.add(new Label("Year:"), 2, 1);
        formGrid.add(yearField, 3, 1);
        formGrid.add(new Label("Copies:"), 4, 1);
        formGrid.add(copiesField, 5, 1);
        
        // Button row
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, updateButton, clearButton);
        formGrid.add(buttonBox, 0, 2, 6, 1);
        
        formBox.getChildren().addAll(formTitle, formGrid);
        
        return formBox;
    }
    
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        // Table header
        HBox tableHeader = new HBox(10);
        Label tableTitle = new Label("Book Catalog");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadBooksFromDatabase());
        
        Button deleteButton = new Button("Delete Selected");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteBook());
        
        tableHeader.getChildren().addAll(tableTitle, refreshButton, deleteButton);
        
        // Create table
        bookTable = createBookTable();
        
        tableBox.getChildren().addAll(tableHeader, bookTable);
        
        return tableBox;
    }
    
    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Configure columns to match database structure
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(150);
        
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(120);
        
        TableColumn<Book, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("publishedYear"));
        yearCol.setPrefWidth(80);
        
        TableColumn<Book, Integer> copiesCol = new TableColumn<>("Available");
        copiesCol.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));
        copiesCol.setPrefWidth(80);
        
        table.getColumns().addAll(idCol, titleCol, authorCol, isbnCol, yearCol, copiesCol);
        
        // Configure data
        bookList = FXCollections.observableArrayList();
        table.setItems(bookList);
        
        // Add selection listener to populate form
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormWithBook(newSelection);
            }
        });
        
        return table;
    }
    
    private void populateFormWithBook(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        yearField.setText(String.valueOf(book.getPublishedYear()));
        copiesField.setText(String.valueOf(book.getCopiesAvailable()));
    }
    
    private void handleAddBook() {
        try {
            // Get form data
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String yearText = yearField.getText().trim();
            String copiesText = copiesField.getText().trim();
            
            // Basic validations
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || 
                yearText.isEmpty() || copiesText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "All fields are required!");
                return;
            }
            
            int year = Integer.parseInt(yearText);
            int copies = Integer.parseInt(copiesText);
            
            // Create book using model (reuses validation)
            Book newBook = new Book(title, author, isbn, year, copies);
            
            // Save to database
            if (saveBookToDatabase(newBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully!");
                clearForm();
                loadBooksFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book!");
            }
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Year and Copies must be valid numbers!");
        } catch (IllegalArgumentException e) {
            // Book model validation - reuse existing validation
            showAlert(Alert.AlertType.WARNING, "Validation", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleUpdateBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a book to update!");
            return;
        }
        
        try {
            // Get form data
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String yearText = yearField.getText().trim();
            String copiesText = copiesField.getText().trim();
            
            // Basic validations
            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || 
                yearText.isEmpty() || copiesText.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "All fields are required!");
                return;
            }
            
            int year = Integer.parseInt(yearText);
            int copies = Integer.parseInt(copiesText);
            
            // Update selected book properties
            selectedBook.setTitle(title);
            selectedBook.setAuthor(author);
            selectedBook.setIsbn(isbn);
            selectedBook.setPublishedYear(year);
            selectedBook.setCopiesAvailable(copies);
            
            // Update in database
            if (updateBookInDatabase(selectedBook)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
                clearForm();
                loadBooksFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book!");
            }
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Year and Copies must be valid numbers!");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a book to delete!");
            return;
        }
        
        // Confirmation dialog
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Delete Book");
        confirmation.setContentText("Do you really want to delete \"" + selectedBook.getTitle() + "\"?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (deleteBookFromDatabase(selectedBook.getBookId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully!");
                clearForm();
                loadBooksFromDatabase();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete book!");
            }
        }
    }
    
    private void clearForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        yearField.clear();
        copiesField.clear();
        bookTable.getSelectionModel().clearSelection();
        titleField.requestFocus();
    }
    
    private void loadBooksFromDatabase() {
        try {
            List<Book> books = getBooksFromDatabase();
            bookList.clear();
            bookList.addAll(books);
            System.out.println("Book list updated. Total: " + books.size());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load books: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE METHODS - Using IIOController
    private boolean saveBookToDatabase(Book book) {
        // Match exact database column names from your screenshot
        String sql = "INSERT INTO books (title, author, isbn, published_year, copies_available) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setInt(4, book.getPublishedYear());
            stmt.setInt(5, book.getCopiesAvailable());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving book: " + e.getMessage());
            return false;
        }
    }
    
    private List<Book> getBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        // Match exact database column names from your screenshot
        String sql = "SELECT book_id, title, author, isbn, published_year, copies_available FROM books ORDER BY title";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Use constructor that matches database structure
                Book book = new Book(
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("published_year"),
                    rs.getInt("copies_available")
                );
                book.setBookId(rs.getInt("book_id")); // Set ID from database
                books.add(book);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        
        return books;
    }
    
    private boolean updateBookInDatabase(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, published_year=?, copies_available=? WHERE book_id=?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setInt(4, book.getPublishedYear());
            stmt.setInt(5, book.getCopiesAvailable());
            stmt.setInt(6, book.getBookId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            return false;
        }
    }
    
    private boolean deleteBookFromDatabase(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
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
