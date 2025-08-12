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

/**
 * BookManagement Plugin - Book Catalog Management
 * 
 * This plugin manages the library's book collection. It allows adding,
 * editing, and removing books from the catalog, with basic information
 * like title, author, ISBN, and available copies.
 * 
 * Features:
 * - Add new books to the collection
 * - Edit book information  
 * - Remove books from catalog
 * - Track available copies
 * - Basic ISBN validation
 * 
 * @author Jorge Dário Costa de Santana (20241160003)
 * @course INF008 - Programação Orientada a Objetos
 * @professor Sandro Santos Andrade
 * @institution IFBA - Instituto Federal da Bahia
 * @version 1.0
 * @since Java 17
 */
public class BookManagement implements IPlugin {
    
    // UI components for book management
    private TableView<Book> bookTable;              // Table to display books
    private ObservableList<Book> bookList;          // List of books for table binding
    private TextField titleField;                   // Input field for book title
    private TextField authorField;                  // Input field for author name
    private TextField isbnField;                    // Input field for ISBN
    private TextField yearField;                    // Input field for publication year
    private TextField copiesField;                  // Input field for available copies
    
    /**
     * Initializes the plugin and sets up the menu item.
     * 
     * @return true if initialization succeeds, false otherwise
     */
    @Override
    public boolean init() {
        try {
            // Get UI controller from core system
            IUIController uiController = ICore.getInstance().getUIController();
            
            // Create menu item for book management
            MenuItem catalogMenu = uiController.createMenuItem("Catalog", "Manage Books");
            
            // Set action handler for menu item
            catalogMenu.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    launchBookManagementWorkspace();
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
    
    /**
     * Creates and displays the book management interface.
     */
    private void launchBookManagementWorkspace() {
        IUIController uiController = ICore.getInstance().getUIController();
        VBox workspaceContent = buildBookManagementInterface();
        uiController.createTab("Book Catalog", workspaceContent);
        refreshCatalogDisplay();
    }
    
    /**
     * Builds the complete book management interface.
     * 
     * @return VBox containing the complete interface
     */
    private VBox buildBookManagementInterface() {
        VBox mainWorkspace = new VBox(20);
        mainWorkspace.setPadding(new Insets(25));
        
        // Title for the interface
        Label workspaceTitle = new Label("Library Catalog Management");
        workspaceTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Form section for adding books
        VBox entrySection = constructCatalogEntryForm();
        
        // Table section for displaying books
        VBox displaySection = constructCatalogDisplayGrid();
        
        mainWorkspace.getChildren().addAll(workspaceTitle, entrySection, displaySection);
        return mainWorkspace;
    }
    
    /**
     * Creates the form for adding new books to the catalog.
     * 
     * @return VBox containing the book entry form
     */
    private VBox constructCatalogEntryForm() {
        VBox formContainer = new VBox(15);
        formContainer.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 20; -fx-background-radius: 10;");
        
        Label formHeader = new Label("Add New Book to Catalog");
        formHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        GridPane entryGrid = new GridPane();
        entryGrid.setHgap(20);
        entryGrid.setVgap(15);
        
        // Input fields for book information
        titleField = new TextField();
        titleField.setPromptText("Enter book title");
        titleField.setPrefWidth(300);
        
        authorField = new TextField();
        authorField.setPromptText("Author name");
        authorField.setPrefWidth(280);
        
        isbnField = new TextField();
        isbnField.setPromptText("ISBN");
        isbnField.setPrefWidth(200);
        
        yearField = new TextField();
        yearField.setPromptText("Publication year");
        yearField.setPrefWidth(120);
        
        copiesField = new TextField();
        copiesField.setPromptText("Available copies");
        copiesField.setPrefWidth(120);
        
        // Action buttons
        Button addButton = new Button("Add to Catalog");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> processNewBookEntry());
        
        Button resetButton = new Button("Reset Form");
        resetButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        resetButton.setOnAction(e -> resetEntryForm());
        
        Button validateButton = new Button("Validate ISBN");
        validateButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        validateButton.setOnAction(e -> validateISBNFormat());
        
        // Organize form elements
        entryGrid.add(new Label("Title:"), 0, 0);
        entryGrid.add(titleField, 1, 0);
        entryGrid.add(new Label("Author:"), 2, 0);
        entryGrid.add(authorField, 3, 0);
        
        entryGrid.add(new Label("ISBN:"), 0, 1);
        entryGrid.add(isbnField, 1, 1);
        entryGrid.add(new Label("Year:"), 2, 1);
        entryGrid.add(yearField, 3, 1);
        
        entryGrid.add(new Label("Copies:"), 0, 2);
        entryGrid.add(copiesField, 1, 2);
        
        HBox actionGroup = new HBox(15);
        actionGroup.getChildren().addAll(addButton, resetButton, validateButton);
        entryGrid.add(actionGroup, 2, 2, 2, 1);
        
        formContainer.getChildren().addAll(formHeader, entryGrid);
        return formContainer;
    }
    
    /**
     * Creates the display section with book table and controls.
     * 
     * @return VBox containing the book display interface
     */
    private VBox constructCatalogDisplayGrid() {
        VBox displayContainer = new VBox(15);
        
        // Header with management controls
        HBox displayHeader = new HBox(15);
        displayHeader.setStyle("-fx-padding: 10; -fx-background-color: #34495e; -fx-background-radius: 5;");
        
        Label displayTitle = new Label("Current Catalog");
        displayTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Management buttons
        Button refreshButton = new Button("Refresh Catalog");
        refreshButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshButton.setOnAction(e -> refreshCatalogDisplay());
        
        Button editButton = new Button("Edit Selected");
        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        editButton.setOnAction(e -> editSelectedBook());
        
        Button removeButton = new Button("Remove Selected");
        removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        removeButton.setOnAction(e -> removeSelectedBook());
        
        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search catalog...");
        searchField.setPrefWidth(250);
        searchField.setOnKeyReleased(e -> performCatalogSearch(searchField.getText()));
        
        displayHeader.getChildren().addAll(displayTitle, refreshButton, editButton, removeButton, searchField);
        
        // Create the catalog table
        bookTable = createCatalogTable();
        
        displayContainer.getChildren().addAll(displayHeader, bookTable);
        return displayContainer;
    }
    
    /**
     * Creates and configures the book catalog table.
     * 
     * @return TableView for displaying books
     */
    private TableView<Book> createCatalogTable() {
        TableView<Book> catalogTable = new TableView<>();
        catalogTable.setPrefHeight(450);
        
        // Table columns
        TableColumn<Book, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        idColumn.setPrefWidth(60);
        
        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setPrefWidth(250);
        
        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorColumn.setPrefWidth(180);
        
        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnColumn.setPrefWidth(150);
        
        TableColumn<Book, Integer> yearColumn = new TableColumn<>("Year");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publishedYear"));
        yearColumn.setPrefWidth(80);
        
        TableColumn<Book, Integer> copiesColumn = new TableColumn<>("Available");
        copiesColumn.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));
        copiesColumn.setPrefWidth(90);
        
