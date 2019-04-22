package net.luporpi.dbtools.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.DatabaseException;

public class Database {

    final static Logger logger = LoggerFactory.getLogger(Database.class);

    private Connection _conn = null;
    private Properties _connectionProperties = null;

    public Database(Properties connectionProperties) {
        _connectionProperties = connectionProperties;
    }

    public void Init() throws DatabaseException {

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", _connectionProperties.getProperty("database.user"));
        connectionProperties.put("password", _connectionProperties.getProperty("database.password"));

        try {
            String dataseUrl = _connectionProperties.getProperty("database.url");
            _conn = DriverManager.getConnection(dataseUrl, connectionProperties);
            logger.info("Connected to database: " + dataseUrl);
        } catch (SQLException ex) {
            throw new DatabaseException("unable to connect to database", ex);
        }
    }

    public void createDatabase() throws DatabaseException {
        String install = null;

        try (FileInputStream input = new FileInputStream("templates/db/T__rpinet_CollectInstall.sql")) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                install = sb.toString();
            }
        } catch (IOException ex) {
            throw new DatabaseException("unable to load T__rpinet_CollectInstall.sql", ex);
        }

        String outputdatabase = _connectionProperties.getProperty("database.outputdatabase");

        install = install.replaceAll("\\${2}\\{OutputDatabaseName\\}", outputdatabase);

        try {
            PreparedStatement stmt = _conn.prepareStatement(install);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logger.info("created database: " + outputdatabase);
    }
}