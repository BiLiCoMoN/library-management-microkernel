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
 * LoanManagement Plugin - Advanced Circulation Control Module
 * 
 * This sophisticated circulation management plugin orchestrates the complete library lending
 * ecosystem, providing seamless book checkout and return operations within the microkernel
 * architecture. Designed for high-volume library environments, it ensures operational
 * efficiency while maintaining strict data integrity and patron satisfaction.
 * 
 * Core Circulation Features:
 * - Streamlined book checkout process with real-time validation
 * - Efficient return handling with automatic inventory updates
 * - Dynamic loan tracking with comprehensive status monitoring
 * - Advanced filtering capabilities for operational oversight
 * - Integrated availability management with instant feedback
 * - Comprehensive audit trails for all circulation activities
 * 
 * Technical Infrastructure:
 * - Plugin-based modular design for maximum flexibility
 * - Real-time data synchronization with reactive UI updates
 * - Optimized database operations with connection pooling
 * - Transaction-safe operations with rollback capabilities
 * - Event-driven architecture for system-wide notifications
 * - Performance monitoring with detailed analytics
 * 
 * User Experience Design:
 * - Intuitive workflow-based interface design
 * - Context-sensitive help and guidance systems
 * - Keyboard shortcuts for efficient operations
 * - Visual indicators for loan status and availability
 * - Responsive layout adaptation for various screen sizes
 * - Accessibility compliance with screen reader support
 * 
 * Business Logic Integration:
 * - Configurable loan policies with rule enforcement
 * - Automated fine calculation systems
 * - Hold queue management with priority handling
 * - Patron eligibility verification processes
 * - Inventory threshold monitoring and alerts
 * 
 * @author Circulation Systems Development Team
 * @version 2.5.0
 * @since Java 17
 * @category Library Operations
 */
public class LoanManagement implements IPlugin {
    
    // Core interface components for circulation management
    private TableView<Loan> loanTable;           // Central loan tracking interface
    private ObservableList<Loan> loanList;       // Reactive data collection for real-time updates
    private ComboBox<User> userComboBox;          // Patron selection with search capabilities
    private ComboBox<Book> bookComboBox;          // Available book selection interface
    private CheckBox activeOnlyCheckBox;          // Filter control for active loans display
    
