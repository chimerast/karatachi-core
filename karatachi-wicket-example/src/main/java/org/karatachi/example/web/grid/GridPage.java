package org.karatachi.example.web.grid;

import org.karatachi.example.web.WebBasePage;
import org.karatachi.wicket.grid.Cells;
import org.karatachi.wicket.grid.DefaultCell;
import org.karatachi.wicket.grid.FixedGridPanel;
import org.karatachi.wicket.grid.ICell;

public class GridPage extends WebBasePage {
    public GridPage() {
        {
            // データの作成
            Cells cells = new Cells();
            for (int r = 1; r <= 64; ++r) {
                for (int c = 1; c <= 16; ++c) {
                    ICell cell = cells.setValue(r, c, r * c);
                    if (r != 1 && c != 1) {
                        cell.setStyle(String.format(
                                "background-color: rgb(%s,%s,%s)", r * 16 - 1,
                                c * 16 - 1, 64));
                        if (r == 2) {
                            ((DefaultCell) cell).setColspan(3);
                        }
                    } else {
                        cell.setStyle(String.format("color: black; background-color: buttonface; border: groove 2px buttonface;"));
                    }
                }
            }

            FixedGridPanel grid;

            add(grid = new FixedGridPanel("grid", cells));
            // スクロール固定位置の設定
            grid.fix(1, 1);
        }
        {
            Cells cells = new Cells();
            cells.setValue(1, 1, "test");
            FixedGridPanel grid;
            add(grid = new FixedGridPanel("grid2", cells));
            grid.fix(3, 3);
        }
    }
}
