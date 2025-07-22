package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;
import br.edu.ifba.inf008.interfaces.model.Loan;

import javafx.scene.control.MenuItem;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportManagement implements IPlugin {
    private TableView<Loan> borrowedBooksTable;
    private ObservableList<Loan> borrowedBooksList;
    private Label totalBooksLabel;
    private Label overdueCountLabel;
    
    @Override
    public boolean init() {
        try {
            IUIController uiController = ICore.getInstance().getUIController();
            
            MenuItem menuItem = uiController.createMenuItem("Reports", "Borrowed Books Report");
            
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    openReportInterface();
                }
            });
            
            System.out.println("ReportManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error loading ReportManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void openReportInterface() {
        IUIController uiController = ICore.getInstance().getUIController();
        VBox content = createReportInterface();
        uiController.createTab("Borrowed Books Report", content);
        
        loadBorrowedBooksReport();
    }
    
    private VBox createReportInterface() {
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("Borrowed Books Report");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Statistics section
        VBox statsSection = createStatsSection();
        
        // Filter and action section
        HBox actionSection = createActionSection();
        
        // Table section
        VBox tableSection = createTableSection();
        
        mainContainer.getChildren().addAll(titleLabel, statsSection, actionSection, tableSection);
        
        return mainContainer;
    }
    
    private VBox createStatsSection() {
        VBox statsBox = new VBox(5);
        statsBox.setStyle("-fx-background-color: #e8f4fd; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label statsTitle = new Label("Summary");
        statsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        totalBooksLabel = new Label("Total borrowed books: Loading...");
        totalBooksLabel.setStyle("-fx-font-size: 12px;");
        
        overdueCountLabel = new Label("Overdue books: Loading...");
        overdueCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc3545;");
        
        statsBox.getChildren().addAll(statsTitle, totalBooksLabel, overdueCountLabel);
        
        return statsBox;
    }
    
    private HBox createActionSection() {
        HBox actionBox = new HBox(10);
        actionBox.setPadding(new Insets(10));
        
        Button refreshButton = new Button("Refresh Report");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshButton.setOnAction(e -> loadBorrowedBooksReport());
        
        Button exportButton = new Button("Export to Console");
        exportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        exportButton.setOnAction(e -> exportReportToConsole());
        
        actionBox.getChildren().addAll(refreshButton, exportButton);
        
        return actionBox;
    }
    
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        Label tableTitle = new Label("Currently Borrowed Books");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        // Create table
        borrowedBooksTable = createBorrowedBooksTable();
        
        tableBox.getChildren().addAll(tableTitle, borrowedBooksTable);
        
        return tableBox;
    }
    
    private TableView<Loan> createBorrowedBooksTable() {
        TableView<Loan> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Configure columns - conforme especificação
        TableColumn<Loan, String> titleCol = new TableColumn<>("Book Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        titleCol.setPrefWidth(200);
        
        TableColumn<Loan, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        authorCol.setPrefWidth(150);
        
        TableColumn<Loan, String> userCol = new TableColumn<>("Borrowed by");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userCol.setPrefWidth(150);
        
        TableColumn<Loan, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        loanDateCol.setPrefWidth(100);
        
        TableColumn<Loan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        
        table.getColumns().addAll(titleCol, authorCol, userCol, loanDateCol, statusCol);
        
        // Configure data
        borrowedBooksList = FXCollections.observableArrayList();
        table.setItems(borrowedBooksList);
        
        return table;
    }
    
    private void loadBorrowedBooksReport() {
        try {
            List<Loan> borrowedBooks = getBorrowedBooksFromDatabase();
            
            borrowedBooksList.clear();
            borrowedBooksList.addAll(borrowedBooks);
            
            // Update statistics
            updateStatistics(borrowedBooks);
            
            System.out.println("Borrowed books report loaded. Total: " + borrowedBooks.size());
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateStatistics(List<Loan> loans) {
        int totalBorrowed = loans.size();
        long overdueCount = loans.stream().filter(Loan::isOverdue).count();
        
        totalBooksLabel.setText("Total borrowed books: " + totalBorrowed);
        overdueCountLabel.setText("Overdue books: " + overdueCount);
    }
    
    private void exportReportToConsole() {
        System.out.println("\n=== BORROWED BOOKS REPORT ===");
        System.out.println("Generated on: " + LocalDate.now());
        System.out.println("Total borrowed books: " + borrowedBooksList.size());
        
        long overdueCount = borrowedBooksList.stream().filter(Loan::isOverdue).count();
        System.out.println("Overdue books: " + overdueCount);
        
        System.out.println("\nDetailed list:");
        System.out.println("Title | Author | Borrowed by | Loan Date | Status");
        System.out.println("---------------------------------------------------------------------");
        
        for (Loan loan : borrowedBooksList) {
            System.out.printf("%s | %s | %s | %s | %s%n",
                loan.getBookTitle(),
                loan.getBookAuthor() != null ? loan.getBookAuthor() : "Unknown",
                loan.getUserName(),
                loan.getLoanDate(),
                loan.getStatus()
            );
        }
        
        System.out.println("=== END OF REPORT ===\n");
        
        showAlert(Alert.AlertType.INFORMATION, "Export", "Report exported to console successfully!");
    }
    
    // DATABASE METHOD
    private List<Loan> getBorrowedBooksFromDatabase() {
        List<Loan> loans = new ArrayList<>();
        
        // Query conforme especificação: livros atualmente emprestados
        String sql = """
            SELECT l.loan_id, l.user_id, l.book_id, l.loan_date, l.return_date,
                   u.name as user_name, b.title as book_title, b.author as book_author
            FROM loans l
            JOIN users u ON l.user_id = u.user_id
            JOIN books b ON l.book_id = b.book_id
            WHERE l.return_date IS NULL
            ORDER BY l.loan_date DESC
            """;
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("loan_id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getDate("loan_date").toLocalDate(),
                    null, // return_date is NULL for borrowed books
                    rs.getString("user_name"),
                    rs.getString("book_title"),
                    "", // userEmail - not needed for report
                    rs.getString("book_author")
                );
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading borrowed books report: " + e.getMessage());
        }
        
        return loans;
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}