package net.luporpi.dbtools.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
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
     * Load native libraries.
     * 
     * @throws ToolsException
     */
    public static void loadNativeLibs() throws ToolsException {
        StringBuilder nativeLibsPath = new StringBuilder();
        File jarpath;

        try {
            jarpath = new File(Tools.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            throw new ToolsException("", ex);
        }

        nativeLibsPath.append(jarpath.getParentFile().getAbsolutePath());

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            LOGGER.info("loading native libraries");
            if ((System.getProperty("os.arch").toLowerCase().compareTo("x86") == 0)
                    || (System.getProperty("sun.arch.data.model").toLowerCase().compareTo("32") == 0)) {
                nativeLibsPath.append("/libs/native/x86");
            } else {
                nativeLibsPath.append("/libs/native/x64");
            }

            nativeLibsPath.append("/sqljdbc_auth.dll");

            try {
                System.load(nativeLibsPath.toString());
            } catch (SecurityException ex) {
                throw new ToolsException("", ex);
            }
        }
    }

    /**
     * merges connection properties into flyway properties.
     * 
     * @param connectionProperties
     * @param flywayProperties
     */
    public static void mergeProperties(Properties connectionProperties, Properties flywayProperties) {
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
    public static Properties loadProperties(String propertiesFile) throws ToolsException {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
        } catch (IOException ex) {
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
    public static void initLogger(String logProperties) throws ToolsException {
        Properties log4jProperties = Tools.loadProperties(logProperties);

        PropertyConfigurator.configure(log4jProperties);
    }
}
