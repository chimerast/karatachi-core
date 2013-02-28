package org.karatachi.wicket.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.karatachi.wicket.label.FormattedLabel;

public class FormattedPropertyColumn<T, S> extends PropertyColumn<T, S> {
    private static final long serialVersionUID = 1L;

    private final String format;

    public FormattedPropertyColumn(IModel<String> displayModel, String format,
            S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
        this.format = format;
    }

    public FormattedPropertyColumn(IModel<String> displayModel, String format,
            String propertyExpression) {
        super(displayModel, propertyExpression);
        this.format = format;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId,
            IModel<T> rowModel) {
        item.add(new FormattedLabel(componentId, format, getDataModel(rowModel)));
    }
}
