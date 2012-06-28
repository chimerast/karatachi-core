package org.karatachi.wicket.grid;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class Grid extends Loop {
    private static final long serialVersionUID = 1L;

    private final IModel<Cells> cells;

    public Grid(final IModel<Cells> model) {
        super("rows", new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                return model.getObject().getRows();
            }
        });
        cells = model;
    }

    @Override
    protected void populateItem(LoopItem item) {
        final int row = item.getIteration() + 1;

        item.add(new Loop("cols", new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                return cells.getObject().getCols();
            }
        }) {
            private static final long serialVersionUID = 1L;

            private int joined = 0;

            @Override
            protected void populateItem(LoopItem item) {
                final int col = item.getIteration() + 1;

                if (joined > 0) {
                    item.setVisible(false);
                    --joined;
                } else {
                    ICell cell = cells.getObject().getCell(row, col);
                    item.add(createCellComponent("cell", row, col, cell).setRenderBodyOnly(
                            true));
                    setCellAttribute(item, row, col,
                            cells.getObject().getCell(row, col));
                    if (cell != null && cell.getColspan() > 1) {
                        item.add(new SimpleAttributeModifier("colspan",
                                Integer.toString(cell.getColspan())));
                        joined = cell.getColspan() - 1;
                    }

                    if (cell instanceof ILinkCell) {
                        final ILinkCell link = (ILinkCell) cell;
                        item.add(new AjaxEventBehavior("onclick") {
                            private static final long serialVersionUID = 1L;

                            @Override
                            protected IAjaxCallDecorator getAjaxCallDecorator() {
                                return link.getAjaxCallDecorator();
                            }

                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                link.onClick(target);
                            }
                        });
                    }
                }
            }
        });
    }

    protected Component createCellComponent(String id, int row, int col,
            ICell cell) {
        StringBuilder sb = new StringBuilder();
        if (cell != null) {
            sb.append(cell.toString());
        } else {
            sb.append("N/A");
        }
        return new Label(id, sb.toString()).setEscapeModelStrings(false);
    }

    protected void setCellAttribute(Component component, int row, int col,
            ICell cell) {
        if (cell == null) {
            return;
        }

        String style = cell.getStyle();
        if (style != null) {
            component.add(new SimpleAttributeModifier("style", style));
        }

        String className = cell.getClassName();
        if (className != null) {
            component.add(new SimpleAttributeModifier("class", className));
        }

        cell.setupComponent(component);
    }
}
