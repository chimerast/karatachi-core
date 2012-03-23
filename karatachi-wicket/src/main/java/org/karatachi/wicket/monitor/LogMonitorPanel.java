package org.karatachi.wicket.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.karatachi.proc.unix.UnixCommands;

public class LogMonitorPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public LogMonitorPanel(String id, File... logs) {
        this(id, 300, logs);
    }

    public LogMonitorPanel(String id, int lines, File... logs) {
        super(id);
        setRenderBodyOnly(true);

        List<ITab> tabs = new ArrayList<ITab>();
        for (File log : logs) {
            addTab(tabs, log, lines);
        }

        add(new TabbedPanel("tabpanel", tabs));
    }

    private void addTab(List<ITab> tabs, final File log, final int buffer) {
        tabs.add(new AbstractTab(new Model<String>(log.getName())) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(String panelId) {
                return new LogViewPanelTab(panelId, log, buffer);
            }
        });
    }
}

class LogViewPanelTab extends Panel {
    private static final long serialVersionUID = 1L;

    public LogViewPanelTab(String id, final File file, final int lines) {
        super(id);
        add(new Label("log", new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String load() {
                return UnixCommands.tail(lines, file);
            }
        }));
    }
}
