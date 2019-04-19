package net.luporpi.dbtools;

import java.io.FileInputStream;
import java.io.IOException;
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
        flyway.migrate();
    }
}
