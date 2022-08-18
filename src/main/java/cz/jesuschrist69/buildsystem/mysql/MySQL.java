package cz.jesuschrist69.buildsystem.mysql;

import cz.jesuschrist69.buildsystem.exceptions.BuildSystemException;
import cz.jesuschrist69.buildsystem.mysql.builder.SqlBuilder;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySQL {

    private final MysqlCredentials credentials;
    private boolean connected = false;

    private Connection connection;

    public MySQL(@NotNull MysqlCredentials credentials) {
        this.credentials = credentials;

        connect();
    }

    /**
     * Method used to establish connection with MySQL database
     */
    private void connect() {
        try {
            synchronized (this) {
                if (isConnected()) return;
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + credentials.getHost() + ":" + credentials.getPort() + "/"
                                + credentials.getDatabase(), credentials.getUsername(), credentials.getPassword()
                );
                connected = true;
            }
        } catch (Throwable ignored) {
            connected = false;
        } finally {
            if (connected) {
                execute(new SqlBuilder.Create(credentials.getTablePrefix() + "world_data")
                        .ifNotExists()
                        .columns("name", "locked", "hidden", "owner", "created_at", "world_type")
                        .columnTypes("VARCHAR(64) NOT NULL PRIMARY KEY", "INTEGER", "INTEGER", "VARCHAR(16)", "TIMESTAMP", "TEXT")
                        .build());
            }
        }
    }

    /**
     * "If we're not connected, connect, then execute the query."
     *
     * The first thing we do is check if we're connected. If we're not, we try to connect. If we can't connect, we throw a
     * BuildSystemException with more details
     *
     * @param query The query to execute.
     */
    public void execute(@NotNull String query) {
        if (!isConnected()) {
            try {
                connect();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new BuildSystemException("Could not connect to MySQL database. Skipping query {0}", t, query);
            }
        }
        query = query.replaceAll("%mysql-table-prefix%", credentials.getTablePrefix());
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.execute();
        } catch (Throwable t) {
            throw new BuildSystemException("Could not execute query: {0}", t, query);
        }
    }

    /**
     * "If we're not connected, connect, then execute the query and return the result set."
     *
     * The first thing we do is check if we're connected. If we're not, we try to connect. If we can't connect, we throw a
     * BuildSystemException with more details.
     *
     * @param query The query to execute.
     * @return {@link ResultSet}
     */
    public ResultSet getResult(@NotNull String query) {
        if (!isConnected()) {
            try {
                connect();
            } catch (Throwable t) {
                throw new BuildSystemException("Could not connect to MySQL database. Skipping query {0}", t, query);
            }
        }
        query = query.replaceAll("%mysql-table-prefix%", credentials.getTablePrefix());
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (Throwable t) {
            throw new BuildSystemException("Could not execute query: {0}", t, query);
        }
    }

    /**
     * Method used to disconnect from database
     */
    public void disconnect() {
        if (connected) {
            connected = false;
            try {
                connection.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Check if connection with database is valid
     *
     * @return {@link Boolean}
     */
    public boolean isConnected() {
        try {
            return connected && connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (Exception ignored) {}

        return false;
    }

}
