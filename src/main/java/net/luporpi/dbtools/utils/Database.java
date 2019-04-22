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

/**
 * Database related stuff.
 */
public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private Connection mConnection;
    private Properties mConnectionProperties;

    /**
     * Constructor.
     * 
     * @param connectionProperties
     */
    public Database(Properties connectionProperties) {
        mConnectionProperties = connectionProperties;
    }

    /**
     * Init.
     * 
     * @throws DatabaseException
     */
    public void init() throws DatabaseException {

        Properties connectionProperties = new Properties();
        connectionProperties.put("user", mConnectionProperties.getProperty("database.user"));
        connectionProperties.put("password", mConnectionProperties.getProperty("database.password"));

        try {
            String dataseUrl = mConnectionProperties.getProperty("database.url");
            mConnection = DriverManager.getConnection(dataseUrl, connectionProperties);
            LOGGER.info("Connected to database: " + dataseUrl);
        } catch (SQLException ex) {
            throw new DatabaseException("unable to connect to database", ex);
        }
    }

    /**
     * create or updates collection database.
     * @throws DatabaseException
     */
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

        String outputdatabase = mConnectionProperties.getProperty("database.outputdatabase");

        install = install.replaceAll("\\${2}\\{OutputDatabaseName\\}", outputdatabase);

        try {
            PreparedStatement stmt = mConnection.prepareStatement(install);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LOGGER.info("created database: " + outputdatabase);
    }
}
