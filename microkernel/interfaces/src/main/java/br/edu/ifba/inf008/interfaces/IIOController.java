package br.edu.ifba.inf008.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface expandida para incluir operações de banco de dados
 */
public interface IIOController {
    /**
     * Obtém uma conexão com o banco de dados
     * @return Connection ativa
     * @throws SQLException em caso de erro
     */
    Connection getDatabaseConnection() throws SQLException;
    
    /**
     * Testa se a conexão com banco está funcionando
     * @return true se conseguir conectar
     */
    boolean testDatabaseConnection();
    
    /**
     * Fecha todas as conexões ativas
     */
    void closeDatabaseConnections();
}
