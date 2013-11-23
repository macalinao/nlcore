package net.new_liberty.nlcore.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 * Database stuff. MySQL sucks.
 */
public final class DB {

    private static final ResultSetHandler SCALAR_HANDLER = new ScalarHandler();

    private static DB instance = null;

    private final DatabaseModule plugin;

    private final MysqlDataSource source;

    public DB(DatabaseModule plugin, String user, String pass, String host, int port, String database) {
        this.plugin = plugin;

        source = new MysqlDataSource();
        source.setUser(user);
        source.setPassword(pass);
        source.setServerName(host);
        source.setPort(port);
        source.setDatabaseName(database);

        instance = this;
    }

    public static DB i() {
        return instance;
    }

    public static void setInstance(DB db) {
        if (instance == null) {
            instance = db;
        }
    }

    /**
     * Attempts to connect to the database to check if the database credentials
     * are valid.
     *
     * @return True if the credentials are valid.
     */
    public boolean isValid() {
        try {
            return source.getConnection() != null;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Gets the MysqlDataSource associated with the database.
     *
     * @return The MysqlDataSource.
     */
    public MysqlDataSource getSource() {
        return source;
    }

    /**
     * Executes a database update.
     *
     * @param query The database query to perform with `?` in place of
     * parameters.
     * @param params The query parameters
     */
    public void update(String query, Object... params) {
        QueryRunner run = new QueryRunner(source);
        try {
            if (params == null) {
                run.update(query);
            } else {
                run.update(query, params);
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not run database query '" + query + "'!", ex);
        }
    }

    /**
     * Executes a database query.
     *
     * @param <T> The return type
     * @param query The database query to perform with `?` in place of
     * parameters.
     * @param handler A {@link ResultSetHandler} to handle the query data.
     * @param params The query parameters
     * @return The result of the query
     */
    public <T> T query(String query, ResultSetHandler<T> handler, Object... params) {
        QueryRunner run = new QueryRunner(source);
        try {
            if (params != null) {
                return run.query(query, handler, params);
            } else {
                return run.query(query, handler);
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not execute database query '" + query + "'!", ex);
        }
        return null;
    }

    /**
     * Gets a value from the database. The column desired must be the first.
     *
     * @param query The database query to perform with `?` in place of
     * parameters.
     * @param def The default value to return if the value is null.
     * @param params The query parameters
     * @return The value
     */
    public Object get(String query, Object def, Object... params) {
        Object ret = query(query, SCALAR_HANDLER, params);
        if (ret == null) {
            return def;
        }
        return ret;
    }

}
