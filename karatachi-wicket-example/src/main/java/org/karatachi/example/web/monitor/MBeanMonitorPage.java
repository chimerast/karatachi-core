package org.karatachi.example.web.monitor;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.monitor.MBeanMonitorPanel;

public class MBeanMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public MBeanMonitorPage() {
        add(new MBeanMonitorPanel("mbean"));
    }
}
