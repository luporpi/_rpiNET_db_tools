package net.luporpi.dbtools.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

public class CommandLineHelper {

    final static org.slf4j.Logger logger = LoggerFactory.getLogger(CommandLineHelper.class);

    private final static String CRERATEDB = "c";
    private final static String DATABASE = "d";
    private final static String FLYWAY = "f";
    private final static String HELP = "h";
    private final static String LOG = "l";

    private CommandLineParser _parser;
    private CommandLine _cmd;
    private Options _options;

    private boolean _createdb = false;
    private String _database = null;
    private String _flyway = null;
    private String _log = null;

    public CommandLineHelper() {
        _options = new Options();

        _options.addOption(DATABASE, "database", true, "use given database.conf file (default: conf/database.conf)");
        _options.addOption(CRERATEDB, "createdb", false, "create db");
        _options.addOption(FLYWAY, "flyway", true, "use given flyway.conf file (default: conf/flyway.conf)");
        _options.addOption(LOG, "log", true, "use given log4j.properties file (default: conf/log4j.properties.conf)");
        _options.addOption(HELP, "help", false, "print this message");

        _parser = new DefaultParser();
    }

    /**
     * @return the _installdb
     */
    public boolean is_createdb() {
        return _createdb;
    }

    /**
     * @param _installdb the _installdb to set
     */
    public void set_installdb(boolean _createdb) {
        this._createdb = _createdb;
    }

    /**
     * @return the _flyway
     */
    public String get_flyway() {
        if (_flyway == null) {
            _flyway = "conf/flyway.conf";
        }
        return _flyway;
    }

    /**
     * @param _flyway the _flyway to set
     */
    public void set_flyway(String _flyway) {
        this._flyway = _flyway;
    }

    /**
     * @return the _database
     */
    public String get_database() {
        if (_database == null) {
            _database = "conf/database.conf";
        }
        return _database;
    }

    /**
     * @param _connection the _connection to set
     */
    public void set_connection(String _database) {
        this._database = _database;
    }

    /**
     * @return the _connection
     */
    public String get_log() {
        if (_log == null) {
            _log = "conf/log4j.properties";
        }
        return _log;
    }

    /**
     * @param _connection the _connection to set
     */
    public void set_log(String _log) {
        this._log = _log;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("_rpinet_dbtools", _options);
    }

    public void parse(String[] args) throws ToolsException {
        try {
            _cmd = _parser.parse(_options, args);
        } catch (ParseException ex) {
            throw new ToolsException("", ex);
        }

        if (_cmd.hasOption(HELP)) {
            printHelp();
            System.exit(0);
        }

        _createdb = _cmd.hasOption(CRERATEDB);
        _database = _cmd.getOptionValue(DATABASE);
        _flyway = _cmd.getOptionValue(FLYWAY);
        _log = _cmd.getOptionValue(LOG);
    }
}