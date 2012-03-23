package org.karatachi.wicket.monitor;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.db.DataSourceManager;
import org.karatachi.db.DataSourceWrapper;
import org.karatachi.wicket.listview.SimpleListView;

public class DataSourceMonitorPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public DataSourceMonitorPanel(String id) {
        super(id, new LoadableDetachableModel<List<DataSourceWrapper>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<DataSourceWrapper> load() {
                return DataSourceManager.getDataSources();
            }
        });
        setRenderBodyOnly(true);

        add(new SimpleListView<DataSourceWrapper>("jndi", getModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected String getClassAttribute(String id, int index,
                    DataSourceWrapper modelObject) {
                if (modelObject.isMaster()) {
                    return "emphatic";
                } else {
                    return super.getClassAttribute(id, index, modelObject);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public final IModel<List<DataSourceWrapper>> getModel() {
        return (IModel<List<DataSourceWrapper>>) getDefaultModel();
    }
}
