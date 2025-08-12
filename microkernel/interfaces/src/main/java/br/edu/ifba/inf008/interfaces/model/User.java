package br.edu.ifba.inf008.interfaces.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo para representar um usuário da livraria
 */
public class User {
    private Integer userId;
    private String name;
    private String email;
    private LocalDateTime registeredAt;
    
    // Construtores
    public User() {}
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.registeredAt = LocalDateTime.now();
    }
    
    public User(Integer userId, String name, String email, LocalDateTime registeredAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.registeredAt = registeredAt;
    }
    
    // Getters e Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        this.name = name.trim();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !isValidEmail(email)) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email.trim().toLowerCase();
    }
    
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    
    // Validação de email
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s'}", userId, name, email);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
