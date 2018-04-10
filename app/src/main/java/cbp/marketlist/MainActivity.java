package cbp.marketlist;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

import cbp.marketlist.data.Key;
import cbp.marketlist.data.MapData;
import cbp.marketlist.key.Field;
import cbp.marketlist.tableview.Cell;
import cbp.marketlist.tableview.HeaderCell;
import cbp.marketlist.tableview.HeaderRowBuilder;
import cbp.marketlist.tableview.Row;
import cbp.marketlist.tableview.RowBuilder;
import cbp.marketlist.tableview.SingleTextCell;
import cbp.marketlist.tableview.TableData;
import cbp.marketlist.tableview.TableDataAdapter;
import cbp.marketlist.tableview.TableView;
import cbp.marketlist.tableview.TableViewStyle;
import cbp.marketlist.tableview.TwoRowTextCell;
import cbp.marketlist.utils.DensityUtil;

/**
 * 此页作为行情列表展示页，数据均为假数据，由于网络层涉及到公司的隐私，不方便开源，有些功能未能很好的展示
 * 后期会不断更新，尽量完善
 */
public class MainActivity extends AppCompatActivity {
    private TableView mTableView;
    private TableDataAdapter mTableDataAdapter;
    private TableData mTableData;
    private List<MapData> mDatas = new LinkedList<>();
    protected HeaderCell.SortType mHeaderSortType = HeaderCell.SortType.NONE;
    protected TableViewStyle mTableViewStyle = new TableViewStyle();
    //------------------Fields-实际应用中不会这么写，此处为了数据演示------------------
    private Key<String> $name = Field.define("$name");//名称
    private Key<String> $code = Field.define("$code");//股票代码
    private Key<String> $lastPrice = Field.define("$lastPrice");//最新
    private Key<String> $changePCT = Field.define("$changePCT");//涨跌幅
    private Key<String> $change = Field.define("$change");//涨跌额
    private Key<String> $volume = Field.define("$volume");//总手
    private Key<String> $amount = Field.define("$amount");//股票金额
    private Key<String> $high = Field.define("$high");//最高
    private Key<String> $low = Field.define("$low");//最低
    private Key<String> $turnoverRate = Field.define("$turnoverRate");//换手
    private Key<String> $PE = Field.define("$PE");//市盈
    private Key<String> $marketValue = Field.define("$marketValue");//总市值
    private Key<String> $circulateValue = Field.define("$circulateValue");//流通市值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initTableView();
    }

    private void initTableView() {
        mTableView = findViewById(R.id.tableView);
        mTableView.setFirstColumnPositionFixed();

        mTableDataAdapter = new TableDataAdapter() {
            @Override
            public Row getHeaderRow() {
                return getHeaderRowForAdapter();
            }

            @Override
            public Row getRow(int position, Row convertRow) {
                MapData rowData = getTableData().getRowData(position);
                String name = rowData.get($name);
                String code = rowData.get($code);
                TwoRowTextCell firstCell = new TwoRowTextCell(name, code, mTableViewStyle.getTwoRowTextCell_StyleRow1(), mTableViewStyle.getTwoRowTextCell_StyleRow2(), Cell.Gravity.LEFT);
                return RowBuilder.makeRow(convertRow)
                        .addCell(firstCell)
                        .addCell(new SingleTextCell(rowData.get($lastPrice), mTableViewStyle.getPriceStyle(10)))
                        .addCell(new SingleTextCell(rowData.get($changePCT), mTableViewStyle.getPriceStyle(10)))
                        .addCell(new SingleTextCell(rowData.get($change), mTableViewStyle.getPriceStyle(10)))
                        .addCell(new SingleTextCell(rowData.get($volume), mTableViewStyle.getDefaultStyle()))
                        .addCell(new SingleTextCell(rowData.get($amount), mTableViewStyle.getDefaultStyle()))
                        .addCell(new SingleTextCell(rowData.get($high), mTableViewStyle.getDefaultStyle()))
                        .addCell(new SingleTextCell(rowData.get($low), mTableViewStyle.getDefaultStyle()))
                        .addCell(new SingleTextCell(rowData.get($turnoverRate), mTableViewStyle.getPriceStyle(1)))
                        .addCell(new SingleTextCell(rowData.get($PE), mTableViewStyle.getPriceStyle(0)))
                        .addCell(new SingleTextCell(rowData.get($marketValue), mTableViewStyle.getDefaultStyle()))
                        .addCell(new SingleTextCell(rowData.get($circulateValue), mTableViewStyle.getDefaultStyle()))
                        .build();
            }
        };
        if (mTableData != null) {
            mTableDataAdapter.replaceTableData(mTableData);
        }
        mTableView.setTableAdapter(mTableDataAdapter);
    }

    private void initData() {
        //以下为测试的假数据，只是为了展示TableView效果
        for (int i = 0; i < 80; i++) {
            MapData mapData = new MapData();
            mapData.set($name, "东方财富");
            mapData.set($code, "300059");
            mapData.set($lastPrice, "11.00");
            mapData.set($changePCT, "10.00%");
            mapData.set($change, "10.00");
            mapData.set($volume, "36.0万");
            mapData.set($amount, "20.00亿");
            mapData.set($high, "11.00");
            mapData.set($low, "10.00");
            mapData.set($turnoverRate, "4.00");
            mapData.set($PE, "30.50");
            mapData.set($marketValue, "750.6亿");
            mapData.set($circulateValue, "668.3亿");
            mDatas.add(mapData);
        }
        mTableData = new TableData(mDatas);
        mTableData.setStartPositionInTotalDataSet(0);
        mTableData.setTotalDataSetCount(80);
    }

    protected Row getHeaderRowForAdapter() {
        final Paint paint = new Paint();
        paint.setTextSize(22);
        float firstCellWidth = paint.measureText("东方财富网") + 10;
        final int cellWidthInDip = DensityUtil.px2dip((getResources().getDisplayMetrics().widthPixels - firstCellWidth) / 3);
        return HeaderRowBuilder
                .makeTitles("名称", "最新", "涨幅", "涨跌", "总手", "金额", "最高", "最低", "换手%", "市盈", "总市值", "流通市值")
                .setSortColumnIndexAndType(0, mHeaderSortType)//设置第一次进来时的排序列index和排序方式，参1：titles数组的角标，参2：排序方式
                .setStyle(mTableViewStyle.getHeaderStyle())
                .setSortable(0, false)
                .setSelectedStyle(0, mTableViewStyle.getHeaderStyle())
                .setSelectedStyle(mTableViewStyle.getHeaderSelectedStyle())
                .setColumnWidthInDip(0, 80)
                .setColumnWidthInDip(cellWidthInDip)
                .setGravity(0, Cell.Gravity.LEFT)
                .setFirstColumnPaddingLeft(10)
                .setCellClickListener(new Cell.OnCellClickListener() {
                    @Override
                    public void onClick(Cell cell, int rowPositionInWindowDataSet, int columnIndex) {
                        //由于公司接口不方便公开，此示例暂不实现点击排序功能
                    }
                })
                .build();
    }
}
