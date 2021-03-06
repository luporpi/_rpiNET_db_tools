package net.luporpi.dbtools.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

/**
 * Class CommandLineHelper.
 */
public class CommandLineHelperTest {

    /**
     * get default values.
     */
    @Test
    public void testGetDefaults() {
        String[] args = null;

        CommandLineHelper commandLineHelper = new CommandLineHelper();
        try {
            commandLineHelper.parse(args);
        } catch (ToolsException e) {
            assertTrue(e.getMessage(), false);
        }

        assertTrue(commandLineHelper.getDatabase().compareTo("conf/database.conf") == 0);
        assertTrue(commandLineHelper.getFlyway().compareTo("conf/flyway.conf") == 0);
        assertTrue(commandLineHelper.getLog().compareTo("conf/log4j.properties") == 0);
    }

    /**
     * test parse command line.
     */
    @Test
    @SuppressWarnings("checkstyle:NoWhitespaceAfter")
    public void testParse() {
        String[] args = { "-d", "conf/test_database.conf", "-f", "conf/test_flyway.conf", "-c", "-l",
                "conf/test_log4j.properties" };

        CommandLineHelper commandLineHelper = new CommandLineHelper();
        try {
            commandLineHelper.parse(args);
        } catch (ToolsException e) {
            assertTrue(e.getMessage(), false);
        }

        assertTrue(commandLineHelper.getDatabase().compareTo("conf/test_database.conf") == 0);
        assertTrue(commandLineHelper.getFlyway().compareTo("conf/test_flyway.conf") == 0);
        assertTrue(commandLineHelper.getLog().compareTo("conf/test_log4j.properties") == 0);
    }
}
