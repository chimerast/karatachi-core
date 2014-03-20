package org.karatachi.wicket.grid;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.Loop;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class Grid extends Loop {
    private static final long serialVersionUID = 1L;

    private final IModel<Cells> cells;

    private int[] rowjoined;

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
    protected void onBeforeRender() {
        rowjoined = new int[cells.getObject().getCols()];
        super.onBeforeRender();
    }

    @Override
    protected void populateItem(LoopItem item) {
        final int row = item.getIndex() + 1;

        item.add(new Loop("cols", new LoadableDetachableModel<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected Integer load() {
                return cells.getObject().getCols();
            }
        }) {
            private static final long serialVersionUID = 1L;

            private int coljoined = 0;

            @Override
            protected void populateItem(LoopItem item) {
                final int col = item.getIndex() + 1;

                if (coljoined > 0 || rowjoined[col - 1] > 0) {
                    item.setVisible(false);
                    --coljoined;
                    --rowjoined[col - 1];
                } else {
                    ICell cell = cells.getObject().getCell(row, col);
                    item.add(createCellComponent("cell", row, col, cell).setRenderBodyOnly(
                            true));
                    setCellAttribute(item, row, col,
                            cells.getObject().getCell(row, col));
                    if (cell != null && cell.getRowspan() > 1) {
                        item.add(new AttributeModifier("rowspan",
                                Integer.toString(cell.getRowspan())));
                        rowjoined[col - 1] = cell.getRowspan() - 1;
                    }
                    if (cell != null && cell.getColspan() > 1) {
                        item.add(new AttributeModifier("colspan",
                                Integer.toString(cell.getColspan())));
                        coljoined = cell.getColspan() - 1;
                    }

                    if (cell instanceof ILinkCell) {
                        final ILinkCell link = (ILinkCell) cell;
                        item.add(new AjaxEventBehavior("onclick") {
                            private static final long serialVersionUID = 1L;

                            @Override
                            protected void updateAjaxAttributes(
                                    AjaxRequestAttributes attributes) {
                                super.updateAjaxAttributes(attributes);

                                link.updateAjaxAttributes(attributes);
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
            component.add(new AttributeModifier("style", style));
        }

        String className = cell.getClassName();
        if (className != null) {
            component.add(new AttributeModifier("class", className));
        }

        cell.setupComponent(component);
    }
}
