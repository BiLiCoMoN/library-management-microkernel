# Library Management System

A modular library management system built with Java 17, JavaFX, and a microkernel architecture supporting dynamic plugin loading.

## 🏗️ Architecture

This system follows a **microkernel architecture** where core functionality is provided by a minimal kernel, and features are implemented as independent, dynamically-loadable plugins.

### Core Components
- **Core Kernel**: Plugin management, database connection, UI coordination
- **Interfaces**: Common contracts for plugins and core services
- **Plugins**: Independent modules for specific functionalities

### Available Plugins
1. **UserManagement** - CRUD operations for library users
2. **BookManagement** - CRUD operations for book catalog  
3. **LoanManagement** - Handle book loans and returns
4. **ReportManagement** - Generate reports of borrowed books

## 🛠️ Technologies

- **Java 17** with text blocks and modern features
- **JavaFX 17** for rich desktop UI
- **Maven** for build management and dependency resolution
- **MariaDB** via JDBC for data persistence
- **Docker** for database containerization
- **Microkernel Pattern** for modular, extensible architecture

## 📋 Requirements

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- MariaDB (via provided Docker container)

## 🚀 Quick Start Guide

### 1. Setup Database (Docker)

```bash
# Navigate to Docker directory
cd "Docker-T2/docker-T2"

# Start MariaDB container
docker-compose up -d

# Verify container is running
docker ps | findstr bookstore
```

### 2. Build and Run

```bash
# Navigate to project
cd microkernel

# Compile, package and run in one command
mvn clean compile package exec:java -pl app
```

**OR use simplified command:**

```bash
# If already compiled before
mvn exec:java -pl app
```

### 3. Verification

When running, you should see:
- ✅ `Starting Bookstore Management System...`
- ✅ `Database connection: OK`
- ✅ `UserManagement plugin loaded successfully!`
- ✅ `BookManagement plugin loaded successfully!`
- ✅ `LoanManagement plugin loaded successfully!`
- ✅ `ReportManagement plugin loaded successfully!`

## 📁 Project Structure

```
microkernel/
├── pom.xml                      # Parent POM
├── README.md                    # This documentation
├── interfaces/                  # Core interfaces and contracts
│   └── src/main/java/br/edu/ifba/inf008/interfaces/
├── app/                         # Main application (microkernel)
│   └── src/main/java/br/edu/ifba/inf008/
└── plugins/                     # Plugin modules
    ├── usermanagement/          # User CRUD plugin
    ├── bookmanagement/          # Book CRUD plugin
    ├── loanmanagement/          # Loan management plugin
    ├── reports/                 # Reports plugin
    └── myplugin/               # Example plugin
```

## 🔌 Plugin Architecture

Each plugin is an independent module that:
- Implements the `IPlugin` interface
- Can be loaded/unloaded at runtime
- Has its own UI components and business logic
- Communicates with core through well-defined interfaces

### Adding New Plugins

1. Create new module in `plugins/` directory
2. Implement `IPlugin` interface
3. Add module to parent `pom.xml`
4. Compile: `mvn clean package`
5. JARs are automatically made available
6. Restart application - plugin loads automatically

## 🧪 Features

### User Management
- ✅ Add, edit, delete users
- ✅ Search and filter capabilities
- ✅ User profile management
- ✅ Unique email validation

### Book Management  
- ✅ Complete catalog management
- ✅ ISBN validation and duplicate checking
- ✅ Copy availability tracking
- ✅ Inventory control

### Loan Management
- ✅ Issue and return books
- ✅ Automatic availability updates
- ✅ Availability verification before loan
- ✅ Active loans filtering

### Reports
- ✅ Currently borrowed books report
- ✅ Detailed information (user, book, date)
- ✅ Intuitive and responsive interface

## 🏛️ Database Schema

The system uses the following tables:
- `users` (user_id, name, email, phone, address, active, created_at)
- `books` (book_id, title, author, isbn, published_year, copies_available)
- `loans` (loan_id, user_id, book_id, loan_date, return_date)

**Database Configuration:**
- **Host**: localhost:3307
- **Database**: bookstore
- **User**: root
- **Password**: root

## 🧑‍💻 Development

### Individual Plugin Compilation
```bash
# Build specific plugin
mvn clean package -pl plugins/usermanagement

# Build all plugins
mvn clean package
```

### Command Structure
```bash
# Clean build
mvn clean

# Compilation
mvn compile

# Packaging
mvn package

# Execution
mvn exec:java -pl app

# All in one command
mvn clean compile package exec:java -pl app
```

## 📚 Academic Context

This project was developed as part of the Object-Oriented Programming course (INF008) at IFBA, demonstrating:
- Advanced OOP concepts (interfaces, polymorphism, dynamic binding)
- Modular architecture patterns
- Plugin-based extensibility
- JavaFX desktop application development
- Database integration via JDBC

## 🎯 Course Requirements Fulfilled

- ✅ **User Management** - Complete CRUD
- ✅ **Book Management** - Complete CRUD with validations
- ✅ **Loan Management** - With availability control
- ✅ **Borrowed Books Report** - As per specification
- ✅ **Plugin System** - Modular architecture with dynamic loading
- ✅ **JavaFX Interface** - Rich and responsive graphical interface
- ✅ **MariaDB Integration** - Persistence via JDBC
- ✅ **Maven Build System** - Automated compilation and execution

## ⚙️ Technical Configurations

### Main Dependencies
- JavaFX Controls 17.0.2
- MariaDB Connector/J 3.1.4
- SLF4J for logging

### Maven Plugins
- Compiler Plugin 3.13.0
- Exec Plugin 3.0.0
- JavaFX Plugin 0.0.8

## 🔧 Troubleshooting

### Common Issues

**Database connection fails:**
```bash
# Check if Docker is running
docker ps

# Restart container if needed
docker-compose restart
```

**Plugins don't load:**
```bash
# Recompile everything
mvn clean compile package

# Check generated JARs
dir plugins\*/target\*.jar
```

**Interface doesn't open:**
- Verify Java 17+ is installed
- Check JavaFX is in dependencies
- Warning about "unnamed module" is normal

## 👨‍🎓 Author

**Jorge** - IFBA Student  
Course: Análise e Desenvolvimento de Sistemas  
Subject: INF008 - Programação Orientada a Objetos  
Professor: Sandro Santos Andrade

## 📅 Assignment Information

- **Deadline**: August 5th, 2025 at 23:59:59
- **Submission**: sandroandrade@ifba.edu.br
- **Subject**: INF008 T2 [Full Name]

## 📄 License

This project is part of academic coursework and is intended for educational purposes.

---

**System tested and working with 4 users, 3 books, and 3 active loans in Docker environment.**