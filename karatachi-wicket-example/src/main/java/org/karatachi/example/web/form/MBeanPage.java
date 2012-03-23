package org.karatachi.example.web.form;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.wicket.PageParameters;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.system.SystemInfo;
import org.karatachi.wicket.chart.MonitorChartImage;
import org.seasar.framework.container.SingletonS2Container;

public class MBeanPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public MBeanPage(PageParameters params) {
        int level = params.getInt("level", 1);

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
