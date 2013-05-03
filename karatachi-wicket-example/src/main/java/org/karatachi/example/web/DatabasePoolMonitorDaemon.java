package org.karatachi.example.web;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.karatachi.daemon.monitor.MonitorDaemon;
import org.karatachi.system.SystemInfo;
import org.seasar.extension.dbcp.ConnectionPool;
import org.seasar.framework.container.SingletonS2Container;

public class DatabasePoolMonitorDaemon extends MonitorDaemon {
    public DatabasePoolMonitorDaemon() {
        super("DatabasePoolMonitor", "monitor", SystemInfo.HOST_NAME);
    }

    @Override
    protected void collectData() throws SQLException {
        ConnectionPool pool =
                SingletonS2Container.getComponent(ConnectionPool.class);
        storeValue(System.currentTimeMillis(), "ActivePoolSize",
                pool.getActivePoolSize());
        storeValue(System.currentTimeMillis(), "TxActivePoolSize",
                pool.getTxActivePoolSize());
        storeValue(System.currentTimeMillis(), "MaxPoolSize",
                pool.getMaxPoolSize());
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return SingletonS2Container.getComponent(DataSource.class).getConnection();
    }
}
