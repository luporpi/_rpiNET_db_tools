package net.luporpi.dbtools.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

public final class Tools {

    final static Logger logger = LoggerFactory.getLogger(Tools.class);

    /**
     * 
     * @throws ToolsException
     */
    public static void loadNativeLibs() throws ToolsException {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            logger.info("loading native libraries");
            String nativeLibsPath = "libs/native/x64";
            if (System.getProperty("os.arch").toLowerCase() == "x86") {
                nativeLibsPath = "libs/native/x86";
            }
            System.setProperty("java.library.path", nativeLibsPath);

            Field fieldSysPath;
            try {
                fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);
            } catch (Exception ex) {
                throw new ToolsException("", ex);
            }
        }
    }

    /**
     * merges connection properties into flyway properties
     * 
     * @param connectionProperties
     * @param flywayProperties
     */
    public static void mergeProperties(Properties connectionProperties, Properties flywayProperties) {
        flywayProperties.setProperty("flyway.url",
                (String) flywayProperties.getOrDefault("flyway.url", connectionProperties.getProperty("database.url")));
        flywayProperties.setProperty("flyway.user", (String) flywayProperties.getOrDefault("flyway.user",
                connectionProperties.getProperty("database.user")));
        flywayProperties.setProperty("flyway.password", (String) flywayProperties.getOrDefault("flyway.password",
                connectionProperties.getProperty("database.password")));
        flywayProperties.setProperty("flyway.placeholders.DatabaseName",
                (String) flywayProperties.getOrDefault("flyway.placeholders.DatabaseName",
                        connectionProperties.getProperty("database.database")));
        flywayProperties.setProperty("flyway.placeholders.OutputDatabaseName",
                (String) flywayProperties.getOrDefault("flyway.placeholders.OutputDatabaseName",
                        connectionProperties.getProperty("database.outputdatabase")));
    }

    /**
     * loads a property file
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

    public static void initLogger(String logProperties) throws ToolsException {
        Properties log4jProperties = Tools.loadProperties(logProperties);

        PropertyConfigurator.configure(log4jProperties);
    }
}