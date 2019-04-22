package net.luporpi.dbtools.utils;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import net.luporpi.dbtools.utils.exceptions.FlywayException;
import net.luporpi.dbtools.utils.exceptions.ToolsException;

public class FlywayTest {

    /**
     * test init flyway
     */
    @Test
    public void testInit() {
        Properties flywayProperties = loadProperties();

        Flyway flyway = new Flyway(flywayProperties);
        try {
            flyway.init();
        } catch (FlywayException e) {
            assert (false);
        }

        assertTrue(true);
    }

    /**
     * test init flyway with master database set
     */
    @Test
    public void testInitwithMaster() {
        Properties flywayProperties = loadProperties();

        flywayProperties.setProperty("flyway.url", "databaseName=master");

        Flyway flyway = new Flyway(flywayProperties);
        try {
            flyway.init();
            assertTrue(false);
        } catch (FlywayException e) {
            assertTrue(true);
        }
    }

    private Properties loadProperties() {
        Properties flywayProperties = null;
        try {
            flywayProperties = Tools.loadProperties("conf/test_flyway.conf");
        } catch (ToolsException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        return flywayProperties;
    }
}
