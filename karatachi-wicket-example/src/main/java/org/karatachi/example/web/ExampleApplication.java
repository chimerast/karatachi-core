package org.karatachi.example.web;

import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.h2.tools.RunScript;
import org.karatachi.daemon.monitor.MBeanMonitorDaemon;
import org.karatachi.example.component.SourceViewPage;
import org.karatachi.example.web.net.DownloadTestPage;
import org.karatachi.wicket.core.NoSerializePageManagerProvider;
import org.karatachi.wicket.system.PackageMounter;
import org.seasar.framework.container.SingletonS2Container;

public class ExampleApplication extends WebApplication {
    @Override
    protected void init() {

        setPageManagerProvider(new NoSerializePageManagerProvider(this,
                getPageManagerContext(), 20));

        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
        getRequestCycleSettings().setGatherExtendedBrowserInfo(true);

        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");

        getDebugSettings().setAjaxDebugModeEnabled(false);

        PackageMounter.mount("org.karatachi.example.web");
        mountPage("/source", SourceViewPage.class);
        mountPage("/env", DownloadTestPage.class);

        initializeDatabase();

        setupMonitor();
    }

    private void initializeDatabase() {
        // オンメモリデータベースの作成
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

    private void setupMonitor() {
        // MBeanDaemon(<デーモン名>, <DBテーブル名>)
        // DBのテーブルはinit.sqlを参照
        MBeanMonitorDaemon monitor =
                new MBeanMonitorDaemon("MBeanMonitor", "monitor") {
                    @Override
                    protected Connection getConnection() throws SQLException {
                        // DBアクセスに使用するConnectionオブジェクトを
                        DataSource dataSource =
                                SingletonS2Container.getComponent(DataSource.class);
                        return dataSource.getConnection();
                    }
                };

        // monitor.mbeanファイルから監視を行うMBeanを設定する
        try {
            List<String> lines =
                    IOUtils.readLines(getClass().getResourceAsStream(
                            "monitor.mbean"));
            for (String line : lines) {
                int i = line.indexOf("=");
                monitor.addAccessor(line.substring(0, i).trim(),
                        line.substring(i + 1).trim());
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }

        // モニタリング開始
        monitor.startup();

        new DatabasePoolMonitorDaemon().startup();
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return IndexPage.class;
    }
}
