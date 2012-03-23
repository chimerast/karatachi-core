package org.karatachi.example.web.monitor;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.monitor.MBeanMonitorPanel;

public class MBeanTreePage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public MBeanTreePage() {
        add(new MBeanMonitorPanel("mbean"));
    }
}
