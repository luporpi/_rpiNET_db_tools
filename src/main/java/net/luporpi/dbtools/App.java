package net.luporpi.dbtools;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.CommandLineHelper;
import net.luporpi.dbtools.utils.Database;
import net.luporpi.dbtools.utils.Flyway;
import net.luporpi.dbtools.utils.Tools;
import net.luporpi.dbtools.utils.exceptions.DatabaseException;
import net.luporpi.dbtools.utils.exceptions.FlywayException;
import net.luporpi.dbtools.utils.exceptions.MainException;
import net.luporpi.dbtools.utils.exceptions.ToolsException;

/**
 * Main App.
 */
public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private CommandLineHelper mCmd;

    private App() {

    }

    private void init(String[] args) {
        try {
            // parse command line arguments
            mCmd = new CommandLineHelper();
            try {
                mCmd.parse(args);
            } catch (ToolsException ex) {
                throw new MainException("Error parsing command line", ex);
            }

            // init logger
            try {
                String logConf = mCmd.getLog();
                Tools.initLogger(logConf);
                LOGGER.info("Log configuration: " + logConf);
            } catch (ToolsException ex) {
                throw new MainException("Logging could not be initialized", ex);
            }
        } catch (MainException ex) {
            mCmd.printHelp();
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void run() throws MainException {
        // load properties
        Properties databaseProperties = null;
        Properties flywayProperties = null;

        String databaseConf = mCmd.getDatabase();
        LOGGER.info("Database configuration: " + databaseConf);
        String flywayConf = mCmd.getFlyway();
        LOGGER.info("Flyway configuration: " + flywayConf);

        try {
            databaseProperties = Tools.loadProperties(databaseConf);
            flywayProperties = Tools.loadProperties(flywayConf);
        } catch (ToolsException ex) {
            throw new MainException("Unable to load properties", ex);
        }

        Tools.mergeProperties(databaseProperties, flywayProperties);
        // load properties - END

        if (mCmd.isSQL2016()) {
            LOGGER.info("Skip scripts that require SQL Server 2016 or greater");
            flywayProperties.setProperty("flyway.locations", flywayProperties.getProperty("flyway.locations")
                    .replace("_rpiNET_SSFRK-luporpi", "_rpiNET_SSFRK-luporpi/all"));
        }

        // create database
        if (mCmd.isCreateDb()) {
            LOGGER.info("Create database");
            Database database = new Database(databaseProperties);
            try {
                database.init();
                database.createDatabase();
            } catch (DatabaseException ex) {
                throw new MainException("Unable to create database", ex);
            }
        }
        // create database - END

        // flyway
        Flyway flyway = new Flyway(flywayProperties);
        try {
            flyway.init();
            flyway.run();
        } catch (FlywayException ex) {
            throw new MainException("unable to create database", ex);
        }
        // flyway - END
    }

    /**
     * main method.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            App app = new App();

            app.init(args);
            app.run();

        } catch (MainException ex) {
            LOGGER.error("", ex);
        }
    }
}
