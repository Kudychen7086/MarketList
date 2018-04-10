package cbp.marketlist.tableview;

/**
 * TableView适配器
 *
 * @author cbp
 */
abstract class TableViewAdapter {
    private int headerCellCount;

    public abstract Row getHeaderRow();

    public abstract Row getRow(int positionInWindowDataSet, Row convertRow);

    public abstract int getTotalDataCount();//总数据

    public abstract int getWindowStartPositionInTotalDataSet();

    public abstract int getWindowDataCount();

    public abstract void notifyDataChanged();

    Row getNonExistRow(Style nonExistRowStyle, Row convertRow) {
        if (convertRow == null) {
            convertRow = new Row();
        }
        convertRow.clearCells();
        if (headerCellCount <= 0) {
            headerCellCount = getHeaderRow().getCells().size();
        }
        for (int i = 0; i < headerCellCount; i++) {
            Cell.Gravity gravity = Cell.Gravity.RIGHT;
            if (i == 0) {
                gravity = Cell.Gravity.CENTER;
            }
            convertRow.addCell(new SingleTextCell("--", nonExistRowStyle, gravity));
        }
        convertRow.backgroundDrawable = null;
        return convertRow;
    }
}