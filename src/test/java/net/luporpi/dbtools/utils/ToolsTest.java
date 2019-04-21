package net.luporpi.dbtools.utils;

import org.junit.Test;

import net.luporpi.dbtools.utils.Tools;
import net.luporpi.dbtools.utils.exceptions.ToolsException;

import static org.junit.Assert.*;

import java.util.Properties;

public class ToolsTest {
    /**
     * Rigorous Test.
     */
    @Test
    public void testApp() {
        assertTrue(true);
    }

    @Test
    public void testLoadProperties() {
        Properties connectionProperties = null;
        try {
            connectionProperties = Tools.loadProperties("conf/test_connection.conf");
        } catch (ToolsException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        assertTrue(true);
    }

    @Test
    public void testMergeProperties() {
        Properties connectionProperties = null;
        Properties flywayProperties = null;

        try {
            connectionProperties = Tools.loadProperties("conf/test_connection.conf");
            flywayProperties = Tools.loadProperties("conf/test_flyway.conf");
        } catch (ToolsException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        Tools.mergeProperties(connectionProperties, flywayProperties);

        assertFalse("flyway.url != database.url", flywayProperties.getProperty("flyway.url")
                .compareTo(connectionProperties.getProperty("database.url")) != 0);
    }

}
