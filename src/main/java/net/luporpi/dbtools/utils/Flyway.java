package net.luporpi.dbtools.utils;

import java.util.Properties;

import org.flywaydb.core.api.configuration.FluentConfiguration;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.FlywayException;

/**
 * Flyway.
 */
public class Flyway {

    //private static final Logger LOGGER = LoggerFactory.getLogger(Flyway.class);

    private Properties mFlywayProperties;
    private org.flywaydb.core.Flyway mFlyway;

    /**
     * Constructor.
     * 
     * @param flywayProperties
     */
    public Flyway(Properties flywayProperties) {
        mFlywayProperties = flywayProperties;
    }

    /**
     * Init.
     * 
     * @throws FlywayException
     */
    public void init() throws FlywayException {
        if (((String) mFlywayProperties.getOrDefault("flyway.url", "databaseName=master")).toLowerCase()
                .contains("databasename=master")) {
            throw new FlywayException("Don't use master database in flyway.url", null);
        }

        FluentConfiguration conf = new FluentConfiguration();
        conf.configuration(mFlywayProperties);

        mFlyway = new org.flywaydb.core.Flyway(conf);
    }

    /**
     * Run.
     */
    public void run() {
        mFlyway.migrate();
    }
}
