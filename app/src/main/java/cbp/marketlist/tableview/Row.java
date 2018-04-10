package cbp.marketlist.tableview;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Row只是简单容器，不需要onDraw，由TableView负责画出，行高列宽都由TableView控制
 *
 * @author cbp
 */
public class Row {
    //最新价cell在一行cell中的索引，默认为1
    int priceCellIndex = 1;

    int rowPositionInTotalDataSet;

    int backgroundColor = 0x00000000;

    Drawable backgroundDrawable;

    private List<Cell> cells = new ArrayList<>();

    void addCell(Cell cell) {
        cells.add(cell);
    }

    List<Cell> getCells() {
        return cells;
    }

    void clearCells() {
        cells.clear();
    }
}