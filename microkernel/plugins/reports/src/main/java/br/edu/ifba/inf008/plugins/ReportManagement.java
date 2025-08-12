package br.edu.ifba.inf008.plugins;

import br.edu.ifba.inf008.interfaces.IPlugin;
import br.edu.ifba.inf008.interfaces.ICore;
import br.edu.ifba.inf008.interfaces.IUIController;

import javafx.scene.control.MenuItem;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * ReportManagement Plugin - Basic Library Reports
 * 
 * This plugin provides simple reporting for the library system.
 * It shows currently borrowed books and basic circulation information
 * using tables and simple charts.
 * 
 * Features:
 * - Show borrowed books report (assignment requirement)
 * - Simple circulation charts
 * - Basic loan statistics
 * - Date filtering for reports
 * - Simple export options (placeholder)
 * 
 * Technical Components:
 * - JavaFX charts for basic data visualization
 * - Tabbed interface for organizing different views
 * - Date pickers for selecting time periods
 * - TableView for displaying borrowed books data
 * - Simple styling for a clean interface
 * 
 * @author Jorge Dário Costa de Santana (20241160003)
 * @course INF008 - Programadação Orientada a Objetos
 * @professor Sandro Santos Andrade
 * @institution IFBA - Instituto Federal da Bahia
 * @version 1.0
 * @since Java 17
 */
public class ReportManagement implements IPlugin {
    
    // Main UI components for the reporting interface
    private TabPane reportTabs;                     // Container for different report views
    private PieChart circulationChart;              // Chart showing circulation by category
    private TableView<ReportData> detailTable;      // Table with detailed metrics
    private ObservableList<ReportData> reportData;  // Data collection for the table
    private DatePicker startDatePicker;             // Start date for report period
    private DatePicker endDatePicker;               // End date for report period
    private ComboBox<String> reportTypeSelector;    // Dropdown to select report type
    private Label metricsOverview;                  // Summary statistics display
    
