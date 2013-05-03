package org.karatachi.wicket.monitor.jmx;

import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.extensions.markup.html.repeater.tree.TableTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.table.TreeColumn;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.WindowsTheme;
import org.apache.wicket.extensions.markup.html.repeater.util.TreeModelProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.karatachi.jmx.MBeanNode;
import org.karatachi.jmx.MBeanServerWrapper;
import org.karatachi.jmx.MBeanTree;

public class MBeanTreeTable extends TableTree<MBeanNode, Void> {
    private static final long serialVersionUID = 1L;

    private static final List<AbstractColumn<MBeanNode, Void>> COLUMNS =
            Arrays.asList(new TreeColumn<MBeanNode, Void>(Model.of("Name")),
                    new ValueColumn());

    public MBeanTreeTable(String id) {
        super(id, COLUMNS, new TreeModelProvider<MBeanNode>(
                new DefaultTreeModel(new MBeanTree(new MBeanServerWrapper())),
                false) {
            private static final long serialVersionUID = 1L;

            @Override
            public IModel<MBeanNode> model(MBeanNode object) {
                return Model.of(object);
            }
        }, Integer.MAX_VALUE);

        add(new WindowsTheme());

        getTable().addTopToolbar(new HeadersToolbar<Void>(getTable(), null));
        getTable().addBottomToolbar(new NoRecordsToolbar(getTable()));
    }

    @Override
    protected Component newContentComponent(String id, IModel<MBeanNode> model) {
        return new MBeanLabel(id, model);
    }
}
