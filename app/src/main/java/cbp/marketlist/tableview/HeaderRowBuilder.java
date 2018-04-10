package cbp.marketlist.tableview;

import java.util.HashMap;
import java.util.Map;

/**
 * header构造工具
 *
 * @author cbp
 */

public class HeaderRowBuilder {
    private static final String TAG = "HeaderRowBuilder";
    private Row headerRow;
    private String[] titles;
    private Map<Integer, HeaderCell.SortType> sortIdAndType;
    private Style style;
    private Map<Integer, Style> withStyleCells;
    private boolean clickable = true;
    private Map<Integer, Boolean> clickableCells;
    private boolean sortable = true;
    private Map<Integer, Boolean> sortableCells;
    private Style selectedStyle;
    private Map<Integer, Style> withSelectedStyleCells;
    private int columnWidthInDip;
    private Map<Integer, Integer> withColumnWidthCells;
    private Cell.Gravity gravity;
    private int firstColumnPaddingLeft;
    private Map<Integer, Cell.Gravity> withGravityCells;
    private Cell.OnCellClickListener listener;
    private Map<Integer, Cell.OnCellClickListener> withClickListenerCells;
    private Cell.OnCellLongClickListener longListener;
    private Map<Integer, Cell.OnCellLongClickListener> withLongClickListenerCells;

    public static HeaderRowBuilder makeTitles(String... titles) {
        return new HeaderRowBuilder(titles);
    }

    private HeaderRowBuilder(String[] titles) {
        headerRow = new Row();
        this.titles = titles;
    }

