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
2. **BookManagement** - CRUD operations for books catalog  
3. **LoanManagement** - Handle book loans and returns
4. **ReportManagement** - Generate reports of borrowed books

## 🛠️ Technologies

- **Java 17** with text blocks and modern features
- **JavaFX 17** for rich desktop UI
- **Maven** for build management and dependency resolution
- **MariaDB** via JDBC for data persistence
- **Microkernel Pattern** for modular, extensible architecture

## 📋 Requirements

- Java 17 or higher
- Maven 3.6+
- MariaDB database
- Docker (for database setup)

## 🚀 Quick Start

### 1. Clone the repository
```bash
git clone <repository-url>
cd microkernel
```

### 2. Setup Database (Docker)
```bash
# Start MariaDB container (if provided)
docker-compose up -d
```

### 3. Build and Run
```bash
# Build all modules
mvn clean install

# Run the application
mvn exec:java -pl app
```

## 📁 Project Structure

```
microkernel/
├── pom.xml                 # Parent POM
├── interfaces/             # Core interfaces and models
├── app/                    # Main application (microkernel)
└── plugins/                # Plugin modules
    ├── usermanagement/     # User CRUD plugin
    ├── bookmanagement/     # Book CRUD plugin
    ├── loanmanagement/     # Loan management plugin
    └── reports/            # Reports plugin
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
4. Place compiled JAR in `plugins/` directory
5. Restart application - plugin loads automatically

## 🧪 Features

### User Management
- ✅ Add, edit, delete users
- ✅ Search and filter capabilities
- ✅ User profile management

### Book Management  
- ✅ Complete book catalog management
- ✅ ISBN validation and duplicate checking
- ✅ Copy availability tracking

### Loan Management
- ✅ Issue and return books
- ✅ Automatic availability updates
- ✅ Overdue calculation and tracking
- ✅ Active loans filtering

### Reports
- ✅ Currently borrowed books report
- ✅ Overdue statistics
- ✅ Export capabilities

## 🏛️ Database Schema

The system expects the following tables:
- `users` (user_id, name, email, registration_date)
- `books` (book_id, title, author, isbn, published_year, copies_available)
- `loans` (loan_id, user_id, book_id, loan_date, return_date)

## 🧑‍💻 Development

### Building Individual Plugins
```bash
# Build specific plugin
mvn clean install -pl plugins/usermanagement

# Build all plugins
mvn clean install
```

### Running Tests
```bash
mvn test
```

## 📚 Academic Context

This project was developed as part of the Object-Oriented Programming course (INF008) at IFBA, demonstrating:
- Advanced OOP concepts (interfaces, polymorphism, dynamic binding)
- Modular architecture patterns
- Plugin-based extensibility
- JavaFX desktop application development
- Database integration with JDBC

## 🎯 Course Requirements Fulfilled

- ✅ User management (CRUD)
- ✅ Book management (CRUD) 
- ✅ Loan management with availability control
- ✅ Borrowed books reporting
- ✅ Plugin-based modular architecture
- ✅ JavaFX graphical interface
- ✅ MariaDB database integration
- ✅ Maven build system

## 👨‍🎓 Author

**Jorge** - IFBA Student  
Course: Análise e Desenvolvimento de Sistemas  
Subject: INF008 - Programação Orientada a Objetos  
Professor: Sandro Santos Andrade

## 📄 License

This project is part of academic coursework and is intended for educational purposes.