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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

class FixedGrid extends Loop {
    private static final long serialVersionUID = 1L;

    private static final AttributeModifier NOBORDER =
            new AttributeModifier("style",
                    "border: none 0px transparent !important; overflow: hidden !important;");
    private static final String CELL_FORMAT =
            "width: %dpx !important; height: %dpx !important; overflow: hidden !important;";
    private static final String SPACER_IMG_FORMAT =
            "<img src=\"%s\" style=\"width: %dpx; height: %dpx\" />";
    private static final ResourceReference SPACER =
            new PackageResourceReference(FixedGrid.class, "spacer.gif");

    private final IModel<Cells> model;
    private final FixedGridPanel owner;
    private final Position position;

    private int[] rowjoined;

    public FixedGrid(final IModel<Cells> model, final FixedGridPanel owner,
            final Position position) {
        super("rows", null);
        setDefaultModel(new PropertyModel<Integer>(this, "rows"));

        this.model = model;
        this.owner = owner;
        this.position = position;
    }

    @Override
    protected void onBeforeRender() {
        rowjoined = new int[getCols()];
        super.onBeforeRender();
    }

    @Override
    protected void populateItem(LoopItem item) {
        final Cells cells = model.getObject();

        final int adjustedRows = getRows();
        final int adjustedCols = getCols();

        final int adjustedRow = item.getIndex() + 1;
        final int row;
        switch (position) {
        case TOP_LEFT:
        case TOP_RIGHT:
            row = adjustedRow;
            break;
        default:
            row = adjustedRow + owner.getFixedRow();
            break;
        }

        item.add(new Loop("cols", new PropertyModel<Integer>(this, "cols")) {
            private static final long serialVersionUID = 1L;

            private int coljoined = 0;

            @Override
            protected void populateItem(LoopItem item) {
                final int adjustedCol = item.getIndex() + 1;
                final int col;
                switch (position) {
                case TOP_LEFT:
                case BOTTOM_LEFT:
                    col = adjustedCol;
                    break;
                default:
                    col = adjustedCol + owner.getFixedCol();
                    break;
                }

                if (adjustedRow < adjustedRows && adjustedCol < adjustedCols) {
                    if (coljoined > 0 || rowjoined[adjustedCol - 1] > 0) {
                        item.setVisible(false);
                        --coljoined;
                        --rowjoined[adjustedCol - 1];
                    } else {
                        ICell cell = cells.getCell(row, col);
                        populateCell(item, row, col, cell);
                        if (cell != null && cell.getRowspan() > 1) {
                            item.add(new AttributeModifier("rowspan",
                                    Integer.toString(cell.getRowspan())));
                            rowjoined[adjustedCol - 1] = cell.getRowspan() - 1;
                        }
                        if (cell != null && cell.getColspan() > 1) {
                            item.add(new AttributeModifier("colspan",
                                    Integer.toString(cell.getColspan())));
                            coljoined = cell.getColspan() - 1;
                        }
                    }
                } else if (adjustedRow < adjustedRows) {
                    StringBuilder sb = new StringBuilder();
                    // 最後までスクロールをしたときの遊び
                    if (position == Position.TOP_RIGHT) {
                        sb.append(String.format(SPACER_IMG_FORMAT,
                                urlFor(SPACER, null), 40, 0));
                    }
                    item.add(new Label("cell", sb.toString()).setEscapeModelStrings(
                            false).setRenderBodyOnly(true));
                    item.add(NOBORDER);
                } else if (adjustedCol < adjustedCols) {
                    StringBuilder sb = new StringBuilder();
                    // 最後までスクロールをしたときの遊び
                    if (position == Position.BOTTOM_LEFT) {
                        sb.append(String.format(SPACER_IMG_FORMAT,
                                urlFor(SPACER, null), 0, 40));
                    }
                    item.add(new Label("cell", sb.toString()).setEscapeModelStrings(
                            false).setRenderBodyOnly(true));
                    item.add(NOBORDER);
                } else {
                    item.add(new Label("cell", "").setEscapeModelStrings(false).setEscapeModelStrings(
                            false).setRenderBodyOnly(true));
                    item.add(NOBORDER);
                }
            }
        });
    }

    private void populateCell(LoopItem item, int r, int c, ICell cell) {
        Component component;
        item.add(component = createCellComponent("cell", r, c, cell));
        setCellAttribute(item, r, c, model.getObject().getCell(r, c));

        if (cell != null) {
            int width;
            if (cell.getColspan() > 1) {
                int colspan = cell.getColspan();
                width = 0;
                for (int i = 0; i < colspan; ++i) {
                    width += owner.getWidth(c + i);
                }
            } else {
                width = owner.getWidth(c);
            }

            int height;
            if (cell.getRowspan() > 1) {
                int rowspan = cell.getRowspan();
                height = 0;
                for (int i = 0; i < rowspan; ++i) {
                    height += owner.getHeight(r + i);
                }
            } else {
                height = owner.getHeight(r);
            }

            component.add(new AttributeModifier("style", String.format(
                    CELL_FORMAT, width, height)));
        } else {
            component.add(new AttributeModifier("style", String.format(
                    CELL_FORMAT, owner.getWidth(c), owner.getHeight(r))));
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

    protected Component createCellComponent(String id, int row, int col,
            ICell cell) {
        StringBuilder sb = new StringBuilder();
        if (cell != null) {
            if (cell.getIndent() != 0) {
                sb.append(String.format(SPACER_IMG_FORMAT,
                        urlFor(SPACER, null), cell.getIndent(), 0));
            }
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

    public int getRows() {
        switch (position) {
        case TOP_LEFT:
        case TOP_RIGHT: {
            if (model.getObject().getRows() <= owner.getFixedRow()) {
                return model.getObject().getRows() + 1;
            } else {
                return owner.getFixedRow() + 1;
            }
        }
        default: {
            if (model.getObject().getRows() <= owner.getFixedRow()) {
                return 0;
            } else {
                return model.getObject().getRows() - owner.getFixedRow() + 1;
            }
        }
        }
    }

    public int getCols() {
        switch (position) {
        case TOP_LEFT:
        case BOTTOM_LEFT: {
            if (model.getObject().getCols() <= owner.getFixedCol()) {
                return model.getObject().getCols() + 1;
            } else {
                return owner.getFixedCol() + 1;
            }
        }
        default:
            if (model.getObject().getCols() <= owner.getFixedCol()) {
                return 0;
            } else {
                return model.getObject().getCols() - owner.getFixedCol() + 1;
            }
        }
    }

    enum Position {
        TOP_LEFT("tl"), TOP_RIGHT("tr"), BOTTOM_LEFT("bl"), BOTTOM_RIGHT("br");

        private final String code;

        private Position(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
