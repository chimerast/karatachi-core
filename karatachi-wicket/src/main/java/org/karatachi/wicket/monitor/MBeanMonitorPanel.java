package org.karatachi.wicket.monitor;

import org.apache.wicket.markup.html.panel.Panel;
import org.karatachi.wicket.monitor.jmx.MBeanTreeTable;

public class MBeanMonitorPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public MBeanMonitorPanel(String id) {
        super(id);
        setRenderBodyOnly(true);

        add(new MBeanTreeTable("jmxTree"));
    }
}
