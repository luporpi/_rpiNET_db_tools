package net.luporpi.dbtools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.CommandLineHelper;
import net.luporpi.dbtools.utils.Tools;
import net.luporpi.dbtools.utils.exceptions.MainException;
import net.luporpi.dbtools.utils.exceptions.ToolsException;

/**
 * main App
 */
public final class App {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    private App() {
    }

    public static void main(String[] args) {
        try {
            try {
                Tools.initLogger();
            } catch (ToolsException ex) {
                throw new RuntimeException("initalisation error", ex);
            }

            try {
                Tools.loadNativeLibs();
            } catch (ToolsException ex) {
                logger.warn("unable to laod native libraries", ex);
            }

            CommandLineHelper cmd = new CommandLineHelper();
            try {
                cmd.parse(args);
            } catch (ToolsException ex) {
                logger.error("", ex);
                cmd.printHelp();
                System.exit(1);
            }

            Properties connectionProperties = null;
            Properties flywayProperties = null;

            String connection = cmd.get_connection();
            String flywayConf = cmd.get_flyway();

            try {
                connectionProperties = Tools.loadProperties(connection);
                flywayProperties = Tools.loadProperties(flywayConf);
            } catch (ToolsException ex) {
                throw new MainException("unable to load properties", ex);
            }

            Tools.mergeProperties(connectionProperties, flywayProperties);

            FluentConfiguration conf = new FluentConfiguration();
            conf.configuration(flywayProperties);

            Flyway flyway = new Flyway(conf);

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
                throw new MainException("unable to load T__rpinet_CollectInstall.sql", ex);
            }

            install = install.replaceAll("\\${2}\\{OutputDatabaseName\\}",
                    flywayProperties.getProperty("flyway.placeholders.OutputDatabaseName"));

            try {
                PreparedStatement stmt = flyway.getConfiguration().getDataSource().getConnection()
                        .prepareStatement(install);

                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            flywayProperties.setProperty("flyway.url", flywayProperties.getProperty("flyway.url").replaceAll("master",
                    flywayProperties.getProperty("flyway.placeholders.OutputDatabaseName")));

            conf = new FluentConfiguration();
            conf.configuration(flywayProperties);

            Flyway flyway2 = new Flyway(conf);

            flyway2.migrate();

        } catch (MainException ex) {
            logger.error("", ex);
        }
    }
}
