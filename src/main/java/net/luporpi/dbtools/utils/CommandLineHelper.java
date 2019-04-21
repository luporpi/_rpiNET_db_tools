package net.luporpi.dbtools.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.luporpi.dbtools.utils.exceptions.ToolsException;

public class CommandLineHelper {

    private final static String CONNECTION = "connection";
    private final static String FLYWAY = "flyway";
    private final static String HELP = "help";
    private final static String INSTALLDB = "db";

    private CommandLineParser _parser;
    private CommandLine _cmd;
    private Options _options;

    private String _connection = null;
    private String _flyway = null;
    private boolean _installdb = false;

    public CommandLineHelper() {
        _options = new Options();

        _options.addOption(CONNECTION, true, "use given connection.properties file");
        _options.addOption(FLYWAY, true, "use given flyway.properties file");
        _options.addOption(HELP, false, "print this message");
        _options.addOption(INSTALLDB, false, "create db");

        _parser = new DefaultParser();
    }

    /**
     * @return the _installdb
     */
    public boolean is_installdb() {
        return _installdb;
    }

    /**
     * @param _installdb the _installdb to set
     */
    public void set_installdb(boolean _installdb) {
        this._installdb = _installdb;
    }

    /**
     * @return the _flyway
     */
    public String get_flyway() {
        return _flyway;
    }

    /**
     * @param _flyway the _flyway to set
     */
    public void set_flyway(String _flyway) {
        this._flyway = _flyway;
    }

    /**
     * @return the _connection
     */
    public String get_connection() {
        return _connection;
    }

    /**
     * @param _connection the _connection to set
     */
    public void set_connection(String _connection) {
        this._connection = _connection;
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

        _connection = _cmd.getOptionValue(CONNECTION);
        _flyway = _cmd.getOptionValue(FLYWAY);
        _installdb = _cmd.hasOption(INSTALLDB);
    }
}