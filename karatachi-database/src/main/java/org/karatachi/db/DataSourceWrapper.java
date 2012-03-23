package org.karatachi.db;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataSourceWrapper implements DataSource,
        Comparable<DataSourceWrapper> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final String name;
    private final DataSource dataSource;
    private final boolean master;
    private boolean active;

    public DataSourceWrapper(String name, DataSource dataSource, boolean master) {
        this.name = name;
        this.dataSource = dataSource;
        this.master = master;
        this.active = true;
    }

    public String getName() {
        return name;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isMaster() {
        return master;
    }

    public boolean isActive() {
        return active;
    }

    public boolean activate() {
        ConnectionManager dbm = new ConnectionManager(this);
        try {
            dbm.createStatement().executeQuery("SELECT version()");
            active = true;
            logger.info("Activate datasource {}.", name);
            return true;
        } catch (SQLException e) {
            active = false;
            logger.warn("Inactivate datasource {}.", name);
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            Connection ret = dataSource.getConnection();
            active = true;
            return ret;
        } catch (SQLException e) {
            if (active) {
                logger.warn("Inactivate datasource {}.", name);
                active = false;
            }
            throw e;
        }
    }

    public Connection getConnection(String username, String password)
            throws SQLException {
        try {
            Connection ret = dataSource.getConnection(username, password);
            active = true;
            return ret;
        } catch (SQLException e) {
            if (active) {
                logger.warn("Inactivate datasource {}.", name);
                active = false;
            }
            throw e;
        }
    }

    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    public int compareTo(DataSourceWrapper o) {
        return name.compareTo(o.name);
    }
}
