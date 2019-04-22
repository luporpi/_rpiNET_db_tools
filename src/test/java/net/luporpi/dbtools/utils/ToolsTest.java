package net.luporpi.dbtools.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

public class ToolsTest {

    @Test
    public void testLoadProperties() {
        Properties connectionProperties = null;
        try {
            connectionProperties = Tools.loadProperties("conf/test_database.conf");
        } catch (ToolsException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        connectionProperties.clear();

        assertTrue(true);
    }

    @Test
    public void testMergeProperties() {
        Properties connectionProperties = null;
        Properties flywayProperties = null;

        try {
            connectionProperties = Tools.loadProperties("conf/test_database.conf");
            flywayProperties = Tools.loadProperties("conf/test_flyway.conf");
        } catch (ToolsException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        Tools.mergeProperties(connectionProperties, flywayProperties);

        assertFalse("flyway.url == database.url", flywayProperties.getProperty("flyway.url")
                .compareTo(connectionProperties.getProperty("database.url")) == 0);
    }

}
