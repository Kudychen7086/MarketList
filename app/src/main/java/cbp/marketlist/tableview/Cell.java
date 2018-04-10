package cbp.marketlist.tableview;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 表示如何画出单元格内容
 *
 * @author cbp
 */
public abstract class Cell {
    boolean isCellFocused;
    boolean isChanged;
    boolean isSetListener;
    float maxCharCountRatio = 1;//Cell内文字容忍数倍率
    int paddingLeft;

    //内部对齐方式
    public enum Gravity {
        LEFT, //左中
        LEFT_TOP,
        LEFT_BOTTOM,
        CENTER, //正中
        CENTER_TOP,
        CENTER_BOTTOM,
        RIGHT,  //右中
        RIGHT_TOP,
        RIGHT_BOTTOM
    }

    //默认样式
    private Style style = Style.DEFAULT;
    private Object tag; //可以存放临时数据
    private boolean clickable = true; //是否可以点击
    private Gravity gravity = Gravity.RIGHT; //cell中内容的对齐方式
    private OnCellClickListener onCellClickListener;
    private OnCellLongClickListener onCellLongClickListener;

    //画Cell
    public abstract void onDraw(Canvas canvas, Rect rect);

    //做Cell自己的默认行为
    protected void onClick() {
    }

    private void onLongClick() {
    }

    //做Cell自己的默认行为
    protected void onSelect() {
    }

    //点击Cell
    final boolean performClick(int rowPositionInWindowDataSet, int columnIndex) {
        if (clickable) {
            onClick();
            if (onCellClickListener != null) {
                onCellClickListener.onClick(this, rowPositionInWindowDataSet, columnIndex);
                setCellFocused(true);
                return true;
            }
        }
        return false;
    }

    //长按Cell
    final boolean performLongClick(int rowPositionInWindowDataSet, int columnIndex) {
        if (clickable) {
            onLongClick();
            if (onCellLongClickListener != null) {
                onCellLongClickListener.onLongClick(this, rowPositionInWindowDataSet, columnIndex);
                setCellFocused(true);
                return true;
            }
        }
        return false;
    }

    void setCellFocused(boolean isCellFocused) {
        this.isCellFocused = isCellFocused;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        if (style != null) {
            this.style = style;
        }
    }

    void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    boolean getClickable() {
        return clickable;
    }

    Gravity getGravity() {
        return gravity;
    }

    void setGravity(Gravity gravity) {
        if (gravity != null) {
            this.gravity = gravity;
        }
    }

    void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    int getPaddingLeft() {
        return this.paddingLeft;
    }

    void setMaxCharCountRatio(float maxCharCountRatio) {
        this.maxCharCountRatio = maxCharCountRatio;
    }

    public void setOnCellClickListener(OnCellClickListener onCellClickListener) {
        if (clickable) {
            isSetListener = true;
            this.onCellClickListener = onCellClickListener;
        }
    }

    void setOnCellLongClickListener(OnCellLongClickListener onCellLongClickListener) {
        if (clickable) {
            this.onCellLongClickListener = onCellLongClickListener;
        }
    }

    //单元格点击
    public interface OnCellClickListener {
        void onClick(Cell cell, int rowPositionInWindowDataSet, int columnIndex);
    }

    //单元格长按
    interface OnCellLongClickListener {
        void onLongClick(Cell cell, int rowPositionInWindowDataSet, int columnIndex);
    }
}