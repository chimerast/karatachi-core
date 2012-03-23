package org.karatachi.example.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.management.JMException;
import javax.sql.DataSource;

import org.apache.wicket.markup.html.WebPage;
import org.h2.tools.RunScript;
import org.karatachi.daemon.monitor.MBeanMonitorDaemon;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.wicket.S2WebApplication;

public class ExampleApplication extends S2WebApplication {
    @Override
    protected void init() {
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        getDebugSettings().setAjaxDebugModeEnabled(false);

        getRequestLoggerSettings().setRequestLoggerEnabled(true);

        // initializeDatabase();

        setupMonitor();
    }

    private void initializeDatabase() {
        // オンメモリデータベースのテーブル作成
        DataSource dataSource =
                SingletonS2Container.getComponent(DataSource.class);
        try {
            Connection conn = dataSource.getConnection();
            try {
                RunScript.execute(conn, new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(
                                "init.sql"), "UTF-8"));
            } finally {
                conn.close();
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private void setupMonitor() {
        MBeanMonitorDaemon monitor =
                new MBeanMonitorDaemon("MBeanMonitor", "monitor") {
                    @Override
                    protected Connection getConnection() throws SQLException {
                        DataSource dataSource =
                                SingletonS2Container.getComponent(DataSource.class);
                        return dataSource.getConnection();
                    }
                };

        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(
                            getClass().getResourceAsStream("monitor.mbean")));
            String line;
            while ((line = in.readLine()) != null) {
                try {
                    monitor.addAccessor(
                            line.substring(line.lastIndexOf(":") + 1), line);
                } catch (JMException e) {
                    System.out.println("error: " + e.getMessage());
                }
            }
        } catch (IOException ignore) {
            System.out.println("error: " + ignore.getMessage());
        }

        monitor.startup();
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return IndexPage.class;
    }
}
