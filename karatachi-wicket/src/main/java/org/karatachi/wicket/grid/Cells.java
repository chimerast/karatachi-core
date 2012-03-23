package org.karatachi.wicket.grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cells implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<ArrayList<ICell>> data =
            new ArrayList<ArrayList<ICell>>();

    @Override
    public String toString() {
        return String.format("Cells(rows=%d, cols=%d)", getRows(), getCols());
    }

    public void pivot() {
        Cells temp = new Cells();
        int rows = getRows();
        int cols = getCols();
        for (int i = 1; i <= rows; ++i) {
            for (int j = 1; j <= cols; ++j) {
                temp.putCell(j, i, getCell(i, j));
            }
        }
        data = temp.data;
    }

    public void sortRow(int row, final int col, boolean ascend,
            final Comparator<ICell> comparator) {
        if (row > getRows()) {
            return;
        }
        if (col > getCols()) {
            return;
        }

        List<ArrayList<ICell>> sub = data.subList(row - 1, data.size());
        Collections.sort(sub, new Comparator<ArrayList<ICell>>() {
            @Override
            public int compare(ArrayList<ICell> o1, ArrayList<ICell> o2) {
                return comparator.compare(o1.get(col - 1), o2.get(col - 1));
            }
        });

        if (!ascend) {
            Collections.reverse(sub);
        }
    }

    public void clear() {
        data.clear();
    }

    public int getRows() {
        return data.size();
    }

    public int getCols() {
        int maxcol = 0;
        for (ArrayList<ICell> row : data) {
            if (row == null) {
                continue;
            }
            if (maxcol < row.size()) {
                maxcol = row.size();
            }
        }
        return maxcol;
    }

    public void insertRow(int row) {
        data.add(row - 1, null);
    }

    public void removeRow(int row) {
        data.remove(row - 1);
    }

    public void insertCol(int col) {
        for (ArrayList<ICell> row : data) {
            if (row == null) {
                continue;
            }
            if (col - 1 < row.size()) {
                row.add(col - 1, null);
            }
        }
    }

    public void removeCol(int col) {
        for (ArrayList<ICell> row : data) {
            if (row == null) {
                continue;
            }
            if (col - 1 < row.size()) {
                row.remove(col - 1);
            }
        }
    }

    public ICell getCell(int row, int col) {
        --row;
        --col;

        if (row < data.size() && data.get(row) != null) {
            ArrayList<ICell> r = data.get(row);
            if (col < r.size() && r.get(col) != null) {
                return r.get(col);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void putCell(int row, int col, ICell cell) {
        --row;
        --col;

        while (data.size() <= row) {
            data.add(null);
        }

        if (data.get(row) == null) {
            data.set(row, new ArrayList<ICell>());
        }

        ArrayList<ICell> r = data.get(row);

        while (r.size() <= col) {
            r.add(null);
        }

        r.set(col, cell);
    }

    public Object getValue(int row, int col) {
        ICell cell = getCell(row, col);
        if (cell != null) {
            return cell.getValue();
        } else {
            return "N/A";
        }
    }

    public ICell setValue(int row, int col, Object value) {
        ICell cell = getCell(row, col);
        if (cell != null) {
            cell.setValue(value);
        } else {
            cell = new DefaultCell(value);
            putCell(row, col, cell);
        }
        return cell;
    }
}