    /**
     * @param index    排序角标，对应titles的角标
     * @param sortType 排序方式
     * @return HeaderRowBuilder
     */
    public HeaderRowBuilder setSortColumnIndexAndType(int index, HeaderCell.SortType sortType) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (sortIdAndType == null) {
            sortIdAndType = new HashMap<>();
            sortIdAndType.put(index, sortType);
        } else {
            sortIdAndType.clear();
            sortIdAndType.put(index, sortType);
        }
        return this;
    }

    public HeaderRowBuilder setStyle(Style style) {
        this.style = style;
        return this;
    }

    public HeaderRowBuilder setStyle(int index, Style style) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (withStyleCells == null) {
            withStyleCells = new HashMap<>();
        }
        withStyleCells.put(index, style);
        return this;
    }

    public HeaderRowBuilder setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public HeaderRowBuilder setSortable(int index, boolean sortable) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (sortableCells == null) {
            sortableCells = new HashMap<>();
        }
        sortableCells.put(index, sortable);
        return this;
    }

    public HeaderRowBuilder setClickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    public HeaderRowBuilder setClickable(int index, boolean clickable) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (clickableCells == null) {
            clickableCells = new HashMap<>();
        }
        clickableCells.put(index, clickable);
        return this;
    }

    public HeaderRowBuilder setSelectedStyle(Style selectedStyle) {
        this.selectedStyle = selectedStyle;
        return this;
    }

    public HeaderRowBuilder setSelectedStyle(int index, Style selectedStyle) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (withSelectedStyleCells == null) {
            withSelectedStyleCells = new HashMap<>();
        }
        withSelectedStyleCells.put(index, selectedStyle);
        return this;
    }

    public HeaderRowBuilder setColumnWidthInDip(int columnWidthInDip) {
        this.columnWidthInDip = columnWidthInDip;
        return this;
    }

    public HeaderRowBuilder setColumnWidthInDip(int index, int columnWidthInDip) {
        if (index < 0 || index >= titles.length || columnWidthInDip <= 0) {
            return this;
        }
        if (withColumnWidthCells == null) {
            withColumnWidthCells = new HashMap<>();
        }
        withColumnWidthCells.put(index, columnWidthInDip);
        return this;
    }

    public HeaderRowBuilder setGravity(Cell.Gravity gravity) {
        this.gravity = gravity;
        return this;
    }

    public HeaderRowBuilder setGravity(int index, Cell.Gravity gravity) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (withGravityCells == null) {
            withGravityCells = new HashMap<>();
        }
        withGravityCells.put(index, gravity);
        return this;
    }

    /**
     * 设置第一个标题文字左边距
     * @param paddingLeft 左边距pixel
     */
    public HeaderRowBuilder setFirstColumnPaddingLeft(int paddingLeft){
        this.firstColumnPaddingLeft = paddingLeft;
        return this;
    }

    public HeaderRowBuilder setCellClickListener(Cell.OnCellClickListener listener) {
        this.listener = listener;
        return this;
    }

    public HeaderRowBuilder setCellClickListener(int index, Cell.OnCellClickListener listener) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (withClickListenerCells == null) {
            withClickListenerCells = new HashMap<>();
        }
        withClickListenerCells.put(index, listener);
        return this;
    }

    public HeaderRowBuilder setCellLongClickListenser(Cell.OnCellLongClickListener longListener) {
        this.longListener = longListener;
        return this;
    }

    public HeaderRowBuilder setCellLongClickListenser(int index, Cell.OnCellLongClickListener longListener) {
        if (index < 0 || index >= titles.length) {
            return this;
        }
        if (withLongClickListenerCells == null) {
            withLongClickListenerCells = new HashMap<>();
        }
        withLongClickListenerCells.put(index, longListener);
        return this;
    }

    public Row build() {
        for (int i = 0; i < titles.length; i++) {
            HeaderCell headerCell = new HeaderCell();
            headerCell.setText(titles[i]);
            if (sortIdAndType != null && sortIdAndType.get(i) != null) {
                headerCell.setSortType(sortIdAndType.get(i));
            }
            if (withStyleCells != null && withStyleCells.get(i) != null) {
                headerCell.setStyle(withStyleCells.get(i));
            } else if (style != null) {
                headerCell.setStyle(style);
            }
            if (clickableCells != null && clickableCells.get(i) != null) {
                headerCell.setClickable(clickableCells.get(i));
            } else if (!clickable) {
                headerCell.setClickable(false);
            }
            if (sortableCells != null && sortableCells.get(i) != null) {
                headerCell.setSortable(sortableCells.get(i));
            } else if (!sortable) {
                headerCell.setSortable(false);
            }
            if (withSelectedStyleCells != null && withSelectedStyleCells.get(i) != null) {
                headerCell.setSelectedStyle(withSelectedStyleCells.get(i));
            } else if (selectedStyle != null) {
                headerCell.setSelectedStyle(selectedStyle);
            }
            if (withColumnWidthCells != null && withColumnWidthCells.get(i) != null && columnWidthInDip > 0) {
                headerCell.setColumnWidthInDip(withColumnWidthCells.get(i));
            } else if (columnWidthInDip > 0) {
                headerCell.setColumnWidthInDip(columnWidthInDip);
            }
            if (withGravityCells != null && withGravityCells.get(i) != null) {
                headerCell.setGravity(withGravityCells.get(i));
            } else if (gravity != null) {
                headerCell.setGravity(gravity);
            }
            if (headerCell.getGravity() == Cell.Gravity.LEFT
                    || headerCell.getGravity() == Cell.Gravity.LEFT_TOP
                    || headerCell.getGravity() == Cell.Gravity.LEFT_BOTTOM){
                headerCell.setPaddingLeft(firstColumnPaddingLeft);
            }
            if (withClickListenerCells != null && withClickListenerCells.get(i) != null) {
                headerCell.setOnCellClickListener(withClickListenerCells.get(i));
            } else if (listener != null) {
                headerCell.setOnCellClickListener(listener);
            }
            if (withLongClickListenerCells != null && withLongClickListenerCells.get(i) != null) {
                headerCell.setOnCellLongClickListener(withLongClickListenerCells.get(i));
            } else if (longListener != null) {
                headerCell.setOnCellLongClickListener(longListener);
            }
            headerRow.addCell(headerCell);
        }
        return headerRow;
    }
}
