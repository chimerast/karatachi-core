package org.karatachi.example.web.monitor;

import java.io.File;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.monitor.LogMonitorPanel;

public class LogMonitorPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    public LogMonitorPage() {
        File[] files =
                new File[] { new File("/var/log/karatachi/error.log"),
                        new File("/var/log/karatachi/karatachi.log"),
                        new File("/var/log/karatachi/database.log"),
                        new File("/var/log/karatachi/wicket.log") };
        add(new LogMonitorPanel("log", files));
    }
}
