package org.karatachi.example.web.monitor;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.system.SystemInfo;
import org.karatachi.wicket.chart.MonitorChartImage;
import org.seasar.framework.container.SingletonS2Container;

public class JVMMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public JVMMonitorPage(PageParameters parameters) {
        super(parameters);

        int level = parameters.getInt("level", 1);

        String[] heap =
                new String[] { "HeapMemoryUsage.init", "HeapMemoryUsage.used",
                        "HeapMemoryUsage.committed", "HeapMemoryUsage.max" };
        String[] nonheap =
                new String[] { "NonHeapMemoryUsage.init",
                        "NonHeapMemoryUsage.used",
                        "NonHeapMemoryUsage.committed",
                        "NonHeapMemoryUsage.max" };
        String[] system =
                new String[] { "CommittedVirtualMemorySize",
                        "FreePhysicalMemorySize", "FreeSwapSpaceSize" };
        String[] thread =
                new String[] { "SystemLoadAverage", "ThreadCount",
                        "DaemonThreadCount", "currentThreadsBusy",
                        "currentThreadCount", "maxThreads", "activeSessions" };

        add(new BookmarkablePageLink<Void>("level0", JVMMonitorPage.class,
                new PageParameters("level=0")));
        add(new BookmarkablePageLink<Void>("level1", JVMMonitorPage.class,
                new PageParameters("level=1")));
        add(new BookmarkablePageLink<Void>("level2", JVMMonitorPage.class,
                new PageParameters("level=2")));
        add(new BookmarkablePageLink<Void>("level3", JVMMonitorPage.class,
                new PageParameters("level=3")));
        add(new BookmarkablePageLink<Void>("level4", JVMMonitorPage.class,
                new PageParameters("level=4")));

        add(new LocalMBeanChartImage("chart1", SystemInfo.HOST_NAME, heap,
                level));
        add(new LocalMBeanChartImage("chart2", SystemInfo.HOST_NAME, nonheap,
                level));
        add(new LocalMBeanChartImage("chart3", SystemInfo.HOST_NAME, system,
                level));
        add(new LocalMBeanChartImage("chart4", SystemInfo.HOST_NAME, thread,
                level));
    }

    private class LocalMBeanChartImage extends MonitorChartImage {
        private static final long serialVersionUID = 1L;

        public LocalMBeanChartImage(String id, String host, String[] titles,
                int level) {
            super(id, 760, 200, "monitor", host, titles, level);
        }

        @Override
        protected Connection getConnection() throws SQLException {
            return SingletonS2Container.getComponent(DataSource.class).getConnection();
        }
    }
}
