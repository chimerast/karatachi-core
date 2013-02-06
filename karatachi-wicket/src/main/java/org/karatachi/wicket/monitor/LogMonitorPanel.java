package org.karatachi.wicket.monitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.karatachi.text.TextFileCommand;

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

        add(new TabbedPanel<ITab>("tabpanel", tabs));
    }

    public String getTabName(String filename) {
        return filename;
    }

    private void addTab(List<ITab> tabs, final File log, final int buffer) {
        tabs.add(new AbstractTab(new Model<String>(
                getTabName(log.getAbsolutePath()))) {
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
                try {
                    return StringUtils.join(TextFileCommand.tail(lines, file),
                            "\r\n");
                } catch (IOException e) {
                    return e.getMessage();
                }
            }
        }));
    }
}
