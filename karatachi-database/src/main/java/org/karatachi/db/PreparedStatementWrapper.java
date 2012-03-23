package org.karatachi.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.karatachi.classloader.Reflection;
import org.karatachi.thread.AcceptInterruptable;
import org.karatachi.thread.InterruptableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PreparedStatementWrapper implements PreparedStatement {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final PreparedStatement statement;
    private final String sql;
    private final String[] args = new String[256];
    private final InterruptableOperation interruptable;

    public PreparedStatementWrapper(PreparedStatement statement, String sql) {
        this.statement = statement;
        this.sql = sql;
        this.interruptable = new InterruptableOperation() {
            @Override
            public void interrupt() {
                try {
                    PreparedStatementWrapper.this.statement.cancel();
                } catch (SQLException e) {
                    logger.error("Error on interrupting statement", e);
                }
            }
        };
    }

    private void setInterruptable() throws SQLException {
        if (Thread.currentThread() instanceof AcceptInterruptable) {
            try {
                ((AcceptInterruptable) Thread.currentThread())
                        .setInterruptable(interruptable);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("Thread Interrupted", e);
            }
        }
    }

    private void clearInterruptable() {
        if (Thread.currentThread() instanceof AcceptInterruptable) {
            ((AcceptInterruptable) Thread.currentThread()).clearInterruptable();
        }
    }

    private void logExecute(String sql) {
        if (logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer(sql);
            for (int i = 0; i < 256; ++i) {
                int pos = sb.indexOf("?");
                if (pos == -1)
                    break;
                String str = args[i];
                if (str == null)
                    str = "null";
                sb.replace(pos, pos + 1, str);
            }

            String logstr = "Execute SQL : sql = " + sb.toString();
            if (logger.isTraceEnabled())
                logstr = logstr + " from : "
                        + Reflection.getAncestorMethodInfo(2);

            logger.debug(logstr);
        }
    }

    private void set(int parameterIndex, Object x) {
        if (!logger.isDebugEnabled())
            return;

        if (x == null) {
            args[parameterIndex - 1] = "null";
        } else if (x instanceof String) {
            args[parameterIndex - 1] = String.format("'%s'", x);
        } else if (x instanceof Boolean) {
            args[parameterIndex - 1] = x.toString();
        } else if (x instanceof Number) {
            args[parameterIndex - 1] = x.toString();
        } else if (x instanceof Date) {
            args[parameterIndex - 1] = String.format("'%tF'::date", x);
        } else if (x instanceof Time) {
            args[parameterIndex - 1] = String.format("'%tT'::time", x);
        } else if (x instanceof Timestamp) {
            args[parameterIndex - 1] = String.format(
                    "'%1$tF %1$tT'::timestamp", x);
        } else {
            args[parameterIndex - 1] = String.format("'%s'::%s", x, x
                    .getClass().getCanonicalName());
        }
    }

    public void addBatch() throws SQLException {
        statement.addBatch();
    }

    public void clearParameters() throws SQLException {
        statement.clearParameters();
    }

    public boolean execute() throws SQLException {
        return statement.execute();
    }

    public ResultSet executeQuery() throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeQuery();
        } finally {
            clearInterruptable();
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeUpdate();
        } finally {
            clearInterruptable();
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return statement.getMetaData();
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return statement.getParameterMetaData();
    }

    public void setArray(int parameterIndex, Array x) throws SQLException {
        set(parameterIndex, x);
        statement.setArray(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x)
            throws SQLException {
        set(parameterIndex, x);
        statement.setAsciiStream(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        set(parameterIndex, x);
        statement.setAsciiStream(parameterIndex, x, length);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        set(parameterIndex, x);
        statement.setAsciiStream(parameterIndex, x, length);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        set(parameterIndex, x);
        statement.setBigDecimal(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x)
            throws SQLException {
        set(parameterIndex, x);
        statement.setBinaryStream(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        set(parameterIndex, x);
        statement.setBinaryStream(parameterIndex, x, length);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        set(parameterIndex, x);
        statement.setBinaryStream(parameterIndex, x, length);
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        set(parameterIndex, x);
        statement.setBlob(parameterIndex, x);
    }

    public void setBlob(int parameterIndex, InputStream inputStream)
            throws SQLException {
        set(parameterIndex, inputStream);
        statement.setBlob(parameterIndex, inputStream);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length)
            throws SQLException {
        set(parameterIndex, inputStream);
        statement.setBlob(parameterIndex, inputStream, length);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        set(parameterIndex, x);
        statement.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        set(parameterIndex, x);
        statement.setByte(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        set(parameterIndex, x);
        statement.setBytes(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader reader)
            throws SQLException {
        set(parameterIndex, reader);
        statement.setCharacterStream(parameterIndex, reader);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        set(parameterIndex, reader);
        statement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setCharacterStream(int parameterIndex, Reader reader,
            long length) throws SQLException {
        set(parameterIndex, reader);
        statement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {
        set(parameterIndex, x);
        statement.setClob(parameterIndex, x);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        set(parameterIndex, reader);
        statement.setClob(parameterIndex, reader);
    }

    public void setClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        set(parameterIndex, reader);
        statement.setClob(parameterIndex, reader, length);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        set(parameterIndex, x);
        statement.setDate(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        set(parameterIndex, x);
        statement.setDate(parameterIndex, x, cal);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        set(parameterIndex, x);
        statement.setDouble(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        set(parameterIndex, x);
        statement.setFloat(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        set(parameterIndex, x);
        statement.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        set(parameterIndex, x);
        statement.setLong(parameterIndex, x);
    }

    public void setNCharacterStream(int parameterIndex, Reader value)
            throws SQLException {
        set(parameterIndex, value);
        statement.setNCharacterStream(parameterIndex, value);
    }

    public void setNCharacterStream(int parameterIndex, Reader value,
            long length) throws SQLException {
        set(parameterIndex, value);
        statement.setNCharacterStream(parameterIndex, value, length);
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        set(parameterIndex, value);
        statement.setNClob(parameterIndex, value);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        set(parameterIndex, reader);
        statement.setNClob(parameterIndex, reader);
    }

    public void setNClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        set(parameterIndex, reader);
        statement.setNClob(parameterIndex, reader, length);
    }

    public void setNString(int parameterIndex, String value)
            throws SQLException {
        set(parameterIndex, value);
        statement.setNString(parameterIndex, value);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        set(parameterIndex, null);
        statement.setNull(parameterIndex, sqlType);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName)
            throws SQLException {
        set(parameterIndex, null);
        statement.setNull(parameterIndex, sqlType, typeName);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        set(parameterIndex, x);
        statement.setObject(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        set(parameterIndex, x);
        statement.setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType,
            int scaleOrLength) throws SQLException {
        set(parameterIndex, x);
        statement.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException {
        set(parameterIndex, x);
        statement.setRef(parameterIndex, x);
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        set(parameterIndex, x);
        statement.setRowId(parameterIndex, x);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject)
            throws SQLException {
        set(parameterIndex, xmlObject);
        statement.setSQLXML(parameterIndex, xmlObject);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        set(parameterIndex, x);
        statement.setShort(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        set(parameterIndex, x);
        statement.setString(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        set(parameterIndex, x);
        statement.setTime(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        set(parameterIndex, x);
        statement.setTime(parameterIndex, x, cal);
    }

    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        set(parameterIndex, x);
        statement.setTimestamp(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        set(parameterIndex, x);
        statement.setTimestamp(parameterIndex, x, cal);
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        set(parameterIndex, x);
        statement.setURL(parameterIndex, x);
    }

    @SuppressWarnings("deprecation")
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        set(parameterIndex, x);
        statement.setUnicodeStream(parameterIndex, x, length);
    }

    public void addBatch(String sql) throws SQLException {
        statement.addBatch(sql);
    }

    public void cancel() throws SQLException {
        statement.cancel();
    }

    public void clearBatch() throws SQLException {
        statement.clearBatch();
    }

    public void clearWarnings() throws SQLException {
        statement.clearWarnings();
    }

    public void close() throws SQLException {
        statement.close();
    }

    public boolean execute(String sql) throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.execute(sql);
        } finally {
            clearInterruptable();
        }
    }

    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.execute(sql, autoGeneratedKeys);
        } finally {
            clearInterruptable();
        }
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.execute(sql, columnIndexes);
        } finally {
            clearInterruptable();
        }
    }

    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.execute(sql, columnNames);
        } finally {
            clearInterruptable();
        }
    }

    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeQuery(sql);
        } finally {
            clearInterruptable();
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeUpdate(sql);
        } finally {
            clearInterruptable();
        }
    }

    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeUpdate(sql, autoGeneratedKeys);
        } finally {
            clearInterruptable();
        }
    }

    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeUpdate(sql, columnIndexes);
        } finally {
            clearInterruptable();
        }
    }

    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        try {
            setInterruptable();
            logExecute(sql);
            return statement.executeUpdate(sql, columnNames);
        } finally {
            clearInterruptable();
        }
    }

    public Connection getConnection() throws SQLException {
        return statement.getConnection();
    }

    public int getFetchDirection() throws SQLException {
        return statement.getFetchDirection();
    }

    public int getFetchSize() throws SQLException {
        return statement.getFetchSize();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    public int getMaxFieldSize() throws SQLException {
        return statement.getMaxFieldSize();
    }

    public int getMaxRows() throws SQLException {
        return statement.getMaxRows();
    }

    public boolean getMoreResults() throws SQLException {
        return statement.getMoreResults();
    }

    public boolean getMoreResults(int current) throws SQLException {
        return statement.getMoreResults(current);
    }

    public int getQueryTimeout() throws SQLException {
        return statement.getQueryTimeout();
    }

    public ResultSet getResultSet() throws SQLException {
        return statement.getResultSet();
    }

    public int getResultSetConcurrency() throws SQLException {
        return statement.getResultSetConcurrency();
    }

    public int getResultSetHoldability() throws SQLException {
        return statement.getResultSetHoldability();
    }

    public int getResultSetType() throws SQLException {
        return statement.getResultSetType();
    }

    public int getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }

    public SQLWarning getWarnings() throws SQLException {
        return statement.getWarnings();
    }

    public boolean isClosed() throws SQLException {
        return statement.isClosed();
    }

    public boolean isPoolable() throws SQLException {
        return statement.isPoolable();
    }

    public void setCursorName(String name) throws SQLException {
        statement.setCursorName(name);
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        statement.setEscapeProcessing(enable);
    }

    public void setFetchDirection(int direction) throws SQLException {
        statement.setFetchDirection(direction);
    }

    public void setFetchSize(int rows) throws SQLException {
        statement.setFetchSize(rows);
    }

    public void setMaxFieldSize(int max) throws SQLException {
        statement.setMaxFieldSize(max);
    }

    public void setMaxRows(int max) throws SQLException {
        statement.setMaxRows(max);
    }

    public void setPoolable(boolean poolable) throws SQLException {
        statement.setPoolable(poolable);
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        statement.setQueryTimeout(seconds);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return statement.isWrapperFor(iface);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return statement.unwrap(iface);
    }
}
