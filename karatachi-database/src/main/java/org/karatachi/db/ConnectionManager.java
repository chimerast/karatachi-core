package org.karatachi.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.karatachi.classloader.Reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataSourceWrapper dataSource;
    private final Connection connection;
    private final ArrayList<Statement> statements = new ArrayList<Statement>();

    public ConnectionManager(DataSourceWrapper dataSource) {
        this(dataSource, 2);
    }

    public ConnectionManager(DataSourceWrapper ds, int stack) {
        this.dataSource = ds;

        Connection connection = null;
        try {
            connection = ds.getConnection();
        } catch (Exception e) {
            logger.error("Cannot create connection '" + ds.getName() + "'", e);
        }
        this.connection = connection;

        if (logger.isTraceEnabled()) {
            logger.debug("DB connection '{}' created : {}", ds.getName(),
                    Reflection.getAncestorMethodInfo(stack));
        } else {
            logger.debug("DB connection '{}' created", ds.getName());
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public DatabaseMetaData getMetaData() {
        try {
            return connection != null ? connection.getMetaData() : null;
        } catch (SQLException e) {
            return null;
        }
    }

    public Statement createStatement() throws SQLException {
        if (connection == null) {
            throw new SQLException("not initialized");
        }

        Statement stmt =
                connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
        statements.add(stmt);
        return new StatementWrapper(stmt);
    }

    public Statement createUpdatableStatement() throws SQLException {
        if (connection == null) {
            throw new SQLException("not initialized");
        }

        Statement stmt =
                connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
        statements.add(stmt);
        return new StatementWrapper(stmt);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null) {
            throw new SQLException("not initialized");
        }

        PreparedStatement stmt =
                connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
        statements.add(stmt);
        return new PreparedStatementWrapper(stmt, sql);
    }

    public PreparedStatement prepareUpdatableStatement(String sql)
            throws SQLException {
        if (connection == null) {
            throw new SQLException("not initialized");
        }

        PreparedStatement stmt =
                connection.prepareStatement(sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
        statements.add(stmt);
        return new PreparedStatementWrapper(stmt, sql);
    }

    public void dispose() {
        if (!isConnected()) {
            return;
        }

        try {
            for (Statement stmt : statements) {
                stmt.close();
            }
            statements.clear();
        } catch (Exception e) {
            logger.error("Cannot close statement", e);
        }

        try {
            if (connection != null) {
                connection.close();
                if (logger.isTraceEnabled()) {
                    logger.debug("DB connection '{}' closed : {}",
                            dataSource.getName(),
                            Reflection.getParentMethodInfo());
                } else {
                    logger.debug("DB connection '{}' closed",
                            dataSource.getName());
                }
            }
        } catch (Exception e) {
            logger.error("Cannot close connection", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }
}
