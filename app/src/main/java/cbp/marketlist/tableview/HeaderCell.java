package cbp.marketlist.tableview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;

import cbp.marketlist.utils.DensityUtil;

/**
 * 表头单元格
 *
 * @author cbp
 */
public class HeaderCell extends Cell {
    private Paint paint;

    //排序类型
    public enum SortType {
        NONE,//无序
        ASC,//升序
        DESC//降序
    }

    private String text;
    private int columnWidthInDip = 92; //决定列宽
    private int paddingLeft = 10; //文字左边距(默认为TableView滚动条宽度10像素,只在gravity为LEFT起作用)
    private int paddingRight = DensityUtil.dip2px(2);//右边距
    private Style selectedStyle = new Style(16, 0xFF3381E3, 0xFF1E1E1E); //选中状态样式;
    private boolean sortable = true; //是否支持排序
    private SortType sortType = SortType.NONE;//当前排序状态

    HeaderCell() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public String getText() {
        return text;
    }

    /**
     * 设置文字左边距
     *
     * @param paddingLeft 单位pixel
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            text = "--";
        }
        this.text = text;
    }

    int getColumnWidthInDip() {
        return columnWidthInDip;
    }

    void setColumnWidthInDip(int columnWidthInDip) {
        this.columnWidthInDip = columnWidthInDip;
    }

    private Style getSelectedStyle() {
        if (selectedStyle == null) {
            return super.getStyle();
        }
        return selectedStyle;
    }

    void setSelectedStyle(Style selectedStyle) {
        this.selectedStyle = selectedStyle;
    }

    boolean isSortable() {
        return sortable;
    }

    void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    void clearSortType() {
        setSortType(SortType.NONE);
    }

    //如果是可排序的，自己控制内部状态
    protected void onClick() {
        if (sortable) {
            switch (sortType) {
                case NONE:
                case ASC:
                    sortType = SortType.DESC;
                    break;
                case DESC:
                    sortType = SortType.ASC;
                    break;
            }
        }
    }

    public void onDraw(Canvas canvas, Rect rect) {
        SortType sortType = getSortType();
        int textSizeSp;
        int textColor;
        int backgroundColor;
        if (sortType == SortType.NONE) {
            Style style = getStyle();
            textSizeSp = style.getTextSizeSp();
            textColor = style.getTextColor();
            backgroundColor = style.getBackgroundColor();
        } else {
            selectedStyle = getSelectedStyle();
            textSizeSp = selectedStyle.getTextSizeSp();
            textColor = selectedStyle.getTextColor();
            backgroundColor = selectedStyle.getBackgroundColor();
        }
        paint.setTextSize(DensityUtil.sp2px(textSizeSp));
        paint.setColor(textColor);
        float ascent = paint.getFontMetrics().ascent;
        float descent = paint.getFontMetrics().descent;
        if (backgroundColor != 0) {
            canvas.drawColor(backgroundColor);
        }

        int left = rect.left;
        int top = (int) (rect.top - ascent);
        int right = (int) (rect.right - paint.measureText(getText()));
        int bottom = (int) (rect.bottom - descent);

        // TODO: 2017/4/18 排序箭头这里只画了Gravity为Right的情况
        switch (getGravity()) {
            case LEFT:
                canvas.drawText(getText(), left + paddingLeft, (top + bottom) / 2, paint);
                switch (this.sortType) {
                    case NONE:
                        break;
                    case ASC:
                        Path pathASC = new Path();
                        pathASC.moveTo(left + paint.measureText(getText()) + DensityUtil.sp2px(1), (top + bottom) / 2 - DensityUtil.sp2px(7));
                        pathASC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(8), (top + bottom) / 2 - DensityUtil.sp2px(7));
                        pathASC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), (top + bottom) / 2 - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect(left + paint.measureText(getText()) + DensityUtil.sp2px(3), (top + bottom) / 2 - DensityUtil.sp2px(7), left + paint.measureText(getText()) + DensityUtil.sp2px(6), (top + bottom) / 2 + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawRect(left + paint.measureText(getText()) + DensityUtil.sp2px(3), (top + bottom) / 2 - DensityUtil.sp2px(12), left + paint.measureText(getText()) + DensityUtil.sp2px(6), (top + bottom) / 2 - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo(left + paint.measureText(getText()) + DensityUtil.sp2px(1), (top + bottom) / 2 - DensityUtil.sp2px(4));
                        pathDESC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(8), (top + bottom) / 2 - DensityUtil.sp2px(4));
                        pathDESC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), (top + bottom) / 2 + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case LEFT_TOP:
                canvas.drawText(getText(), left + paddingLeft, top, paint);
                switch (this.sortType) {
                    case NONE:
                        break;
                    case ASC:
                        Path pathASC = new Path();
                        pathASC.moveTo(left + paint.measureText(getText()) + DensityUtil.sp2px(1), top - DensityUtil.sp2px(7));
                        pathASC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(8), top - DensityUtil.sp2px(7));
                        pathASC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), top - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect(left + paint.measureText(getText()) + DensityUtil.sp2px(3), top - DensityUtil.sp2px(7), left + paint.measureText(getText()) + DensityUtil.sp2px(6), top + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawRect(left + paint.measureText(getText()) + DensityUtil.sp2px(3), top - DensityUtil.sp2px(12), left + paint.measureText(getText()) + DensityUtil.sp2px(6), top - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo(left + paint.measureText(getText()) + DensityUtil.sp2px(1), top - DensityUtil.sp2px(4));
                        pathDESC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(8), top - DensityUtil.sp2px(4));
                        pathDESC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), top + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case LEFT_BOTTOM:
                canvas.drawText(getText(), left + paddingLeft, bottom, paint);
                switch (this.sortType) {
                    case NONE:
                        break;
                    case ASC:
                        Path pathASC = new Path();
                        pathASC.moveTo(left + paint.measureText(getText()) + DensityUtil.sp2px(1), bottom - DensityUtil.sp2px(7));
                        pathASC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(8), bottom - DensityUtil.sp2px(7));
                        pathASC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), bottom - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect(left + paint.measureText(getText()) + DensityUtil.sp2px(3), bottom - DensityUtil.sp2px(7), left + paint.measureText(getText()) + DensityUtil.sp2px(6), bottom + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawRect(left + paint.measureText(getText()) + DensityUtil.sp2px(3), bottom - DensityUtil.sp2px(12), left + paint.measureText(getText()) + DensityUtil.sp2px(6), bottom - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo(left + paint.measureText(getText()) + DensityUtil.sp2px(1), bottom - DensityUtil.sp2px(4));
                        pathDESC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(8), bottom - DensityUtil.sp2px(4));
                        pathDESC.lineTo(left + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), bottom + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case CENTER:
                canvas.drawText(getText(), (left + right) / 2, (top + bottom) / 2, paint);
                switch (this.sortType) {
                    case NONE:
                        break;
                    case ASC:
                        Path pathASC = new Path();
                        pathASC.moveTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(1), (top + bottom) / 2 - DensityUtil.sp2px(7));
                        pathASC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(8), (top + bottom) / 2 - DensityUtil.sp2px(7));
                        pathASC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), (top + bottom) / 2 - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(3), (top + bottom) / 2 - DensityUtil.sp2px(7), (left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(6), (top + bottom) / 2 + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawRect((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(3), (top + bottom) / 2 - DensityUtil.sp2px(12), (left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(6), (top + bottom) / 2 - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(1), (top + bottom) / 2 - DensityUtil.sp2px(4));
                        pathDESC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(8), (top + bottom) / 2 - DensityUtil.sp2px(4));
                        pathDESC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), (top + bottom) / 2 + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case CENTER_TOP:
                canvas.drawText(getText(), (left + right) / 2, top, paint);
                switch (this.sortType) {
                    case NONE:
                        break;
                    case ASC:
                        Path pathASC = new Path();
                        pathASC.moveTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(1), top - DensityUtil.sp2px(7));
                        pathASC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(8), top - DensityUtil.sp2px(7));
                        pathASC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), top - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(3), top - DensityUtil.sp2px(7), (left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(6), top + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawRect((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(3), top - DensityUtil.sp2px(12), (left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(6), top - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(1), top - DensityUtil.sp2px(4));
                        pathDESC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(8), top - DensityUtil.sp2px(4));
                        pathDESC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), top + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case CENTER_BOTTOM:
                canvas.drawText(getText(), (left + right) / 2, bottom, paint);
                switch (this.sortType) {
                    case NONE:
                        break;
                    case ASC:
                        Path pathASC = new Path();
                        pathASC.moveTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(1), bottom - DensityUtil.sp2px(7));
                        pathASC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(8), bottom - DensityUtil.sp2px(7));
                        pathASC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), bottom - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(3), bottom - DensityUtil.sp2px(7), (left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(6), bottom + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawRect((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(3), bottom - DensityUtil.sp2px(12), (left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(6), bottom - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(1), bottom - DensityUtil.sp2px(4));
                        pathDESC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(8), bottom - DensityUtil.sp2px(4));
                        pathDESC.lineTo((left + right) / 2 + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), bottom + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case RIGHT:
                switch (this.sortType) {
                    case NONE:
                        canvas.drawText(getText(), right, (top + bottom) / 2, paint);
                        break;
                    case ASC:
                        canvas.drawText(getText(), right - DensityUtil.dip2px(9), (top + bottom) / 2, paint);
                        Path pathASC = new Path();
                        pathASC.moveTo(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(1), (top + bottom) / 2 - DensityUtil.dip2px(7));
                        pathASC.lineTo(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(4.5f), (top + bottom) / 2 - DensityUtil.dip2px(12));
                        pathASC.lineTo(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(8), (top + bottom) / 2 - DensityUtil.dip2px(7));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(3), (top + bottom) / 2 - DensityUtil.dip2px(7), right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(6), (top + bottom) / 2 + DensityUtil.dip2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawText(getText(), right - DensityUtil.dip2px(9), (top + bottom) / 2, paint);
                        canvas.drawRect(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(3), (top + bottom) / 2 - DensityUtil.dip2px(12), right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(6), (top + bottom) / 2 - DensityUtil.dip2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(1), (top + bottom) / 2 - DensityUtil.dip2px(4));
                        pathDESC.lineTo(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(8), (top + bottom) / 2 - DensityUtil.dip2px(4));
                        pathDESC.lineTo(right - DensityUtil.dip2px(9) + paint.measureText(getText()) + DensityUtil.dip2px(4.5f), (top + bottom) / 2 + DensityUtil.dip2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case RIGHT_TOP:
                switch (this.sortType) {
                    case NONE:
                        canvas.drawText(getText(), right, top, paint);
                        break;
                    case ASC:
                        canvas.drawText(getText(), right - DensityUtil.sp2px(9), top, paint);
                        Path pathASC = new Path();
                        pathASC.moveTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(1), top - DensityUtil.sp2px(7));
                        pathASC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(8), top - DensityUtil.sp2px(7));
                        pathASC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), top - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(3), top - DensityUtil.sp2px(7), right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(6), top + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawText(getText(), right - DensityUtil.sp2px(9), top, paint);
                        canvas.drawRect(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(3), top - DensityUtil.sp2px(12), right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(6), top - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(1), top - DensityUtil.sp2px(4));
                        pathDESC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(8), top - DensityUtil.sp2px(4));
                        pathDESC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), top + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
            case RIGHT_BOTTOM:
                switch (this.sortType) {
                    case NONE:
                        canvas.drawText(getText(), right, bottom, paint);
                        break;
                    case ASC:
                        canvas.drawText(getText(), right - DensityUtil.sp2px(9), bottom, paint);
                        Path pathASC = new Path();
                        pathASC.moveTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(1), bottom - DensityUtil.sp2px(7));
                        pathASC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(8), bottom - DensityUtil.sp2px(7));
                        pathASC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), bottom - DensityUtil.sp2px(12));
                        pathASC.close();
                        canvas.drawPath(pathASC, paint);
                        canvas.drawRect(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(3), bottom - DensityUtil.sp2px(7), right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(6), bottom + DensityUtil.sp2px(1), paint);
                        break;
                    case DESC:
                        canvas.drawText(getText(), right - DensityUtil.sp2px(9), bottom, paint);
                        canvas.drawRect(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(3), bottom - DensityUtil.sp2px(12), right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(6), bottom - DensityUtil.sp2px(4), paint);
                        Path pathDESC = new Path();
                        pathDESC.moveTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(1), bottom - DensityUtil.sp2px(4));
                        pathDESC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(8), bottom - DensityUtil.sp2px(4));
                        pathDESC.lineTo(right - DensityUtil.sp2px(9) + paint.measureText(getText()) + DensityUtil.sp2px(4.5f), bottom + DensityUtil.sp2px(1));
                        pathDESC.close();
                        canvas.drawPath(pathDESC, paint);
                        break;
                }
                break;
        }
    }
}
