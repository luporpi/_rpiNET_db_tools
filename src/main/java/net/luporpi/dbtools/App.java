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
 * main App
 */
public final class App {

    final static Logger logger = LoggerFactory.getLogger(App.class);

    private CommandLineHelper _cmd = null;

    private App() {

    }

    private void init(String[] args) {
        try {
            // parse command line arguments
            _cmd = new CommandLineHelper();
            try {
                _cmd.parse(args);
            } catch (ToolsException ex) {
                throw new MainException("Error parsing command line", ex);
            }

            // init logger
            try {
                String logConf = _cmd.get_log();
                Tools.initLogger(logConf);
                logger.info("Log configuration: " + logConf);
            } catch (ToolsException ex) {
                throw new MainException("Logging could not be initialized", ex);
            }

            // load native libs
            try {
                Tools.loadNativeLibs();
            } catch (ToolsException ex) {
                logger.warn("Unable to laod native libraries", ex);
            }
        } catch (MainException ex) {
            _cmd.printHelp();
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void run() throws MainException {
        // load properties
        Properties databaseProperties = null;
        Properties flywayProperties = null;

        String databaseConf = _cmd.get_database();
        logger.info("Database configuration: " + databaseConf);
        String flywayConf = _cmd.get_flyway();
        logger.info("Flyway configuration: " + flywayConf);

        try {
            databaseProperties = Tools.loadProperties(databaseConf);
            flywayProperties = Tools.loadProperties(flywayConf);
        } catch (ToolsException ex) {
            throw new MainException("Unable to load properties", ex);
        }

        Tools.mergeProperties(databaseProperties, flywayProperties);
        // load properties - END

        // create database
        if (_cmd.is_createdb()) {
            logger.info("Create database");
            Database database = new Database(databaseProperties);
            try {
                database.Init();
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

    public static void main(String[] args) {
        try {
            App app = new App();

            app.init(args);
            app.run();

        } catch (MainException ex) {
            logger.error("", ex);
        }
    }
}
