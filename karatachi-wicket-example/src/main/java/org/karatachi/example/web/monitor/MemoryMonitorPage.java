package org.karatachi.example.web.monitor;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.monitor.MemoryMonitorPanel;

public class MemoryMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public MemoryMonitorPage() {
        add(new MemoryMonitorPanel("memory"));
    }
}
