package org.karatachi.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.karatachi.classloader.Reflection;
import org.karatachi.exception.IncompatiblePlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionWrapper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Connection connection;
    private final String url;
    private final ArrayList<Statement> statements = new ArrayList<Statement>();

    public ConnectionWrapper(Connection connection) throws SQLException {
        this.connection = connection;
        this.url = connection.getMetaData().getURL();
        if (logger.isTraceEnabled()) {
            logger.debug("DB connection '{}' created : {}", url,
                    Reflection.getParentMethodInfo());
        } else {
            logger.debug("DB connection '{}' created", url);
        }
    }

    public void dispose() {
        try {
            for (Statement stmt : statements) {
                stmt.close();
            }
            statements.clear();
        } catch (Exception e) {
            logger.error("Cannot close statement", e);
        }

        try {
            connection.close();
            if (logger.isTraceEnabled()) {
                logger.debug("DB connection '{}' closed : {}", url,
                        Reflection.getParentMethodInfo());
            } else {
                logger.debug("DB connection '{}' closed", url);
            }
        } catch (SQLException e) {
            logger.error("Cannot close connection", e);
        }
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public ResultSet executeQuery(String sql, Object... args)
            throws SQLException {
        return prepareStatement(sql, args).executeQuery();
    }

    public int executeUpdate(String sql, Object... args) throws SQLException {
        return prepareStatement(sql, args).executeUpdate();
    }

    public PreparedStatement prepareStatement(String sql, Object... args)
            throws SQLException {
        return setupPreparedStatment(connection.prepareStatement(sql), sql,
                args);
    }

    public PreparedStatement prepareStatementWithParameter(String sql,
            int resultSetType, int resultSetConcurrency, Object... args)
            throws SQLException {
        return setupPreparedStatment(connection.prepareStatement(sql,
                resultSetType, resultSetConcurrency), sql, args);
    }

    private PreparedStatement setupPreparedStatment(PreparedStatement stmt,
            String sql, Object... args) throws SQLException {
        statements.add(stmt);
        PreparedStatementWrapper ret = new PreparedStatementWrapper(stmt, sql);
        int i = 1;
        for (Object arg : args) {
            if (arg == null) {
                ret.setObject(i, null);
            } else if (arg.getClass() == Boolean.class) {
                ret.setBoolean(i, (Boolean) arg);
            } else if (arg.getClass() == Short.class) {
                ret.setShort(i, (Short) arg);
            } else if (arg.getClass() == Integer.class) {
                ret.setInt(i, (Integer) arg);
            } else if (arg.getClass() == Long.class) {
                ret.setLong(i, (Long) arg);
            } else if (arg.getClass() == Float.class) {
                ret.setFloat(i, (Float) arg);
            } else if (arg.getClass() == Double.class) {
                ret.setDouble(i, (Double) arg);
            } else if (arg.getClass() == String.class) {
                ret.setString(i, (String) arg);
            } else if (arg.getClass() == Date.class) {
                ret.setDate(i, (Date) arg);
            } else if (arg.getClass() == Time.class) {
                ret.setTime(i, (Time) arg);
            } else if (arg.getClass() == Timestamp.class) {
                ret.setTimestamp(i, (Timestamp) arg);
            } else if (arg.getClass() == java.util.Date.class) {
                ret.setTimestamp(i, new Timestamp(
                        ((java.util.Date) arg).getTime()));
            } else {
                throw new IncompatiblePlatformException();
            }
            ++i;
        }
        return ret;
    }
}
