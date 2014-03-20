package org.karatachi.example.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.LoadableDetachableModel;
import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.grid.Cells;
import org.karatachi.wicket.grid.DefaultCell;
import org.karatachi.wicket.grid.FixedGridPanel;
import org.karatachi.wicket.grid.ICell;
import org.karatachi.wicket.script.AjaxLibrariesReference;

public class FixedGridPage extends WebBasePage {
    private static final long serialVersionUID = 1L;

    private int rows = 80;
    private int cols = 151;

    public FixedGridPage() {
        final FixedGridPanel grid;
        add(grid = new FixedGridPanel("grid", new CellModel()).fix(1, 1));
        grid.setDefalutWidth(60);
        grid.setWidth(2, 100);
        grid.setWidth(4, 120);
        grid.setHeight(3, 40);

        add(new AjaxLink<Void>("update") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                rows++;
                target.add(grid);
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptHeaderItem.forReference(AjaxLibrariesReference.jquery_migrate));
    }

    private class CellModel extends LoadableDetachableModel<Cells> {
        private static final long serialVersionUID = 1L;

        @Override
        protected Cells load() {
            // データの作成
            Cells ret = new Cells();
            for (int r = 1; r <= rows; ++r) {
                for (int c = 1; c <= cols; ++c) {
                    ICell cell = ret.setValue(r, c, r * c);
                    if (r != 1 && c != 1) {
                        cell.setStyle(String.format(
                                "background-color: rgb(%s,%s,%s)",
                                (64 - r) * 8 + 127, (16 - c) * 8 + 127, 255));
                        if (r == 2 || r == 4) {
                            ((DefaultCell) cell).setColspan(3);
                        }
                        if (r == 5 && (c == 3 || c == 5)) {
                            ((DefaultCell) cell).setRowspan(3);
                        }
                    } else {
                        cell.setStyle("background-color: #FAFAFA");
                    }
                }
            }
            return ret;
        }
    }
}
