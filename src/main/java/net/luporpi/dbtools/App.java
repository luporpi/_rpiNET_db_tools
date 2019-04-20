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

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        Properties props = new Properties();

        try (FileInputStream input = new FileInputStream("conf/flyway.conf")) {
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        FluentConfiguration conf = new FluentConfiguration();
        conf.configuration(props);

        Flyway flyway = new Flyway(conf);

        String install = null;

        try (FileInputStream input = new FileInputStream("templates/T__rpinet_CollectInstall.sql")) {
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
            ex.printStackTrace();
        }

        install = install.replaceAll("\\${2}\\{OutputDatabaseName\\}", props.getProperty("flyway.placeholders.OutputDatabaseName"));

        try {
            PreparedStatement stmt = flyway.getConfiguration().getDataSource().getConnection()
                    .prepareStatement(install);

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        props.setProperty("flyway.url", props.getProperty("flyway.url").replaceAll("master", props.getProperty("flyway.placeholders.OutputDatabaseName")));

        conf = new FluentConfiguration();
        conf.configuration(props);

        Flyway flyway2 = new Flyway(conf);

        flyway2.migrate();
    }
}
