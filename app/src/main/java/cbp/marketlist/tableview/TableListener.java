package cbp.marketlist.tableview;

/**
 * TableView滚动监听
 *
 * @author cbp
 */
public interface TableListener {
    /**
     * 滚动停止时触发（只考虑内容滚动时的视觉表现，不管手指是否抬起）
     *
     * @param tableView                             TableView
     * @param firstVisibleRowPositionInTotalDataSet 屏幕上第一个可见行在总数据集中的位置
     * @param lastVisibleRowPositionInTotalDataSet  屏幕上最后一个可见行在总数据集中的位置
     */
    void onScrollEnd(TableView tableView, int firstVisibleRowPositionInTotalDataSet, int lastVisibleRowPositionInTotalDataSet);

    /**
     * 当Adapter与TableView绑定后触发，适用于首次加载监听
     *
     * @param tableView TableView
     */
    void onTableReady(TableView tableView);
}
