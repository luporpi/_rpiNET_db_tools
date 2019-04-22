package net.luporpi.dbtools.utils;

import java.util.Properties;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.luporpi.dbtools.utils.exceptions.FlywayException;

public class Flyway {

    final static Logger logger = LoggerFactory.getLogger(Flyway.class);

    private Properties _flywayProperties = null;
    private org.flywaydb.core.Flyway _flyway = null;

    public Flyway(Properties flywayProperties) {
        _flywayProperties = flywayProperties;
    }

    public void init() throws FlywayException {
        if (((String) _flywayProperties.getOrDefault("flyway.url", "databaseName=master")).toLowerCase()
                .contains("databasename=master")) {
            throw new FlywayException("Don't use master database in flyway.url", null);
        }

        FluentConfiguration conf = new FluentConfiguration();
        conf.configuration(_flywayProperties);

        _flyway = new org.flywaydb.core.Flyway(conf);
    }

    public void run() {
        _flyway.migrate();
    }
}