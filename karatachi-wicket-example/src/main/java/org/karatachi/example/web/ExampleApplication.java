package org.karatachi.example.web;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
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

        initializeDatabase();

        setupMonitor();
    }

    private void initializeDatabase() {
        // オンメモリデータベースのテーブル作成
        DataSource dataSource =
                SingletonS2Container.getComponent(DataSource.class);
        try {
            Connection conn = dataSource.getConnection();
            try {
                RunScript.execute(
                        conn,
                        new InputStreamReader(
                                getClass().getClassLoader().getResourceAsStream(
                                        "init.sql"), "UTF-8"));
            } finally {
                conn.close();
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
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
            List<String> lines =
                    IOUtils.readLines(getClass().getResourceAsStream(
                            "monitor.mbean"));
            for (String line : lines) {
                monitor.addAccessor(line.substring(line.lastIndexOf(":") + 1),
                        line);
            }
        } catch (Exception ignore) {
            System.out.println("error: " + ignore.getMessage());
        }

        monitor.startup();
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return IndexPage.class;
    }
}
