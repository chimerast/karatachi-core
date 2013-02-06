package org.karatachi.wicket.monitor;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.daemon.Daemon;
import org.karatachi.daemon.DaemonGroup;
import org.karatachi.daemon.DaemonManager;
import org.karatachi.daemon.producer.ProducerDaemon;
import org.karatachi.wicket.auto.AutoResolveListView;
import org.karatachi.wicket.label.DateLabel;
import org.karatachi.wicket.util.BehaviorUtil;

public class DaemonMonitorPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private WebMarkupContainer daemonMonitor;

    public DaemonMonitorPanel(String id,
            final Class<? extends DaemonGroup> daemonGroup) {
        super(id, new CompoundPropertyModel<DaemonGroup>(
                new LoadableDetachableModel<DaemonGroup>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public DaemonGroup load() {
                        return DaemonManager.getDaemonGroup(daemonGroup);
                    }
                }));
        setRenderBodyOnly(true);

        add(new Label("groupName"));
        add(new Label("status", new AbstractReadOnlyModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                return getDaemonGroup().isRunning() ? "RUNNING" : "TERMINATED";
            }
        }));

        add(new Link<Void>("startup") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                getDaemonGroup().startup();
            }
        });
        add(new Link<Void>("shutdown") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                try {
                    getDaemonGroup().shutdown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new WicketRuntimeException(e);
                }
            }
        });

        boolean uniqueDaemon =
                getDaemonGroup() instanceof DaemonManager.UniqueDaemonGroup;
        add(new AjaxLink<Void>("upCount") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                getDaemonGroup().upCount();
                target.add(daemonMonitor);
            }
        }.setVisible(!uniqueDaemon));
        add(new AjaxLink<Void>("downCount") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                getDaemonGroup().downCount();
                target.add(daemonMonitor);
            }
        }.setVisible(!uniqueDaemon));

        add(BehaviorUtil.setSelfUpdate(daemonMonitor =
                new WebMarkupContainer("daemonMonitor"), 1));
        daemonMonitor.add(new AutoResolveListView<Daemon>("daemons") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<Daemon> item) {
                item.add(new DateLabel("nextRun"));
                item.add(new DateLabel("lastStarted"));
            }

            @Override
            protected String getClassAttribute(String id, int index,
                    Daemon modelObject) {
                if (modelObject.getStatus() == Thread.State.RUNNABLE) {
                    return "highlighted";
                } else if (modelObject instanceof ProducerDaemon) {
                    return "emphatic";
                } else {
                    return super.getClassAttribute(id, index, modelObject);
                }
            }
        });
    }

    private DaemonGroup getDaemonGroup() {
        return (DaemonGroup) getDefaultModelObject();
    }
}
