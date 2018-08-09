package io.rixa.bot.data.storage;

import io.rixa.bot.Rixa;
import io.rixa.bot.data.config.Configuration;
import io.rixa.bot.data.storage.enums.DatabaseTables;
import io.rixa.bot.data.storage.enums.Statements;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCountCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class DatabaseAdapter {

    private static DatabaseAdapter instance;
    private Rixa rixaInstance;
    private JdbcTemplate jdbcTemplate;
    private DatabaseAdapter() {
        instance = this;
        rixaInstance = Rixa.getInstance();
    }

    public void check() {
        if (jdbcTemplate != null) {
            return;
        }
        Configuration config = rixaInstance.getConfiguration();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        String url = String.format("jdbc:mysql://%s:%s/%s%s", config.getSqlCredentials().get("hostName"),
                config.getSqlCredentials().get("port"), config.getSqlCredentials().get("databaseName"),
                config.getSqlCredentials().get("jdbc_args"));
        dataSource.setUrl(url);
        dataSource.setUsername(config.getSqlCredentials().get("userName"));
        dataSource.setPassword(config.getSqlCredentials().get("password"));
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate get() {
        check();
        return jdbcTemplate;
    }

    public boolean exists(String table, String key, String value) {
        try {
            int amount = get().queryForObject
                    (String.format(Statements.COUNT_CORE.getStatement(), table, key), new Object[]{value}, Integer.class);
            return amount > 0;
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    public Array createArrayOf(String typeName, Object[] elements) {
        try {
            return get().getDataSource().getConnection().createArrayOf(typeName, elements);
        } catch (SQLException ignored) {
        }
        return null;
    }

    public static DatabaseAdapter getInstance() {
        return ( (instance == null) ? instance = new DatabaseAdapter() : instance );
    }
}