        catalogTable.getColumns().addAll(idColumn, titleColumn, authorColumn, isbnColumn, yearColumn, copiesColumn);
        
        // Initialize data list
        bookList = FXCollections.observableArrayList();
        catalogTable.setItems(bookList);
        
        // Set selection listener to populate form
        catalogTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormWithBook(newSelection);
            }
        });
        
        return catalogTable;
    }
    
    /**
     * Populates the form with data from selected book.
     * 
     * @param book Book to populate form with
     */
    private void populateFormWithBook(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        yearField.setText(String.valueOf(book.getPublishedYear()));
        copiesField.setText(String.valueOf(book.getCopiesAvailable()));
    }
    
    /**
     * Processes adding a new book to the catalog.
     */
    private void processNewBookEntry() {
        try {
            // Get form data
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String yearText = yearField.getText().trim();
            String copiesText = copiesField.getText().trim();
            
            // Validate required fields
            if (title.isEmpty()) {
                displayAlert(Alert.AlertType.WARNING, "Validation Error", "Book title is required!");
                titleField.requestFocus();
                return;
            }
            
            if (author.isEmpty()) {
                displayAlert(Alert.AlertType.WARNING, "Validation Error", "Author name is required!");
                authorField.requestFocus();
                return;
            }
            
            if (isbn.isEmpty()) {
                displayAlert(Alert.AlertType.WARNING, "Validation Error", "ISBN is required!");
                isbnField.requestFocus();
                return;
            }
            
            // Validate numeric fields
            int publicationYear;
            try {
                publicationYear = Integer.parseInt(yearText);
                if (publicationYear < 1000 || publicationYear > 2030) {
                    displayAlert(Alert.AlertType.WARNING, "Validation Error", "Publication year must be between 1000 and 2030!");
                    return;
                }
            } catch (NumberFormatException e) {
                displayAlert(Alert.AlertType.WARNING, "Validation Error", "Publication year must be a valid number!");
                yearField.requestFocus();
                return;
            }
            
            int availableCopies;
            try {
                availableCopies = Integer.parseInt(copiesText);
                if (availableCopies < 0) {
                    displayAlert(Alert.AlertType.WARNING, "Validation Error", "Available copies cannot be negative!");
                    return;
                }
            } catch (NumberFormatException e) {
                displayAlert(Alert.AlertType.WARNING, "Validation Error", "Available copies must be a valid number!");
                copiesField.requestFocus();
                return;
            }
            
            // Create new book
            Book newBook = new Book(title, author, isbn, publicationYear, availableCopies);
            
            // Save to database
            if (persistBookToDatabase(newBook)) {
                displayAlert(Alert.AlertType.INFORMATION, "Success", "Book added to catalog successfully!");
                resetEntryForm();
                refreshCatalogDisplay();
            } else {
                displayAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add book to catalog!");
            }
            
        } catch (IllegalArgumentException e) {
            displayAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            displayAlert(Alert.AlertType.ERROR, "System Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Resets the entry form to empty state.
     */
    private void resetEntryForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        yearField.clear();
        copiesField.clear();
        titleField.requestFocus();
    }
    
    /**
     * Validates the ISBN format (basic validation).
     */
    private void validateISBNFormat() {
        String isbn = isbnField.getText().trim();
        if (isbn.isEmpty()) {
            displayAlert(Alert.AlertType.WARNING, "Validation", "Please enter an ISBN to validate!");
            return;
        }
        
        if (isbn.length() >= 10) {
            displayAlert(Alert.AlertType.INFORMATION, "ISBN Validation", "ISBN format appears valid!");
        } else {
            displayAlert(Alert.AlertType.WARNING, "ISBN Validation", "ISBN format appears invalid!");
        }
    }
    
    /**
     * Handles editing of selected book.
     */
    private void editSelectedBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            displayAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a book to edit!");
            return;
        }
        
        displayAlert(Alert.AlertType.INFORMATION, "Edit Mode", 
                    "Edit functionality for " + selectedBook.getTitle() + " would be implemented here.");
    }
    
    /**
     * Removes selected book from catalog.
     */
    private void removeSelectedBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            displayAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a book to remove!");
            return;
        }
        
        // Confirm removal
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Removal");
        confirmation.setHeaderText("Remove Book from Catalog");
        confirmation.setContentText("Remove '" + selectedBook.getTitle() + "' by " + selectedBook.getAuthor() + "?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            if (removeBookFromDatabase(selectedBook.getBookId())) {
                displayAlert(Alert.AlertType.INFORMATION, "Success", "Book removed successfully!");
                refreshCatalogDisplay();
            } else {
                displayAlert(Alert.AlertType.ERROR, "Error", "Failed to remove book!");
            }
        }
    }
    
    /**
     * Performs search in the catalog.
     * 
     * @param searchTerm Search term entered by user
     */
    private void performCatalogSearch(String searchTerm) {
        System.out.println("Searching catalog for: " + searchTerm);
        // Search implementation would go here
    }
    
    /**
     * Refreshes the catalog display with current database data.
     */
    private void refreshCatalogDisplay() {
        try {
            List<Book> books = retrieveBooksFromDatabase();
            bookList.clear();
            bookList.addAll(books);
            System.out.println("Book list updated. Total: " + books.size());
        } catch (Exception e) {
            displayAlert(Alert.AlertType.ERROR, "Data Loading Error", "Failed to refresh catalog: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE OPERATIONS
    
    /**
     * Saves a book to the database.
     * 
     * @param book Book to save
     * @return true if successful, false otherwise
     */
    private boolean persistBookToDatabase(Book book) {
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
    
    /**
     * Retrieves all books from the database.
     * 
     * @return List of books
     */
    private List<Book> retrieveBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, title, author, isbn, published_year, copies_available FROM books ORDER BY title";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("published_year"),
                    rs.getInt("copies_available")
                );
                books.add(book);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Removes a book from the database.
     * 
     * @param bookId ID of book to remove
     * @return true if successful, false otherwise
     */
    private boolean removeBookFromDatabase(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing book: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Shows an alert dialog to the user.
     * 
     * @param type Type of alert
     * @param title Title of the dialog
     * @param message Message to display
     */
    private void displayAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
