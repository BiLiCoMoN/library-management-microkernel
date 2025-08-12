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
4. **ReportManagement** - Generate reports and analytics

## 🛠️ Technologies

- **Java 17** with modern language features
- **JavaFX 17** for desktop user interface
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

When running successfully, you should see:
- ✅ `Starting Bookstore Management System...`
- ✅ `Database connection: OK`
- ✅ `UserManagement plugin loaded successfully!`
- ✅ `BookManagement plugin loaded successfully!`
- ✅ `LoanManagement plugin loaded successfully!`
- ✅ `ReportManagement plugin loaded successfully!`

## 📁 Project Structure

```
microkernel/
├── pom.xml                      # Parent POM configuration
├── README.md                    # This documentation
├── interfaces/                  # Core interfaces and contracts
│   └── src/main/java/br/edu/ifba/inf008/interfaces/
├── app/                         # Main application (microkernel)
│   └── src/main/java/br/edu/ifba/inf008/
└── plugins/                     # Plugin modules
    ├── usermanagement/          # User CRUD plugin
    ├── bookmanagement/          # Book CRUD plugin
    ├── loanmanagement/          # Loan management plugin
    └── reports/                 # Reports and analytics plugin
```

## 🔌 Plugin Architecture

Each plugin is an independent module that:
- Implements the `IPlugin` interface
- Can be loaded dynamically at runtime
- Has its own UI components and business logic
- Communicates with core through well-defined interfaces

### Adding New Plugins

1. Create new module in `plugins/` directory
2. Implement `IPlugin` interface
3. Add module to parent `pom.xml`
4. Compile: `mvn clean package`
5. JARs are automatically made available
6. Restart application - plugin loads automatically

## 🧪 System Features

### User Management
- ✅ Add, edit, delete users
- ✅ Search and filter capabilities
- ✅ User profile management
- ✅ Email validation and duplicate checking

### Book Management  
- ✅ Complete catalog management
- ✅ ISBN validation and duplicate prevention
- ✅ Available copies tracking
- ✅ Inventory control system

### Loan Management
- ✅ Issue and return books
- ✅ Automatic availability updates
- ✅ Copy availability verification
- ✅ Active loans filtering and tracking

### Reports and Analytics
- ✅ Borrowed books reports
- ✅ Circulation statistics
- ✅ User activity analysis
- ✅ Collection utilization metrics

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

## 🧑‍💻 Development Commands

### Build Commands
```bash
# Clean build
mvn clean

# Compilation only
mvn compile

# Create JAR packages
mvn package

# Run application
mvn exec:java -pl app

# Complete build and run
mvn clean compile package exec:java -pl app
```

### Individual Plugin Development
```bash
# Build specific plugin
mvn clean package -pl plugins/usermanagement

# Build all plugins
mvn clean package
```

## 📚 Academic Information

**Author**: Jorge Dário Costa de Santana (20241160003)  
**Course**: INF008 - Programação Orientada a Objetos  
**Professor**: Sandro Santos Andrade  
**Institution**: IFBA - Instituto Federal da Bahia  

This project demonstrates advanced object-oriented programming concepts including:
- Interface design and implementation
- Polymorphism and dynamic binding
- Modular architecture patterns
- Plugin-based extensibility
- JavaFX desktop application development
- Database integration using JDBC

## 🎯 Course Requirements Compliance

- ✅ **User Management** - Complete CRUD operations
- ✅ **Book Management** - Complete CRUD with validations
- ✅ **Loan Management** - With availability control system
- ✅ **Borrowed Books Report** - As specified in requirements
- ✅ **Plugin System** - Modular architecture with dynamic loading
- ✅ **JavaFX Interface** - Graphical user interface
- ✅ **MariaDB Integration** - Database persistence via JDBC
- ✅ **Maven Build System** - Automated compilation and execution

## ⚙️ Technical Configuration

### Main Dependencies
- JavaFX Controls 17.0.2
- MariaDB Connector/J 3.1.4
- SLF4J for logging

### Maven Plugins
- Maven Compiler Plugin 3.13.0
- Exec Maven Plugin 3.0.0
- JavaFX Maven Plugin 0.0.8

## 🔧 Troubleshooting

### Common Issues and Solutions

**Database connection problems:**
```bash
# Check if Docker container is running
docker ps

# Restart container if needed
docker-compose restart
```

**Plugins not loading:**
```bash
# Recompile all modules
mvn clean compile package

# Verify JAR files are created
dir plugins\*/target\*.jar
```

**Application interface issues:**
- Verify Java 17+ is installed and configured
- Check that JavaFX dependencies are resolved
- Note: Warning messages about "unnamed module" are expected and normal

## 📅 Assignment Details

- **Submission Deadline**: August 5th, 2025 at 23:59:59
- **Email**: sandroandrade@ifba.edu.br
- **Subject Format**: INF008 T2 Jorge Dário Costa de Santana

## 📊 System Status

**Testing Environment**: Successfully tested with sample data
- 4 registered users
- 3 books in catalog
- 3 active loans
- All plugins operational
- Docker MariaDB container running

## 📄 License

This project is developed for academic purposes as part of the Object-Oriented Programming course at IFBA.

---

**Note**: This system successfully implements all required functionalities using modern Java features, microkernel architecture, and plugin-based design patterns.