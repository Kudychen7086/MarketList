package cbp.marketlist.tableview;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cbp.marketlist.data.Key;
import cbp.marketlist.data.MapData;

/**
 * 充当总数据的本地数据窗口（概念上类似数据库的Table）
 * 名词：
 * totalDataSet：总数据集（服务器能提供的总数据量）
 * windowDataSet：窗口数据集（本地维护的一个数据集，是服务器数据集的一部分）
 *
 * @author cbp
 */
public final class TableData {
    //初始窗口大小，也用于增量（请求数据时）
    //public static final int DEFAULT_WINDOW_SIZE = 20;
    //数据总量
    private int totalDataSetCount;
    //当前数据窗口的起始位置，相对于总数据集
    private int startPositionInTotalDataSet;
    //表体数据
    private LinkedList<MapData> data = new LinkedList<>();
    //设置主键，用于标示row
    private Key<?> primaryKey;

    TableData() {

    }

    public TableData(List<MapData> data) {
        this.data.addAll(data);
    }

    public void setTotalDataSetCount(int totalDataSetCount) {
        this.totalDataSetCount = totalDataSetCount;
    }


    public int getTotalDataSetCount() {
        return totalDataSetCount;
    }

    public int getRowCount() {
        return data.size();
    }

    public void setStartPositionInTotalDataSet(int startPositionInTotalDataSet) {
        this.startPositionInTotalDataSet = startPositionInTotalDataSet;
    }

    public int getStartPositionInTotalDataSet() {
        return startPositionInTotalDataSet;
    }


    public MapData getRowData(int position) {
        return data.get(position);
    }

    public LinkedList<MapData> getRowDatas() {
        return (LinkedList<MapData>) data.clone();
    }

    public void setPK(Key<?> primaryKey) {
        this.primaryKey = primaryKey;
    }

    Key<?> getPK() {
        return primaryKey;
    }

    /**
     * 更新当前tableData，如果存在则更新现有的，不存在则忽略
     *
     * @param newTableData  TableData
     * @param rowComparator 判断两个MapData实例是否同一行
     */
    synchronized void updateFrom(TableData newTableData, Comparator<MapData> rowComparator) {
        primaryKey = newTableData.primaryKey;
        totalDataSetCount = newTableData.totalDataSetCount;
        startPositionInTotalDataSet = newTableData.startPositionInTotalDataSet;
        int index = 0;
        for (MapData oldData : data) {
            for (MapData newData : newTableData.data) {
                if (rowComparator.compare(oldData, newData) == 0) {
                    //相同行，替换更新
                    data.set(index, newData);
                    break;
                }
            }
            index++;
        }
    }

    /**
     * 追加到当前tableData（不判重）
     */
    synchronized void appendFrom(TableData newTableData) {
        data.addAll(newTableData.data);
    }

    /**
     * 替换内部tableData的数据
     */
    @SuppressWarnings("unchecked")
    synchronized void replaceBy(TableData newTableData) {
        primaryKey = newTableData.primaryKey;
        totalDataSetCount = newTableData.totalDataSetCount;
        startPositionInTotalDataSet = newTableData.startPositionInTotalDataSet;
        data = (LinkedList<MapData>) newTableData.data.clone();
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized Object clone() {
        TableData clone = new TableData();
        clone.primaryKey = primaryKey;
        clone.totalDataSetCount = totalDataSetCount;
        clone.totalDataSetCount = startPositionInTotalDataSet;
        clone.data = (LinkedList<MapData>) data.clone();
        return clone;
    }
}