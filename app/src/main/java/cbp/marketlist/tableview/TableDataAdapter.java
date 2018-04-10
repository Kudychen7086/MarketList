package cbp.marketlist.tableview;

import android.os.Handler;
import android.text.TextUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import cbp.marketlist.data.Key;
import cbp.marketlist.data.MapData;
import cbp.marketlist.utils.ContextUtil;

/**
 * 充当数据和UI的中间桥梁，负责为UI提供数据的外观模式。
 * 样式也在这里考虑，例如以下问题：
 * 1)字段的显示文字（表头文字）
 * 2)字段合并在一列显示（列的产生）
 * 3)字段值显示的默认样式（文字大小，颜色，位置，对齐等等）
 * 4)皮肤问题
 *
 * @author cbp
 */
public abstract class TableDataAdapter extends TableViewAdapter {
    //内部数据不暴露给外部程序直接操作
    private final TableData tableData;
    private TableView tableView;
    private final int CACHE_SIZE = 100;
    private Handler uiHandler = new Handler(ContextUtil.getContext().getMainLooper());
    private Style nonExistRowStyle = new Style(16, 0xFFFFFFFF, 0xFF0D0D0D);
    //缓存上一次的价格数据
    private WeakHashMap<Object, String> priceCache = new WeakHashMap<>();
    private Runnable resetRunnable = new Runnable() {
        @Override
        public void run() {
            if (tableView != null) {
                tableView.notifyDataChanged();
            }
        }
    };

    protected TableDataAdapter() {
        tableData = new TableData();
    }

    /**
     * 更新表数据
     *
     * @param newTableData  TableData
     * @param rowComparator 判断两个MapData实例是否同一行
     */
    public void updateTableData(TableData newTableData, Comparator<MapData> rowComparator) {
        //todo update的标准？id
        tableData.updateFrom(newTableData, rowComparator);
    }

    /**
     * 追加表数据
     */
    public void appendTableData(TableData newTableData) {
        tableData.appendFrom(newTableData);
    }

    /**
     * 替换表数据
     */
    public void replaceTableData(TableData newTableData) {
        tableData.replaceBy(newTableData);
    }

    public TableData getTableData() {
        return (TableData) tableData.clone();
    }

    final void setTableView(TableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public abstract Row getHeaderRow();

    @Override
    public abstract Row getRow(int position, Row convertRow);

    final Row getRowInternal(int position, Row convertRow) {
        Row newRow = getRow(position, convertRow);
        if (newRow == null) {
            return getNonExistRow(nonExistRowStyle, convertRow);
        }
        Key<?> pk = tableData.getPK(); //列名，例如：stockCode
        if (pk != null) {
            MapData rowData = tableData.getRowData(position);
            Object pkValue = rowData.get(pk);  //当前行的主键的值，例如：SH000001
            String prePrice = priceCache.get(pkValue);
            String curPrice;
            try {
                List<Cell> newCells = newRow.getCells();
                int priceCellIndex = newRow.priceCellIndex;
                Cell priceCell = newCells.get(priceCellIndex);
                if (priceCell instanceof SingleTextCell) {
                    curPrice = ((SingleTextCell) priceCell).getText();
                    if (prePrice != null) {
                        //有缓存
                        if (!TextUtils.equals(curPrice, prePrice)) {
                            //此处做股票价格变化时的背景高亮，由于需要依赖其他模块，本示例中不实现
//                            Double preValue = Double.parseDouble(prePrice);
//                            Double curValue = Double.parseDouble(curPrice);
//                            if (preValue > curValue) {
//                                newRow.backgroundDrawable = SkinThemeCache.getDrawable(R.drawable.price_down_gradient_bg);
//                            } else if (preValue < curValue) {
//                                newRow.backgroundDrawable = SkinThemeCache.getDrawable(R.drawable.price_up_gradient_bg);
//                            }
                        } else {
                            newRow.backgroundDrawable = null;
                        }
                    } else {
                        newRow.backgroundDrawable = null;
                    }
                    priceCache.put(pkValue, curPrice);
                }
            } catch (Exception e) {
                newRow.backgroundDrawable = null;
            }
        }
        return newRow;
    }

    @Override
    public void notifyDataChanged() {
        if (tableView != null) {
            tableView.notifyDataChanged();
            uiHandler.removeCallbacks(resetRunnable);
            uiHandler.postDelayed(resetRunnable, 2 * 1000);
            trimCacheToSize();
        }
    }

    /**
     * 避免缓存过多数据
     */
    private void trimCacheToSize() {
        int cacheCount = priceCache.size();
        if (cacheCount > CACHE_SIZE) {
            int rowCount = tableData.getRowCount();
            if (cacheCount > rowCount) {
                Set<Object> cacheKeys = priceCache.keySet();
                HashMap<Object, String> temp = new HashMap<>();
                for (int i = 0; i < rowCount; i++) {
                    MapData rowData = tableData.getRowData(i);
                    Object pkValue = rowData.get(tableData.getPK());
                    //取出当前展示的row集合
                    if (cacheKeys.contains(pkValue)) {
                        temp.put(pkValue, priceCache.get(pkValue));
                    }
                }
                priceCache.clear();
                priceCache.putAll(temp);
            }
        }
    }

    @Override
    public int getTotalDataCount() {
        return tableData.getTotalDataSetCount();
    }

    @Override
    public int getWindowStartPositionInTotalDataSet() {
        return tableData.getStartPositionInTotalDataSet();
    }

    @Override
    public int getWindowDataCount() {
        return tableData.getRowCount();
    }

}
