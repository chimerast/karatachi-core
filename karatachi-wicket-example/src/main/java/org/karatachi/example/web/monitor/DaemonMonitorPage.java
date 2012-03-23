package org.karatachi.example.web.monitor;

import org.karatachi.daemon.DaemonManager;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.monitor.DaemonMonitorPanel;

public class DaemonMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public DaemonMonitorPage() {
        add(new DaemonMonitorPanel("daemon",
                DaemonManager.UniqueDaemonGroup.class));
    }
}
