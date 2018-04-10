package cbp.marketlist.tableview;

/**
 * TableView 样式工具类，此示例中的字体大小和各种颜色都是写死的，
 * 在项目中可能涉及换肤，字号-大号、标准等不同需求对样式做管理
 *
 * @author cbp
 */

public class TableViewStyle {
    private Style priceRiseStyle;
    private Style priceFallStyle;
    private Style priceDefaultStyle;
    private HighLightStyle priceRiseHighLightStyle;
    private HighLightStyle priceDefaultHighLightStyle;
    private HighLightStyle priceFallHighLightStyle;
    private HighLightStyle priceTTypeRiseHighLightStyle;
    private HighLightStyle priceTTypeDefaultHighLightStyle;
    private HighLightStyle priceTTypeFallHighLightStyle;
    private Style defaultStyle;
    private Style yellowTextStyle;
    private Style headerStyle;
    private Style headerSelectedStyle;
    private Style headerTransparentStyle;
    private Style headerTransparentSelectedStyle;
    private Style twoRowTextCell_StyleRow1; //双行Cell第1行
    private Style twoRowTextCell_StyleRow2; //双行Cell第2行

    public TableViewStyle() {
        init();
    }

    private void init() {
        int nameTextSize = 16;
        int valueTextSize = 18;

        defaultStyle = new Style(valueTextSize, 0xffffffff, 0xff0d0d0d);
        yellowTextStyle = new Style(valueTextSize, 0xfffff000, 0xff0d0d0d);
        priceRiseStyle = new Style(valueTextSize, 0xffec0000, 0xff0d0d0d);
        priceFallStyle = new Style(valueTextSize, 0xff00d800, 0xff0d0d0d);
        priceDefaultStyle = defaultStyle;
        priceRiseHighLightStyle = new HighLightStyle(priceRiseStyle, 0xFF080F2B);
        priceDefaultHighLightStyle = new HighLightStyle(priceDefaultStyle, 0xFF080F2B);
        priceFallHighLightStyle = new HighLightStyle(priceFallStyle, 0xFF080F2B);

        Style priceTTypeDefaultStyle = new Style(valueTextSize, 0xffffffff, 0xFF260000);
        Style priceTTypeRiseStyle = new Style(valueTextSize, 0xffec0000, 0xFF260000);
        Style priceTTypeFallStyle = new Style(valueTextSize, 0xff00d800, 0xFF260000);
        priceTTypeRiseHighLightStyle = new HighLightStyle(priceTTypeRiseStyle, 0xFF080F2B);
        priceTTypeDefaultHighLightStyle = new HighLightStyle(priceTTypeDefaultStyle, 0xFF080F2B);
        priceTTypeFallHighLightStyle = new HighLightStyle(priceTTypeFallStyle, 0xFF080F2B);

        headerStyle = new Style(16, 0xff8e8e93, 0xff1c1c1c);
        headerSelectedStyle = new Style(16, 0xff3074c2, 0xff1c1c1c);
        headerTransparentStyle = new Style(16, 0x8e8e93, 0xff0d0d0d);
        headerTransparentSelectedStyle = new Style(16, 0xff3074c2, 0xff0d0d0d);
        twoRowTextCell_StyleRow1 = new Style(nameTextSize, 0xffffffff, 0xff0d0d0d);
        twoRowTextCell_StyleRow2 = new Style(16, 0xff8e8e93, 0xff0d0d0d);
    }

    /**
     * 价格型数据，涨跌型样式。例如：最高，最低价
     *
     * @param priceDiff 涨（>0），跌（<0），平（=0）
     * @return Style
     */
    public Style getPriceStyle(int priceDiff) {
        if (priceDiff > 0) {
            return priceRiseStyle;
        } else if (priceDiff == 0) {
            return priceDefaultStyle;
        }
        return priceFallStyle;
    }


    /**
     * 价格型数据，涨跌+高亮型样式。例如：最新，涨跌，涨幅，有高亮效果
     *
     * @param priceDiff 涨（>0），跌（<0），平（=0）
     * @return HighLightStyle
     */
    public Style getPriceHighLightStyle(int priceDiff) {
        if (priceDiff > 0) {
            return priceRiseStyle;
        } else if (priceDiff == 0) {
            return defaultStyle;
        }
        return priceFallStyle;
    }

    /**
     * T型报价style
     *
     * @param priceDiff 涨（>0），跌（<0），平（=0）innerValue 内在价值>0的显示特殊style
     * @return HighLightStyle
     */
    public HighLightStyle getPriceTTypeHighLightStyle(int priceDiff, int innerValue) {
        if (innerValue > 0) {
            if (priceDiff > 0) {
                return priceTTypeRiseHighLightStyle;
            } else if (priceDiff == 0) {
                return priceTTypeDefaultHighLightStyle;
            }
            return priceTTypeFallHighLightStyle;
        }
        if (priceDiff > 0) {
            return priceRiseHighLightStyle;
        } else if (priceDiff == 0) {
            return priceDefaultHighLightStyle;
        }
        return priceFallHighLightStyle;
    }

    /**
     * 默认文字样式
     */
    public Style getDefaultStyle() {
        return defaultStyle;
    }

    /**
     * 黄色文字样式，例如：排名等字段
     */
    public Style getYellowTextStyle() {
        return yellowTextStyle;
    }

    /**
     * 表头样式
     */
    public Style getHeaderStyle() {
        return headerStyle;
    }

    /**
     * 无背景表头
     */
    public Style getHeaderTransparentStyle() {
        return headerTransparentStyle;
    }

    /**
     * 表头选择状态样式
     */
    public Style getHeaderSelectedStyle() {
        return headerSelectedStyle;
    }

    /**
     * 选中状态无背景表头
     */
    public Style getHeaderTransparentSelectedStyle() {
        return headerTransparentSelectedStyle;
    }

    /**
     * 双行Cell第1行 （股票名称）
     */
    public Style getTwoRowTextCell_StyleRow1() {
        return twoRowTextCell_StyleRow1;
    }

    /**
     * 双行Cell第2行 （股票代码）
     */
    public Style getTwoRowTextCell_StyleRow2() {
        return twoRowTextCell_StyleRow2;
    }

    /**
     * 获取现手Style
     *
     * @param lastVolume Int32 [Bit31:bit30]表示内外盘标记，1=买盘，2=卖盘，3=平盘，4=集合竞价。[bit29~bit0] 表示实际现手. 无符号数字
     */
    public Style getLastVolumeStyle(int lastVolume) {
        int type = lastVolume >>> 30;
        if (type == 1) {
            return priceRiseStyle;
        } else if (type == 2) {
            return priceFallStyle;
        } else {
            return priceDefaultStyle;
        }
    }
}
