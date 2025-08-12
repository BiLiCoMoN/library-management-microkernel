package br.edu.ifba.inf008.interfaces.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Loan {
    private int loanId;
    private int userId;
    private int bookId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    
    // Campos extras para exibição (joins com outras tabelas)
    private String userName;
    private String bookTitle;
    private String userEmail;
    private String bookAuthor;
    
    // Constructor for new loan
    public Loan(int userId, int bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = LocalDate.now();
        this.returnDate = null; // null = empréstimo ativo
    }
    
    // Constructor for existing loan (from database)
    public Loan(int loanId, int userId, int bookId, LocalDate loanDate, LocalDate returnDate) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }
    
    // Constructor with user and book info (for display)
    public Loan(int loanId, int userId, int bookId, LocalDate loanDate, LocalDate returnDate,
                String userName, String bookTitle, String userEmail, String bookAuthor) {
        this.loanId = loanId;
        this.userId = userId;
        this.bookId = bookId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.userName = userName;
        this.bookTitle = bookTitle;
        this.userEmail = userEmail;
        this.bookAuthor = bookAuthor;
    }
    
    // Business methods
    public boolean isActive() {
        return returnDate == null;
    }
    
    public boolean isOverdue() {
        if (!isActive()) return false;
        return loanDate.plusDays(14).isBefore(LocalDate.now()); // 14 dias de prazo
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(loanDate.plusDays(14), LocalDate.now());
    }
    
    public void returnBook() {
        this.returnDate = LocalDate.now();
    }
    
    // Getters and Setters
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    
    // Formatted display methods
    public String getLoanDateFormatted() {
        return loanDate != null ? loanDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
    
    public String getReturnDateFormatted() {
        return returnDate != null ? returnDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Active";
    }
    
    public String getStatus() {
        if (!isActive()) return "Returned";
        if (isOverdue()) return "Overdue (" + getDaysOverdue() + " days)";
        return "Active";
    }
    
    @Override
    public String toString() {
        return String.format("Loan #%d: %s borrowed %s on %s", 
                           loanId, userName != null ? userName : "User " + userId, 
                           bookTitle != null ? bookTitle : "Book " + bookId, 
                           getLoanDateFormatted());
    }
}
