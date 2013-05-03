package org.karatachi.example.web.monitor;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.system.SystemInfo;
import org.karatachi.wicket.chart.MonitorChartImage;
import org.seasar.framework.container.SingletonS2Container;

public class JVMMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public JVMMonitorPage(PageParameters parameters) {
        super(parameters);

        int level = parameters.get("level").toInt(1);

        String[] heap =
                new String[] { "HeapMemoryMax", "HeapMemory", "EdenSpace",
                        "SurvivorSpace", "OldGen" };
        String[] nonheap =
                new String[] { "NonHeapMemoryMax", "NonHeapMemory",
                        "CodeCache", "PermGen", };
        String[] system =
                new String[] { "CommittedVirtualMemorySize",
                        "FreePhysicalMemorySize", "FreeSwapSpaceSize" };
        String[] thread =
                new String[] { "ThreadCount", "DaemonThreadCount",
                        "currentThreadsBusy", "currentThreadCount",
                        "maxThreads", "activeSessions" };
        String[] load = new String[] { "SystemLoadAverage" };
        String[] pool =
                new String[] { "ActivePoolSize", "TxActivePoolSize",
                        "MaxPoolSize" };

        PageParameters newParams0 = new PageParameters();
        PageParameters newParams1 = new PageParameters();
        PageParameters newParams2 = new PageParameters();
        PageParameters newParams3 = new PageParameters();
        PageParameters newParams4 = new PageParameters();

        RequestUtils.decodeParameters("level=0", newParams0);
        RequestUtils.decodeParameters("level=1", newParams1);
        RequestUtils.decodeParameters("level=2", newParams2);
        RequestUtils.decodeParameters("level=3", newParams3);
        RequestUtils.decodeParameters("level=4", newParams4);

        add(new BookmarkablePageLink<Void>("link0", JVMMonitorPage.class,
                newParams0));
        add(new BookmarkablePageLink<Void>("link1", JVMMonitorPage.class,
                newParams1));
        add(new BookmarkablePageLink<Void>("link2", JVMMonitorPage.class,
                newParams2));
        add(new BookmarkablePageLink<Void>("link3", JVMMonitorPage.class,
                newParams3));
        add(new BookmarkablePageLink<Void>("link4", JVMMonitorPage.class,
                newParams4));

        add(new LocalMBeanChartImage("chart1", SystemInfo.HOST_NAME, heap,
                level));
        add(new LocalMBeanChartImage("chart2", SystemInfo.HOST_NAME, nonheap,
                level));
        add(new LocalMBeanChartImage("chart3", SystemInfo.HOST_NAME, system,
                level));
        add(new LocalMBeanChartImage("chart4", SystemInfo.HOST_NAME, thread,
                level));
        add(new LocalMBeanChartImage("chart5", SystemInfo.HOST_NAME, load,
                level));
        add(new LocalMBeanChartImage("chart6", SystemInfo.HOST_NAME, pool,
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
