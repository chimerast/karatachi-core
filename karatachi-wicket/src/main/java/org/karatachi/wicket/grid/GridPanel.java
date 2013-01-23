package org.karatachi.wicket.grid;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class GridPanel extends Panel {
    private static final long serialVersionUID = 1L;

    private final WebMarkupContainer grid;

    public GridPanel(String id, Cells cells) {
        this(id, new Model<Cells>(cells));
    }

    public GridPanel(String id, IModel<Cells> cells) {
        super(id, cells);
        add(grid = new WebMarkupContainer("grid"));
        grid.add(new Grid(cells));
    }
}
