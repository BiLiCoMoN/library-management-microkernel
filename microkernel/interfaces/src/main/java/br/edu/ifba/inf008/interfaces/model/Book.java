package br.edu.ifba.inf008.interfaces.model;

import java.time.LocalDateTime;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private int publishedYear;  // ← AJUSTADO para coincidir com DB
    private int copiesAvailable; // ← AJUSTADO para coincidir com DB
    private LocalDateTime createdAt;
    
    // Constructor for new book
    public Book(String title, String author, String isbn, int publishedYear, int copiesAvailable) {
        this.title = validateTitle(title);
        this.author = validateAuthor(author);
        this.isbn = validateIsbn(isbn);
        this.publishedYear = validateYear(publishedYear);
        this.copiesAvailable = validateCopies(copiesAvailable);
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor for existing book (from database)
    public Book(int bookId, String title, String author, String isbn, 
                int publishedYear, int copiesAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.copiesAvailable = copiesAvailable;
        this.createdAt = LocalDateTime.now(); // Will be updated when loaded from DB
    }
    
    // Validation methods
    private String validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (title.length() > 255) {
            throw new IllegalArgumentException("Book title cannot exceed 255 characters");
        }
        return title.trim();
    }
    
    private String validateAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }
        if (author.length() > 255) {
            throw new IllegalArgumentException("Author name cannot exceed 255 characters");
        }
        return author.trim();
    }
    
    private String validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
        return isbn.trim();
    }
    
    private int validateYear(int year) {
        int currentYear = LocalDateTime.now().getYear();
        if (year < 1000 || year > currentYear + 5) {
            throw new IllegalArgumentException("Publication year must be between 1000 and " + (currentYear + 5));
        }
        return year;
    }
    
    private int validateCopies(int copies) {
        if (copies < 0) {
            throw new IllegalArgumentException("Number of copies cannot be negative");
        }
        return copies;
    }
    
    // Business methods
    public boolean isAvailable() {
        return copiesAvailable > 0;
    }
    
    public boolean borrowCopy() {
        if (copiesAvailable > 0) {
            copiesAvailable--;
            return true;
        }
        return false;
    }
    
    public boolean returnCopy() {
        copiesAvailable++;
        return true;
    }
    
    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = validateTitle(title); }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = validateAuthor(author); }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = validateIsbn(isbn); }
    
    public int getPublishedYear() { return publishedYear; }
    public void setPublishedYear(int publishedYear) { this.publishedYear = validateYear(publishedYear); }
    
    public int getCopiesAvailable() { return copiesAvailable; }
    public void setCopiesAvailable(int copiesAvailable) { this.copiesAvailable = validateCopies(copiesAvailable); }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return String.format("%s by %s (%d) - %d copies available", 
                           title, author, publishedYear, copiesAvailable);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }
    
    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}