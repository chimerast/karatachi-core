package org.karatachi.example.web.monitor;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.monitor.DataSourceMonitorPanel;

public class DataSourceMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public DataSourceMonitorPage() {
        add(new DataSourceMonitorPanel("datasource"));
    }
}
