package net.luporpi.dbtools.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

/**
 * Tools.
 */
public final class Tools {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

    /**
     * Constructor.
     */
    protected Tools() {
        throw new UnsupportedOperationException();
    }

    /**
     * merges connection properties into flyway properties.
     * 
     * @param connectionProperties
     * @param flywayProperties
     */
    public static void mergeProperties(final Properties connectionProperties, final Properties flywayProperties) {
        flywayProperties.setProperty("flyway.url", (String) flywayProperties.getOrDefault("flyway.url",
                connectionProperties.getOrDefault("database.url", "")));
        flywayProperties.setProperty("flyway.user", (String) flywayProperties.getOrDefault("flyway.user",
                connectionProperties.getOrDefault("database.user", "")));
        flywayProperties.setProperty("flyway.password", (String) flywayProperties.getOrDefault("flyway.password",
                connectionProperties.getOrDefault("database.password", "")));
        flywayProperties.setProperty("flyway.placeholders.DatabaseName",
                (String) flywayProperties.getOrDefault("flyway.placeholders.DatabaseName",
                        connectionProperties.getOrDefault("database.database", "")));
        flywayProperties.setProperty("flyway.placeholders.OutputDatabaseName",
                (String) flywayProperties.getOrDefault("flyway.placeholders.OutputDatabaseName",
                        connectionProperties.getOrDefault("database.outputdatabase", "")));
    }

    /**
     * loads a property file.
     * 
     * @param propertiesFile
     * @return
     * @throws ToolsException
     */
    public static Properties loadProperties(final String propertiesFile) throws ToolsException {
        final Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
        } catch (final IOException ex) {
            throw new ToolsException("unable to load properties file: " + propertiesFile, ex);
        }

        return properties;
    }

    /**
     * Init logger.
     * 
     * @param logProperties
     * @throws ToolsException
     */
    public static void initLogger(final String logProperties) throws ToolsException {
        final Properties log4jProperties = Tools.loadProperties(logProperties);

        PropertyConfigurator.configure(log4jProperties);
    }
}
