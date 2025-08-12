package br.edu.ifba.inf008.shell;

import br.edu.ifba.inf008.interfaces.IIOController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementação do IOController com suporte a banco de dados
 */
public class IOController implements IIOController {
    private static final Logger logger = LoggerFactory.getLogger(IOController.class);
    
    // Configurações do banco
    private final String url = "jdbc:mariadb://localhost:3307/bookstore";
    private final String username = "root";
    private final String password = "root";
    
    public IOController() {
        try {
            // Registrar driver MariaDB
            Class.forName("org.mariadb.jdbc.Driver");
            logger.info("Driver MariaDB carregado com sucesso");
        } catch (ClassNotFoundException e) {
            logger.error("Erro ao carregar driver MariaDB: {}", e.getMessage());
        }
    }
    
    @Override
    public Connection getDatabaseConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            logger.debug("Nova conexão estabelecida com o banco");
            return conn;
        } catch (SQLException e) {
            logger.error("Falha ao conectar com banco: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean testDatabaseConnection() {
        try (Connection conn = getDatabaseConnection()) {
            boolean isValid = conn.isValid(5);
            logger.info("Teste de conexão: {}", isValid ? "SUCESSO" : "FALHOU");
            return isValid;
        } catch (SQLException e) {
            logger.error("Teste de conexão falhou: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public void closeDatabaseConnections() {
        logger.info("Limpeza de conexões solicitada");
        // Em versão futura, usaremos pool de conexões
    }
}