    /**
     * Initializes the plugin and adds it to the system menu.
     * Sets up the menu item and event handler for opening the reports interface.
     * 
     * @return true if initialization is successful, false otherwise
     */
    @Override
    public boolean init() {
        try {
            // Get the UI controller from the core system
            IUIController uiController = ICore.getInstance().getUIController();
            
            // Create menu item for reports in the Analytics menu
            MenuItem reportsMenuItem = uiController.createMenuItem("Analytics", "Library Reports");
            
            // Set up the click handler to open the reports interface
            reportsMenuItem.setOnAction(e -> openReportsInterface());
            
            // Log successful loading
            System.out.println("ReportManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            // Handle any errors during initialization
            System.err.println("Error loading ReportManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates and displays the main reports interface.
     * Builds the complete UI and loads initial data from the database.
     */
    private void openReportsInterface() {
        // Get UI controller to create the tab
        IUIController uiController = ICore.getInstance().getUIController();
        
        // Build the complete reports interface
        BorderPane reportsContent = createReportsInterface();
        
        // Add the interface as a new tab
        uiController.createTab("Reports & Analytics", reportsContent);
        
        // Load data from database to populate the interface
        loadReportData();
    }
    
    /**
     * Builds the complete reports interface using BorderPane layout.
     * Creates header, main content area, control panel, and status bar.
     * 
     * @return BorderPane containing the full reports interface
     */
    private BorderPane createReportsInterface() {
        // Main container for the reports interface
        BorderPane mainContainer = new BorderPane();
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");
        
        // Create header section with title and summary metrics
        VBox headerSection = createHeaderSection();
        mainContainer.setTop(headerSection);
        
        // Create central area with tabs for different reports
        TabPane centralArea = createReportTabs();
        mainContainer.setCenter(centralArea);
        
        // Create left panel with filters and controls
        VBox controlPanel = createControlPanel();
        mainContainer.setLeft(controlPanel);
        
        // Create bottom status bar
        HBox statusBar = createStatusBar();
        mainContainer.setBottom(statusBar);
        
        return mainContainer;
    }
    
    /**
     * Creates the header section with title and key metrics overview.
     * 
     * @return VBox containing the header elements
     */
    private VBox createHeaderSection() {
        VBox header = new VBox(15);
        header.setPadding(new Insets(25));
        header.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        
        // Main title
        Label title = new Label("Library Reports Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Key metrics summary
        HBox metricsPanel = createMetricsPanel();
        
        header.getChildren().addAll(title, metricsPanel);
        return header;
    }
    
    /**
     * Creates a panel showing key library metrics with real data.
     * 
     * @return HBox containing metric cards
     */
    private HBox createMetricsPanel() {
        HBox metricsPanel = new HBox(25);
        metricsPanel.setStyle("-fx-alignment: center; -fx-padding: 20 0;");
        
        // Get real counts from database
        int activeLoans = getCount("SELECT COUNT(*) FROM loans WHERE return_date IS NULL");
        int totalBooks = getCount("SELECT COUNT(*) FROM books");
        int totalUsers = getCount("SELECT COUNT(*) FROM users");
        int monthlyLoans = getCount("SELECT COUNT(*) FROM loans WHERE MONTH(loan_date) = MONTH(CURDATE())");
        
        // Create individual metric cards with real data
        VBox activeLoansCard = createMetricCard("Active Loans", String.valueOf(activeLoans), "#27ae60");
        VBox totalBooksCard = createMetricCard("Total Books", String.valueOf(totalBooks), "#3498db");
        VBox totalUsersCard = createMetricCard("Registered Users", String.valueOf(totalUsers), "#f39c12");
        VBox monthlyLoansCard = createMetricCard("This Month", String.valueOf(monthlyLoans), "#e74c3c");
        
        metricsPanel.getChildren().addAll(activeLoansCard, totalBooksCard, totalUsersCard, monthlyLoansCard);
        return metricsPanel;
    }
    
    /**
     * Creates a single metric card for the dashboard.
     * 
     * @param title The metric name
     * @param value The metric value
     * @param color The card color
     * @return VBox containing the metric card
     */
    private VBox createMetricCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; " +
                     "-fx-background-radius: 10; -fx-alignment: center; -fx-min-width: 180;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-weight: bold;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    /**
     * Creates the tabbed area with different report categories.
     * 
     * @return TabPane containing all report tabs
     */
    private TabPane createReportTabs() {
        reportTabs = new TabPane();
        reportTabs.setStyle("-fx-tab-min-width: 120;");
        
        // Create individual tabs for different report types
        Tab overviewTab = new Tab("Overview");
        overviewTab.setContent(createOverviewTab());
        overviewTab.setClosable(false);
        
        Tab circulationTab = new Tab("Circulation");
        circulationTab.setContent(createCirculationTab());
        circulationTab.setClosable(false);
        
        Tab booksTab = new Tab("Books");
        booksTab.setContent(createBooksTab());
        booksTab.setClosable(false);
        
        Tab usersTab = new Tab("Users");
        usersTab.setContent(createUsersTab());
        usersTab.setClosable(false);
        
        // Add all tabs to the tab pane
        reportTabs.getTabs().addAll(overviewTab, circulationTab, booksTab, usersTab);
        
        return reportTabs;
    }
    
    /**
     * Creates the overview tab with real statistics from database.
     * 
     * @return VBox containing overview content
     */
    private VBox createOverviewTab() {
        VBox overview = new VBox(20);
        overview.setPadding(new Insets(25));
        
        Label tabTitle = new Label("Library Overview");
        tabTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Get real statistics from database
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(15);
        
        int totalBooks = getCount("SELECT COUNT(*) FROM books");
        int totalUsers = getCount("SELECT COUNT(*) FROM users");
        int activeLoans = getCount("SELECT COUNT(*) FROM loans WHERE return_date IS NULL");
        
        // Display real statistics
        statsGrid.add(new Label("Total Collection:"), 0, 0);
        statsGrid.add(new Label(totalBooks + " books"), 1, 0);
        
        statsGrid.add(new Label("Active Members:"), 0, 1);
        statsGrid.add(new Label(totalUsers + " users"), 1, 1);
        
        statsGrid.add(new Label("Current Loans:"), 0, 2);
        statsGrid.add(new Label(activeLoans + " active"), 1, 2);
        
        overview.getChildren().addAll(tabTitle, statsGrid);
        return overview;
    }
    
    /**
     * Creates the circulation tab with loan statistics and charts.
     * 
     * @return VBox containing circulation content
     */
    private VBox createCirculationTab() {
        VBox circulation = new VBox(20);
        circulation.setPadding(new Insets(25));
        
        Label tabTitle = new Label("Circulation Analytics");
        tabTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Apenas o pie chart - remover bar chart
        circulationChart = createCirculationChart();
        
        // Centralizar o chart
        HBox chartContainer = new HBox();
        chartContainer.setStyle("-fx-alignment: center;");
        chartContainer.getChildren().add(circulationChart);
        
        circulation.getChildren().addAll(tabTitle, chartContainer);
        return circulation;
    }
    
    /**
     * Creates the books tab with collection statistics.
     * 
     * @return VBox containing books content
     */
    private VBox createBooksTab() {
        VBox books = new VBox(20);
        books.setPadding(new Insets(25));
        
        Label tabTitle = new Label("Collection Analysis");
        tabTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Create detailed table for book statistics
        detailTable = createDetailTable();
        
        books.getChildren().addAll(tabTitle, detailTable);
        return books;
    }
    
    /**
     * Creates the users tab with member statistics.
     * 
     * @return VBox containing users content
     */
    private VBox createUsersTab() {
        VBox users = new VBox(20);
        users.setPadding(new Insets(25));
        
        Label tabTitle = new Label("User Activity");
        tabTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Add user-related statistics and charts here
        Label placeholder = new Label("User activity reports will be displayed here");
        placeholder.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        users.getChildren().addAll(tabTitle, placeholder);
        return users;
    }
    
    /**
     * Creates the control panel with filters and export options.
     * 
     * @return VBox containing control elements
     */
    private VBox createControlPanel() {
        VBox controlPanel = new VBox(20);
        controlPanel.setPadding(new Insets(25));
        controlPanel.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; " +
                             "-fx-border-width: 0 1 0 0; -fx-min-width: 250;");
        
        Label controlTitle = new Label("Report Controls");
        controlTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Date range selection
        VBox dateSection = createDateControls();
        
        // Report type selection
        VBox typeSection = createTypeControls();
        
        // Export options
        VBox exportSection = createExportControls();
        
        controlPanel.getChildren().addAll(
            controlTitle,
            new Separator(),
            dateSection,
            new Separator(),
            typeSection,
            new Separator(),
            exportSection
        );
        
        return controlPanel;
    }
    
    /**
     * Creates date range selection controls.
     * 
     * @return VBox containing date controls
     */
    private VBox createDateControls() {
        VBox dateSection = new VBox(10);
        
        Label dateLabel = new Label("Date Range");
        dateLabel.setStyle("-fx-font-weight: bold;");
        
        startDatePicker = new DatePicker(LocalDate.now().minusMonths(1));
        startDatePicker.setPromptText("Start Date");
        
        endDatePicker = new DatePicker(LocalDate.now());
        endDatePicker.setPromptText("End Date");
        
        Button applyBtn = new Button("Apply");
        applyBtn.setOnAction(e -> loadReportData());
        
        dateSection.getChildren().addAll(dateLabel, startDatePicker, endDatePicker, applyBtn);
        return dateSection;
    }
    
    /**
     * Creates report type selection controls.
     * 
     * @return VBox containing type controls
     */
    private VBox createTypeControls() {
        VBox typeSection = new VBox(10);
        
        Label typeLabel = new Label("Report Type");
        typeLabel.setStyle("-fx-font-weight: bold;");
        
        reportTypeSelector = new ComboBox<>();
        reportTypeSelector.getItems().addAll(
            "All Reports",
            "Circulation Only",
            "Books Only",
            "Users Only"
        );
        reportTypeSelector.setValue("All Reports");
        reportTypeSelector.setOnAction(e -> updateReportView());
        
        typeSection.getChildren().addAll(typeLabel, reportTypeSelector);
        return typeSection;
    }
    
    /**
     * Creates export controls for different file formats.
     * 
     * @return VBox containing export buttons
     */
    private VBox createExportControls() {
        VBox exportSection = new VBox(10);
        
        Label exportLabel = new Label("Export");
        exportLabel.setStyle("-fx-font-weight: bold;");
        
        Button pdfBtn = new Button("Export PDF");
        pdfBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        pdfBtn.setOnAction(e -> exportToPDF());
        
        Button excelBtn = new Button("Export Excel");
        excelBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        excelBtn.setOnAction(e -> exportToExcel());
        
        Button csvBtn = new Button("Export CSV");
        csvBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        csvBtn.setOnAction(e -> exportToCSV());
        
        exportSection.getChildren().addAll(exportLabel, pdfBtn, excelBtn, csvBtn);
        return exportSection;
    }
    
    /**
     * Creates the status bar with refresh controls.
     * 
     * @return HBox containing status elements
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(15));
        statusBar.setStyle("-fx-background-color: #ecf0f1;");
        
        Label statusLabel = new Label("Last Updated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        Button refreshBtn = new Button("Refresh Data");
        refreshBtn.setOnAction(e -> loadReportData());
        
        statusBar.getChildren().addAll(statusLabel, refreshBtn);
        return statusBar;
    }
    
    /**
     * Creates a pie chart showing loans by individual books with real data.
     * 
     * @return PieChart with circulation data
     */
    private PieChart createCirculationChart() {
        circulationChart = new PieChart();
        circulationChart.setTitle("Active Loans by Book");
        circulationChart.setPrefSize(350, 250);
        
        try (Connection conn = getConnection()) {
            // Query para mostrar livros emprestados individualmente
            String query = """
                SELECT b.title, COUNT(*) as loan_count
                FROM loans l 
                JOIN books b ON l.book_id = b.book_id 
                WHERE l.return_date IS NULL 
                GROUP BY b.book_id, b.title
                ORDER BY loan_count DESC
                """;
            
            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                boolean hasData = false;
                while (rs.next()) {
                    String bookTitle = rs.getString("title");
                    int loanCount = rs.getInt("loan_count");
                    chartData.add(new PieChart.Data(bookTitle, loanCount));
                    hasData = true;
                }
                
                // Se não há empréstimos, mostrar placeholder
                if (!hasData) {
                    chartData.add(new PieChart.Data("No Active Loans", 1));
                }
            }
            
            circulationChart.setData(chartData);
            
        } catch (SQLException e) {
            System.err.println("Error loading chart data: " + e.getMessage());
            ObservableList<PieChart.Data> fallbackData = FXCollections.observableArrayList(
                new PieChart.Data("Database Error", 1)
            );
            circulationChart.setData(fallbackData);
        }
        
        return circulationChart;
    }
    
    /**
     * Creates a table showing detailed report data.
     * 
     * @return TableView with detailed metrics
     */
    private TableView<ReportData> createDetailTable() {
        TableView<ReportData> table = new TableView<>();
        table.setPrefHeight(300);
        
        // Create table columns
        TableColumn<ReportData, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);
        
        TableColumn<ReportData, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalItems"));
        totalCol.setPrefWidth(80);
        
        TableColumn<ReportData, Integer> loanedCol = new TableColumn<>("Loaned");
        loanedCol.setCellValueFactory(new PropertyValueFactory<>("circulated"));
        loanedCol.setPrefWidth(80);
        
        TableColumn<ReportData, String> percentCol = new TableColumn<>("Usage %");
        percentCol.setCellValueFactory(new PropertyValueFactory<>("utilizationRate"));
        percentCol.setPrefWidth(80);
        
        table.getColumns().addAll(categoryCol, totalCol, loanedCol, percentCol);
        
        // Initialize data list
        reportData = FXCollections.observableArrayList();
        table.setItems(reportData);
        
        return table;
    }
    
    /**
     * Updates the report view based on selected filters.
     */
    private void updateReportView() {
        String selectedType = reportTypeSelector.getValue();
        System.out.println("Updating view for: " + selectedType);
        loadReportData();
    }
    
    /**
     * Loads report data from the database and updates all components.
     */
    private void loadReportData() {
        try {
            // Load data for charts
            loadChartData();
            
            // Load data for detailed table
            loadTableData();
            
            System.out.println("Report data loaded successfully");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Data Error", 
                     "Failed to load report data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads data for the charts from database.
     */
    private void loadChartData() {
        // Implementation would load actual data from database
        System.out.println("Loading chart data...");
    }
    
    /**
     * Loads real data for the detailed table - books and their loan status.
     */
    private void loadTableData() {
        reportData.clear();
        
        try (Connection conn = getConnection()) {
            // Query para mostrar cada livro e seus empréstimos
            String query = """
                SELECT 
                    b.title as book_title,
                    b.copies_available as total_copies,
                    COUNT(l.loan_id) as times_loaned,
                    SUM(CASE WHEN l.return_date IS NULL THEN 1 ELSE 0 END) as currently_loaned
                FROM books b
                LEFT JOIN loans l ON b.book_id = l.book_id
                GROUP BY b.book_id, b.title, b.copies_available
                ORDER BY times_loaned DESC
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String bookTitle = rs.getString("book_title");
                    int totalCopies = rs.getInt("total_copies");
                    int currentlyLoaned = rs.getInt("currently_loaned");
                    int timesLoaned = rs.getInt("times_loaned");
                    
                    String status = currentlyLoaned > 0 ? "Loaned" : "Available";
                    
                    reportData.add(new ReportData(bookTitle, totalCopies, currentlyLoaned, status));
                }
            }
            
            if (reportData.isEmpty()) {
                reportData.add(new ReportData("No Books", 0, 0, "N/A"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading table data: " + e.getMessage());
            reportData.add(new ReportData("Database Error", 0, 0, "Error"));
        }
    }
    
    /**
     * Exports current report data to PDF format.
     */
    private void exportToPDF() {
        showAlert(Alert.AlertType.INFORMATION, "Export", 
                 "PDF export feature will be implemented here.");
    }
    
    /**
     * Exports current report data to Excel format.
     */
    private void exportToExcel() {
        showAlert(Alert.AlertType.INFORMATION, "Export", 
                 "Excel export feature will be implemented here.");
    }
    
    /**
     * Exports current report data to CSV format.
     */
    private void exportToCSV() {
        showAlert(Alert.AlertType.INFORMATION, "Export", 
                 "CSV export feature will be implemented here.");
    }
    
    /**
     * Shows an alert dialog to the user.
     * 
     * @param type The type of alert
     * @param title The dialog title
     * @param message The message to display
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Data class for report table entries.
     * Represents statistical data for different categories.
     */
    public static class ReportData {
        private String category;
        private int totalItems;
        private int circulated;
        private String utilizationRate;
        
        public ReportData(String category, int totalItems, int circulated, String utilizationRate) {
            this.category = category;
            this.totalItems = totalItems;
            this.circulated = circulated;
            this.utilizationRate = utilizationRate;
        }
        
        // Getters for TableView
        public String getCategory() { return category; }
        public int getTotalItems() { return totalItems; }
        public int getCirculated() { return circulated; }
        public String getUtilizationRate() { return utilizationRate; }
    }
    
    /**
     * Gets a simple database connection to read data.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mariadb://localhost:3307/bookstore", 
            "root", 
            "root"
        );
    }

    /**
     * Gets count from a simple SQL query.
     */
    private int getCount(String sql) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next() ? rs.getInt(1) : 0;
            
        } catch (SQLException e) {
            System.err.println("Database read error: " + e.getMessage());
            return 0; // Fallback to 0 if error
        }
    }
}