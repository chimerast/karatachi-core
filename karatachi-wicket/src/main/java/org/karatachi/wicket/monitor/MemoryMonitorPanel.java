package org.karatachi.wicket.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.wicket.auto.AutoResolveListView;
import org.karatachi.wicket.label.FormattedLabel;

public class MemoryMonitorPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public MemoryMonitorPanel(String id) {
        super(id, new MemorySpaceModel());
        setRenderBodyOnly(true);

        add(new Link<Void>("gc") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                ManagementFactory.getMemoryMXBean().gc();
            }
        });

        add(new AutoResolveListView<MemorySpace>("data", getModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<MemorySpace> item) {
                item.add(new Label("name"));
                item.add(new FormattedLabel("init", "%,d Bytes"));
                item.add(new FormattedLabel("used", "%,d Bytes"));
                item.add(new FormattedLabel("committed", "%,d Bytes"));
                item.add(new FormattedLabel("max", "%,d Bytes"));
                item.add(new FormattedLabel("ratio", "%.2f %%"));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public final IModel<List<MemorySpace>> getModel() {
        return (IModel<List<MemorySpace>>) getDefaultModel();
    }

    private static class MemorySpaceModel extends
            LoadableDetachableModel<List<MemorySpace>> {
        private static final long serialVersionUID = 1L;

        @Override
        public List<MemorySpace> load() {
            List<MemorySpace> ret = new ArrayList<MemorySpace>();

            ret.add(new MemorySpace("Heap Space",
                    ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()));
            ret.add(new MemorySpace("NonHeap Space",
                    ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()));

            int size = ManagementFactory.getMemoryPoolMXBeans().size();
            for (int i = 0; i < size; ++i) {
                MemoryPoolMXBean bean =
                        ManagementFactory.getMemoryPoolMXBeans().get(i);
                ret.add(new MemorySpace(bean.getName(), bean.getUsage()));
            }

            return ret;
        }
    }
}

class MemorySpace {
    private final String name;
    private final MemoryUsage usage;

    public MemorySpace(String name, MemoryUsage usage) {
        this.name = name;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public long getCommitted() {
        return usage.getCommitted();
    }

    public long getInit() {
        return usage.getInit();
    }

    public long getMax() {
        return usage.getMax();
    }

    public long getUsed() {
        return usage.getUsed();
    }

    public double getRatio() {
        return (double) usage.getUsed() / (double) usage.getMax() * 100.0;
    }
}
