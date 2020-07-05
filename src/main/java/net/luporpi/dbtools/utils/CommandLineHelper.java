package net.luporpi.dbtools.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

/**
 * Helper for command line handling.
 */
public class CommandLineHelper {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(CommandLineHelper.class);

    private static final String CRERATEDB = "c";
    private static final String DATABASE = "d";
    private static final String FLYWAY = "f";
    private static final String HELP = "h";
    private static final String LOG = "l";

    private CommandLineParser mParser;
    private CommandLine mCmd;
    private Options mOptions;

    private boolean mCreateDb;
    private String mDatabase;
    private String mFlyway;
    private String mLog;

    /**
     * Contructor.
     */
    public CommandLineHelper() {
        mOptions = new Options();

        mOptions.addOption(CRERATEDB, "createdb", false,
                "If set the output database for collection data will be created.");
        mOptions.addOption(DATABASE, "database", true,
                "Use an alternative database configuration file instead of the default conf/database.conf");
        mOptions.addOption(FLYWAY, "flyway", true,
                "Use an alternative flyway configuration file instead of the default conf/flyway.conf");
        mOptions.addOption(HELP, "help", false, "Print help for command-line parameters.");
        mOptions.addOption(LOG, "log", true,
                "Use an alternative log4j configuration file instead of the default conf/log4j.properties");

        mParser = new DefaultParser();
    }

    /**
     * @return the createDb
     */
    public boolean isCreateDb() {
        return mCreateDb;
    }

    /**
     * @param createdb the createdb to set
     */
    public void setCreateDb(boolean createdb) {
        this.mCreateDb = createdb;
    }

    /**
     * @return the flyway
     */
    public String getFlyway() {
        if (mFlyway == null) {
            mFlyway = "conf/flyway.conf";
        }
        return mFlyway;
    }

    /**
     * @param flyway the flyway to set
     */
    public void setFlyway(String flyway) {
        this.mFlyway = flyway;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        if (mDatabase == null) {
            mDatabase = "conf/database.conf";
        }
        return mDatabase;
    }

    /**
     * @param database the database to set
     */
    public void set_connection(String database) {
        this.mDatabase = database;
    }

    /**
     * @return the log
     */
    public String getLog() {
        if (mLog == null) {
            mLog = "conf/log4j.properties";
        }
        return mLog;
    }

    /**
     * @param log the log to set
     */
    public void setLog(String log) {
        this.mLog = log;
    }

    /**
     * print help.
     */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        String usage = "_rpiNET_dbtools\n\n" + "java -jar _rpiNET_dbtools-<version>.jar [options]\n\n" + "";
        formatter.printHelp(usage, mOptions);
    }

    /**
     * Parse the command line.
     * 
     * @param args
     * @throws ToolsException
     */
    public void parse(String[] args) throws ToolsException {
        try {
            mCmd = mParser.parse(mOptions, args);
        } catch (ParseException ex) {
            throw new ToolsException("", ex);
        }

        if (mCmd.hasOption(HELP)) {
            printHelp();
            System.exit(0);
        }

        mCreateDb = mCmd.hasOption(CRERATEDB);
        mDatabase = mCmd.getOptionValue(DATABASE);
        mFlyway = mCmd.getOptionValue(FLYWAY);
        mLog = mCmd.getOptionValue(LOG);
    }
}
