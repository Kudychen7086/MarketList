package cbp.marketlist.tableview;

/**
 * Row构造工具
 *
 * @author cbp
 */

public class RowBuilder {
    private Row row;

    private RowBuilder(Row convertRow) {
        if (convertRow == null) {
            row = new Row();
        } else {
            convertRow.clearCells();
            row = convertRow;
        }
    }

    public static RowBuilder makeRow(Row convertRow) {
        return new RowBuilder(convertRow);
    }

    public RowBuilder setBackgroundColor(int color) {
        row.backgroundColor = color;
        return this;
    }

    public RowBuilder addCell(Cell cell) {
        row.addCell(cell);
        return this;
    }

    public RowBuilder setPriceCellIndex(int index) {
        row.priceCellIndex = index;
        return this;
    }

    public Row build() {
        return row;
    }

}