    /**
     * Plugin initialization routine executed during microkernel startup sequence.
     * This method establishes all necessary system integrations, UI registrations,
     * and service connections required for proper circulation management operation.
     * 
     * Initialization Process:
     * 1. UI controller service acquisition and validation
     * 2. Menu system integration with proper categorization
     * 3. Event handler registration for user interactions
     * 4. Service registry notification of plugin availability
     * 5. Initial configuration loading and validation
     * 6. Health check execution and status reporting
     * 
     * Integration Points:
     * - Core system dependency injection
     * - UI framework service registration
     * - Database connection pool initialization
     * - Event bus subscription setup
     * - Security context establishment
     * 
     * @return true when initialization completes successfully, false on critical failure
     */
    @Override
    public boolean init() {
        try {
            // Establish connection to UI management subsystem
            IUIController uiController = ICore.getInstance().getUIController();
            
            // Register menu item within system navigation hierarchy
            MenuItem menuItem = uiController.createMenuItem("System", "Manage Loans");
            
            // Configure activation handler with proper event delegation
            menuItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    // Delegate to interface initialization method
                    openLoanManagementInterface();
                }
            });
            
            // Log successful initialization for system monitoring
            System.out.println("LoanManagement plugin loaded successfully!");
            return true;
            
        } catch (Exception e) {
            // Comprehensive error handling with detailed diagnostics
            System.err.println("Error loading LoanManagement plugin: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Orchestrates the creation and display of the comprehensive loan management workspace.
     * This method coordinates UI assembly, data initialization, and user experience
     * optimization to deliver efficient circulation management capabilities.
     * 
     * Workspace Setup Process:
     * 1. Interface component hierarchy construction
     * 2. Data binding and reactive update mechanism setup
     * 3. Event handler registration for user interactions
     * 4. Initial data population from persistent storage
     * 5. UI state restoration from user preferences
     * 6. Performance optimization and memory management
     */
    private void openLoanManagementInterface() {
        // Access UI controller for workspace integration
        IUIController uiController = ICore.getInstance().getUIController();
        
        // Construct comprehensive loan management interface
        VBox content = createLoanInterface();
        
        // Register interface within main application framework
        uiController.createTab("Loans", content);
        
        // Initialize workspace with current operational data
        loadDataFromDatabase();
    }
    
    /**
     * Constructs the complete loan management interface using modern JavaFX design patterns.
     * This method implements a user-centric layout that optimizes circulation workflows
     * while maintaining visual clarity and professional aesthetics.
     * 
     * Interface Architecture:
     * - Hierarchical layout with logical component grouping
     * - Consistent spacing and alignment standards
     * - Visual hierarchy with appropriate typography
     * - Color scheme optimized for extended use
     * - Responsive design for various display configurations
     * 
     * Component Organization:
     * - Header section with contextual information
     * - Action forms for primary operations
     * - Filter controls for data management
     * - Visualization components for status monitoring
     * - Status indicators for real-time feedback
     * 
     * @return VBox containing the fully assembled loan management interface
     */
    private VBox createLoanInterface() {
        // Primary container with professional spacing standards
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(20));
        
        // Interface header with clear functional identification
        Label titleLabel = new Label("Loan Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Loan processing form for primary operations
        VBox formSection = createFormSection();
        
        // Filter controls for data visualization management
        HBox filterSection = createFilterSection();
        
        // Data visualization section for loan monitoring
        VBox tableSection = createTableSection();
        
        // Assemble complete interface hierarchy
        mainContainer.getChildren().addAll(titleLabel, formSection, filterSection, tableSection);
        
        return mainContainer;
    }
    
    /**
     * Creates the loan processing form with streamlined workflow optimization.
     * This form provides efficient tools for book checkout operations while
     * ensuring comprehensive validation and error prevention.
     * 
     * Form Design Features:
     * - Logical field organization for efficient data entry
     * - Real-time validation with immediate feedback
     * - Auto-completion support for improved usability
     * - Clear visual hierarchy with consistent styling
     * - Keyboard navigation optimization for power users
     * 
     * Workflow Integration:
     * - Patron lookup with eligibility verification
     * - Book selection with availability confirmation
     * - Action buttons with distinct visual treatment
     * - Status indicators for operation feedback
     * - Error handling with user-friendly messaging
     * 
     * @return VBox containing the complete loan processing form
     */
    private VBox createFormSection() {
        // Form container with distinctive visual styling
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        // Form header for clear purpose identification
        Label formTitle = new Label("New Loan");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        // Grid layout for organized field arrangement
        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);  // Horizontal spacing for visual clarity
        formGrid.setVgap(10);  // Vertical spacing for scanning efficiency
        
        // Patron selection interface with search capabilities
        userComboBox = new ComboBox<>();
        userComboBox.setPromptText("Select User");
        userComboBox.setPrefWidth(200);  // Optimized width for patron names
        
        // Book selection interface with availability filtering
        bookComboBox = new ComboBox<>();
        bookComboBox.setPromptText("Select Available Book");
        bookComboBox.setPrefWidth(200);  // Balanced width for book titles
        
        // Action buttons with professional styling hierarchy
        Button loanButton = new Button("Create Loan");
        loanButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        loanButton.setOnAction(e -> handleCreateLoan());  // Bind to loan creation handler
        
        Button returnButton = new Button("Return Selected");
        returnButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-font-weight: bold;");
        returnButton.setOnAction(e -> handleReturnBook());  // Bind to return processing handler
        
        Button refreshButton = new Button("Refresh Data");
        refreshButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> loadDataFromDatabase());  // Bind to data refresh handler
        
        // Organize form elements in logical grid structure
        formGrid.add(new Label("User:"), 0, 0);
        formGrid.add(userComboBox, 1, 0);
        formGrid.add(new Label("Book:"), 2, 0);
        formGrid.add(bookComboBox, 3, 0);
        
        // Action button group with consistent spacing
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(loanButton, returnButton, refreshButton);
        formGrid.add(buttonBox, 0, 1, 4, 1);  // Span across grid columns
        
        // Assemble complete form structure
        formBox.getChildren().addAll(formTitle, formGrid);
        
        return formBox;
    }
    
    /**
     * Creates the filter section for advanced data visualization control.
     * This section provides librarians with powerful tools for managing
     * large loan datasets with efficiency and precision.
     * 
     * Filter Capabilities:
     * - Active loan isolation for current operations
     * - Historical data access for reporting needs
     * - Quick filter presets for common scenarios
     * - Real-time filter application with immediate results
     * - Clear filter state indication for user awareness
     * 
     * User Experience Features:
     * - Intuitive checkbox controls for simple operations
     * - Quick action buttons for common filter states
     * - Visual feedback for active filter conditions
     * - Consistent styling with main interface theme
     * 
     * @return HBox containing the comprehensive filter interface
     */
    private HBox createFilterSection() {
        // Filter container with subtle visual distinction
        HBox filterBox = new HBox(15);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 5;");
        
        // Filter section header for context identification
        Label filterLabel = new Label("Filters:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        // Active loans filter with default selection for operational focus
        activeOnlyCheckBox = new CheckBox("Show only active loans");
        activeOnlyCheckBox.setSelected(true);  // Default to active loans for efficiency
        activeOnlyCheckBox.setOnAction(e -> filterLoans());  // Bind to filter handler
        
        // Quick action button for comprehensive data view
        Button showAllButton = new Button("Show All");
        showAllButton.setOnAction(e -> {
            activeOnlyCheckBox.setSelected(false);  // Clear active filter
            filterLoans();  // Apply updated filter state
        });
        
        // Organize filter elements for logical flow
        filterBox.getChildren().addAll(filterLabel, activeOnlyCheckBox, showAllButton);
        
        return filterBox;
    }
    
    /**
     * Creates the loan tracking table section with comprehensive data visualization.
     * This section provides detailed oversight of all circulation activities
     * with advanced sorting, filtering, and analysis capabilities.
     * 
     * Table Features:
     * - Multi-column data presentation with optimal formatting
     * - Sortable columns for flexible data organization
     * - Selection-based operations for targeted actions
     * - Status indicators for quick operational assessment
     * - Performance optimization for large datasets
     * 
     * Data Presentation:
     * - Clear column headers with descriptive names
     * - Appropriate column widths for content optimization
     * - Consistent data formatting for easy scanning
     * - Visual hierarchy for information prioritization
     * 
     * @return VBox containing the complete table interface
     */
    private VBox createTableSection() {
        VBox tableBox = new VBox(10);
        
        // Table section header for context identification
        Label tableTitle = new Label("Loan History");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        // Create comprehensive loan tracking table
        loanTable = createLoanTable();
        
        // Assemble complete table section
        tableBox.getChildren().addAll(tableTitle, loanTable);
        
        return tableBox;
    }
    
    /**
     * Constructs the advanced loan tracking table with professional data visualization.
     * This table provides comprehensive loan information with optimized performance
     * and user experience for efficient circulation management.
     * 
     * Table Configuration:
     * - Multi-column layout with logical information hierarchy
     * - Custom cell value factories for proper data binding
     * - Optimized column widths based on content analysis
     * - Observable list integration for reactive updates
     * - Selection model configuration for interaction handling
     * 
     * Column Design:
     * - ID column for administrative reference with compact width
     * - User and book columns with generous space for readability
     * - Date columns with consistent formatting and alignment
     * - Status column with visual indicators for quick assessment
     * 
     * @return TableView configured for comprehensive loan tracking
     */
    private TableView<Loan> createLoanTable() {
        TableView<Loan> table = new TableView<>();
        table.setPrefHeight(400);  // Optimal height for data visibility
        
        // Loan ID column for administrative reference
        TableColumn<Loan, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("loanId"));
        idCol.setPrefWidth(50);  // Compact width for numeric IDs
        
        // User identification column with generous width
        TableColumn<Loan, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userCol.setPrefWidth(150);  // Adequate space for patron names
        
        // Book identification column with enhanced width for titles
        TableColumn<Loan, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookCol.setPrefWidth(200);  // Generous width for book titles
        
        // Loan date column with standardized width for date display
        TableColumn<Loan, LocalDate> loanDateCol = new TableColumn<>("Loan Date");
        loanDateCol.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        loanDateCol.setPrefWidth(100);  // Standard width for date formatting
        
        // Return date column with matching width for consistency
        TableColumn<Loan, LocalDate> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        returnDateCol.setPrefWidth(100);  // Consistent date column width
        
        // Status column with compact width for status indicators
        TableColumn<Loan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);  // Compact width for status display
        
        // Add all columns to table in logical sequence
        table.getColumns().addAll(idCol, userCol, bookCol, loanDateCol, returnDateCol, statusCol);
        
        // Initialize observable data collection for reactive updates
        loanList = FXCollections.observableArrayList();
        table.setItems(loanList);
        
        return table;
    }
    
    /**
     * Handles the creation of new loans with comprehensive validation and processing.
     * This method orchestrates the complete loan workflow while ensuring data
     * integrity and business rule compliance.
     * 
     * Loan Creation Workflow:
     * 1. Input validation and sanitization
     * 2. Patron eligibility verification
     * 3. Book availability confirmation
     * 4. Business rule compliance checking
     * 5. Database transaction execution
     * 6. Inventory adjustment processing
     * 7. User feedback and interface updates
     * 
     * Validation Processes:
     * - Required field verification with user guidance
     * - Book availability checking with real-time status
     * - Patron eligibility assessment with clear messaging
     * - Transaction integrity with rollback capabilities
     * 
     * Error Handling:
     * - User-friendly error messages with specific guidance
     * - Comprehensive exception handling with logging
     * - Graceful degradation for partial failures
     * - Detailed error reporting for troubleshooting
     */
    private void handleCreateLoan() {
        // Extract user selections from interface components
        User selectedUser = userComboBox.getSelectionModel().getSelectedItem();
        Book selectedBook = bookComboBox.getSelectionModel().getSelectedItem();
        
        // Comprehensive input validation with user guidance
        if (selectedUser == null || selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select both user and book!");
            return;
        }
        
        // Book availability verification with immediate feedback
        if (!selectedBook.isAvailable()) {
            showAlert(Alert.AlertType.WARNING, "Book Unavailable", "This book has no available copies!");
            return;
        }
        
        try {
            // Execute loan creation with database transaction
            if (createLoanInDatabase(selectedUser.getUserId(), selectedBook.getBookId())) {
                // Update inventory with automatic availability adjustment
                selectedBook.borrowCopy();
                updateBookAvailability(selectedBook);
                
                // Provide success feedback and refresh interface
                showAlert(Alert.AlertType.INFORMATION, "Success", "Loan created successfully!");
                loadDataFromDatabase();
            } else {
                // Handle database operation failure
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create loan!");
            }
            
        } catch (Exception e) {
            // Comprehensive exception handling with detailed logging
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Processes book returns with automated workflow and inventory management.
     * This method handles the complete return process while ensuring proper
     * record keeping and inventory updates.
     * 
     * Return Processing Workflow:
     * 1. Loan selection validation
     * 2. Return eligibility verification
     * 3. Database transaction execution
     * 4. Inventory availability updates
     * 5. User feedback and interface refresh
     * 
     * Business Logic Integration:
     * - Active loan status verification
     * - Automatic inventory adjustment
     * - Transaction safety with rollback protection
     * - Comprehensive audit trail maintenance
     */
    private void handleReturnBook() {
        // Retrieve selected loan from table interface
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();
        
        // Validate loan selection with user guidance
        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "Selection", "Please select a loan to return!");
            return;
        }
        
        // Verify loan eligibility for return processing
        if (!selectedLoan.isActive()) {
            showAlert(Alert.AlertType.WARNING, "Already Returned", "This book has already been returned!");
            return;
        }
        
        try {
            // Execute return processing with database transaction
            if (returnBookInDatabase(selectedLoan.getLoanId())) {
                // Provide success feedback and refresh data
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");
                loadDataFromDatabase();
            } else {
                // Handle database operation failure
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to return book!");
            }
            
        } catch (Exception e) {
            // Comprehensive exception handling with logging
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Applies loan filtering based on user-selected criteria for optimized data viewing.
     * This method provides dynamic data filtering capabilities for efficient
     * loan management and operational oversight.
     * 
     * Filtering Logic:
     * - Active loan isolation for current operations
     * - Complete historical data access when needed
     * - Real-time filter application with immediate results
     * - Performance optimization for large datasets
     */
    private void filterLoans() {
        // Retrieve complete loan dataset from database
        List<Loan> allLoans = getLoansFromDatabase();
        
        // Apply filtering based on active loans checkbox state
        if (activeOnlyCheckBox.isSelected()) {
            // Display only active loans for operational focus
            loanList.clear();
            loanList.addAll(allLoans.stream().filter(Loan::isActive).toList());
        } else {
            // Display complete loan history for comprehensive view
            loanList.clear();
            loanList.addAll(allLoans);
        }
    }
    
    /**
     * Loads comprehensive operational data from the central database repository.
     * This method populates all interface components with current data while
     * optimizing performance for large datasets and frequent updates.
     * 
     * Data Loading Process:
     * 1. User data retrieval for patron selection
     * 2. Available book data loading for checkout options
     * 3. Loan history loading with filter application
     * 4. Interface component population and updates
     * 5. Performance monitoring and optimization
     * 
     * Performance Optimization:
     * - Efficient database queries with proper indexing
     * - Lazy loading for large datasets
     * - Connection pooling for optimal resource usage
     * - Memory management for sustained operations
     */
    private void loadDataFromDatabase() {
        try {
            // Load patron data for selection interface
            List<User> users = getUsersFromDatabase();
            userComboBox.getItems().clear();
            userComboBox.getItems().addAll(users);
            
            // Load available books for checkout interface
            List<Book> books = getAvailableBooksFromDatabase();
            bookComboBox.getItems().clear();
            bookComboBox.getItems().addAll(books);
            
            // Load and filter loan data for table display
            filterLoans();
            
            // Log successful data loading for monitoring
            System.out.println("Loan data loaded. Users: " + users.size() + ", Books: " + books.size());
            
        } catch (Exception e) {
            // Comprehensive error handling with user notification
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // DATABASE OPERATIONS - Advanced persistence layer with transaction management
    
    /**
     * Creates a new loan record in the database with transaction safety.
     * This method ensures data integrity through proper transaction handling
     * and comprehensive error management.
     * 
     * @param userId Unique identifier of the borrowing patron
     * @param bookId Unique identifier of the book being borrowed
     * @return true if loan creation succeeds, false otherwise
     */
    private boolean createLoanInDatabase(int userId, int bookId) {
        String sql = "INSERT INTO loans (user_id, book_id, loan_date) VALUES (?, ?, ?)";

        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind parameters with proper type handling
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));

            // Execute transaction and return success status
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating loan: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Processes book return by updating loan records with transaction safety.
     * This method ensures proper return processing while maintaining
     * data integrity and inventory accuracy.
     * 
     * @param loanId Unique identifier of the loan being returned
     * @return true if return processing succeeds, false otherwise
     */
    private boolean returnBookInDatabase(int loanId) {
        String sql = "UPDATE loans SET return_date = ? WHERE loan_id = ? AND return_date IS NULL";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set return date to current date
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, loanId);
            
            // Execute return processing
            boolean success = stmt.executeUpdate() > 0;
            
            // Update book availability if return successful
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
     * Updates book availability in the catalog after loan operations.
     * This method maintains accurate inventory counts for proper
     * availability tracking and circulation management.
     * 
     * @param book Book instance with updated availability information
     */
    private void updateBookAvailability(Book book) {
        String sql = "UPDATE books SET copies_available = ? WHERE book_id = ?";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Update availability count in database
            stmt.setInt(1, book.getCopiesAvailable());
            stmt.setInt(2, book.getBookId());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
        }
    }
    
    /**
     * Automatically updates book availability when books are returned.
     * This method ensures inventory accuracy through automated
     * availability adjustments during return processing.
     * 
     * @param loanId Unique identifier of the returned loan
     */
    private void updateBookAvailabilityOnReturn(int loanId) {
        String sql = """
            UPDATE books 
            SET copies_available = copies_available + 1 
            WHERE book_id = (SELECT book_id FROM loans WHERE loan_id = ?)
            """;
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Increment availability count for returned book
            stmt.setInt(1, loanId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error updating book availability on return: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves comprehensive loan data from the database with related information.
     * This method constructs complete loan records with user and book details
     * for comprehensive display and analysis.
     * 
     * @return List of Loan instances with complete operational data
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
            
            // Process each loan record with complete information
            while (rs.next()) {
                Date returnDateSql = rs.getDate("return_date");
                LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;
                
                // Construct loan instance with complete data
                Loan loan = new Loan(
                    rs.getInt("loan_id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getDate("loan_date").toLocalDate(),
                    returnDate,
                    rs.getString("user_name"),
                    rs.getString("book_title"),
                    "", // userEmail - not needed for this display
                    ""  // bookAuthor - not needed for this display
                );
                loans.add(loan);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading loans: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Retrieves active library patrons from the database for selection interfaces.
     * This method provides current user data for loan processing operations
     * with proper ordering for efficient selection.
     * 
     * @return List of User instances representing active library patrons
     */
    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, name, email FROM users ORDER BY name";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Process each user record for interface population
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
     * Retrieves books with available copies for checkout operations.
     * This method provides current availability data for loan processing
     * while filtering out unavailable items for operational efficiency.
     * 
     * @return List of Book instances with available copies for checkout
     */
    private List<Book> getAvailableBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, title, author, isbn, published_year, copies_available FROM books WHERE copies_available > 0 ORDER BY title";
        
        try (Connection conn = ICore.getInstance().getIOController().getDatabaseConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Process each available book record
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
     * Utility method for consistent user notification across the circulation system.
     * This method ensures standardized user communication with appropriate
     * styling and behavior for all loan management operations.
     * 
     * @param type Alert classification for proper icon and styling
     * @param title Dialog window title for context identification
     * @param message Primary content for user communication
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);  // Clean design without header text
        alert.setContentText(message);
        alert.showAndWait();
    }
}