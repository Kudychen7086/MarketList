package cbp.marketlist.tableview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import cbp.marketlist.utils.DensityUtil;

/**
 * 自定义view TableView
 *
 * @author cbp
 */
public class TableView extends View {
    private int mMeasuredWidth, mMeasuredHeight;
    public static final int headerHeight = DensityUtil.dip2px(30);
    public static final int rowHeight = DensityUtil.dip2px(48);
    private boolean mIsFirstRowPositionFixed;
    private boolean mIsFirstColumnPositionFixed;
    private int mOffsetX;//用于记录左右滑动时x轴的偏移量
    private int mOffsetY;//用于记录上下滑动时y轴的偏移量
    private int mPreOffsetX;
    private int mPreOffsetY;
    private State mState = State.IDLE;//记录手势状态
    private State mPreState = State.IDLE;//记录上一次手势状态

    private VelocityTracker mVelocityTracker = null;
    private ValueAnimator mValueAnimator;
    private int mOverScrollX;
    private int mOverScrollY;
    private int mEfficientX;
    private int mEfficientY;
    private int mRowStartPositionInTotalDataSet;//本地Row集合在总数据集的起始坐标
    private int mWindowStartPositionInTotalDataSet;
    private Row mHeaderRow;
    private Row mFirstRow;
    private Paint mPaint;
    private Paint mLinePaint;
    private boolean mIsEnoughDataCount = true;
    private Handler mHandler = new Handler();
    private boolean mNeedRefreshRows;
    private boolean mIsNeedRefreshHeaders;
    private boolean mIsSingleTapUp;
    private boolean mIsShowPress;
    private boolean mIsLongPress;
    private int mTouchSlop;
    private Rect rect = new Rect();
    private RectF rectF = new RectF();
    private DecimalFormat df = new DecimalFormat("#.######");//用于计算滚动条位置
    private Style nonExistRowStyle = new Style(16, 0xFFFFFFFF, 0xFF0D0D0D);

    public enum State {
        IDLE, TOLEFTORRIGHT, TOUPORDOWN//默认，左右滑动，上下滑动
    }

    private GestureDetector mGestureDetector;
    private TableDataAdapter mTableAdapter;
    private TableListener mTableListener;
    private HorizontalScrollListener mHorizontalScrollListener;
    private List<Row> mRows = new LinkedList<>();
    private List<Integer> mColumnEndWidth = new LinkedList<>();
    private int[] mFocusedRowAndColumn;//记录点击或按压的cell相对于总数据集所在的行和列
    private boolean isShowPosition;//是否显示底部位置提示框
    private boolean mScrollbarEnable = false;//是否显示滚动条
    private boolean isFocusedCellWithListener;//当前cell是否有设置listener，用于判断背景变色是整行还是单个cell

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(DensityUtil.sp2px(10));
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(1);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onShowPress(MotionEvent e) {
                mIsShowPress = true;
                notifyFocused();
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                mIsSingleTapUp = true;
                if (!mIsShowPress) {
                    notifyFocused();
                }
                if (startY > headerHeight) {
                    int rowPositionInTotalDataSet = 0;
                    if (mIsFirstRowPositionFixed) {
                        if (startY > headerHeight + rowHeight) {
                            rowPositionInTotalDataSet = (startY - headerHeight - mOffsetY) / rowHeight;
                        }
                    } else {
                        rowPositionInTotalDataSet = (startY - headerHeight - mOffsetY) / rowHeight;
                    }
                    performCellClick(rowPositionInTotalDataSet);
                } else {
                    //处理表头的点击事件
                    int columnIndex = 0;
                    if (mIsFirstColumnPositionFixed) {
                        if (startX > fixedLeftPart) {
                            columnIndex = getColumnIndex(columnIndex);
                        }
                    } else {
                        columnIndex = getColumnIndex(columnIndex);
                    }
                    if (mHeaderRow != null) {
                        List<Cell> headerCells = mHeaderRow.getCells();
                        HeaderCell headerCell = (HeaderCell) headerCells.get(columnIndex);
                        for (int i = 0; i < headerCells.size(); i++) {
                            if (i != columnIndex) {
                                HeaderCell cell = (HeaderCell) headerCells.get(i);
                                if (headerCell.getClickable() && cell.isSortable()) {
                                    cell.clearSortType();
                                }
                            }
                        }
                        if (headerCell.getClickable()) {
                            resetSortState();
                            headerCell.performClick(Integer.MIN_VALUE, columnIndex);
                        }
                    }
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (mTableAdapter == null || mTableAdapter.getTotalDataCount() == 0) {
                    return;
                }
                mIsLongPress = true;
                if (startY > headerHeight) {
                    int rowPositionInTotalDataSet = 0;
                    if (mIsFirstRowPositionFixed) {
                        if (startY > headerHeight + rowHeight) {
                            rowPositionInTotalDataSet = (startY - headerHeight - mOffsetY) / rowHeight;
                        }
                    } else {
                        rowPositionInTotalDataSet = (startY - headerHeight - mOffsetY) / rowHeight;
                    }
                    int columnIndex = 0;
                    if (mIsFirstColumnPositionFixed) {
                        if (startX > fixedLeftPart) {
                            columnIndex = getColumnIndex(columnIndex);
                        }
                    } else {
                        columnIndex = getColumnIndex(columnIndex);
                    }
                    List<Cell> cells = null;
                    if (mIsFirstRowPositionFixed) {
                        if (mFirstRow != null) {
                            cells = mFirstRow.getCells();
                        }
                    } else {
                        int position = rowPositionInTotalDataSet - mRowStartPositionInTotalDataSet;
                        if (position >= 0 && position < mRows.size()) {
                            Row pressedRow = mRows.get(position);
                            cells = pressedRow.getCells();
                        }
                    }
                    if (cells != null) {
                        Cell cell = cells.get(columnIndex);
                        boolean isPermLongClick = cell.performLongClick(rowPositionInTotalDataSet - mWindowStartPositionInTotalDataSet, columnIndex);
                        if (!isPermLongClick) {
                            if (onTableItemLongClickListener != null) {
                                onTableItemLongClickListener.onClick(rowPositionInTotalDataSet - mWindowStartPositionInTotalDataSet);
                            }
                        }
                    }
                }
            }
        });
    }

    private void notifyFocused() {
        //记录触碰的cell所在总数据集的行和列
        if (startY > headerHeight) {
            int rowPositionInTotalDataSet = 0;
            if (mIsFirstRowPositionFixed) {
                if (startY > headerHeight + rowHeight) {
                    rowPositionInTotalDataSet = (startY - headerHeight - mOffsetY) / rowHeight;
                }
            } else {
                rowPositionInTotalDataSet = (startY - headerHeight - mOffsetY) / rowHeight;
            }
            int columnIndex = 0;
            if (mIsFirstColumnPositionFixed) {
                if (startX > fixedLeftPart) {
                    columnIndex = getColumnIndex(columnIndex);
                }
            } else {
                columnIndex = getColumnIndex(columnIndex);
            }
            mFocusedRowAndColumn = new int[]{rowPositionInTotalDataSet, columnIndex};
            postInvalidate();
        }
    }

    private void resetFocused() {
        if (mFocusedRowAndColumn != null) {
            mFocusedRowAndColumn = null;
        }
        if (mIsShowPress) {
            mIsShowPress = false;
        }
        if (mIsSingleTapUp) {
            mIsSingleTapUp = false;
        }
        if (mIsLongPress) {
            mIsLongPress = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
    }

    private int leftPartWidth;
    private int fixedLeftPart;
    private int maxHeadersWidth;
    private boolean isNeedHeadersWidth = true;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTableAdapter != null) {
            mWindowStartPositionInTotalDataSet = mTableAdapter.getWindowStartPositionInTotalDataSet();
            int totalDataCount = mTableAdapter.getTotalDataCount();
            int windowDataCount = mTableAdapter.getWindowDataCount();
            mIsEnoughDataCount = totalDataCount > (mMeasuredHeight - headerHeight) / rowHeight;
            if (mHeaderRow == null || mIsNeedRefreshHeaders) {
                mIsNeedRefreshHeaders = false;
                mHeaderRow = mTableAdapter.getHeaderRow();
            }
            if (mHeaderRow == null) {
                throw new RuntimeException("the return type of getHeaderRow() cannot be null");
            }
            List<Cell> headerCells = mHeaderRow.getCells();
            if (isNeedHeadersWidth) {
                maxHeadersWidth = 0;
                mColumnEndWidth.clear();
                for (int i = 0; i < headerCells.size(); i++) {
                    HeaderCell cell = (HeaderCell) headerCells.get(i);
                    if (i == 0) {
                        fixedLeftPart = DensityUtil.dip2px(cell.getColumnWidthInDip());
                    }
                    maxHeadersWidth += DensityUtil.dip2px(cell.getColumnWidthInDip());
                    mColumnEndWidth.add(maxHeadersWidth);
                }
                mEfficientX = mIsFirstColumnPositionFixed ? fixedLeftPart : 0;
                mEfficientY = mIsFirstRowPositionFixed ? headerHeight + rowHeight : headerHeight;
                isNeedHeadersWidth = false;
            }

            //填充Rows集合
            mRowStartPositionInTotalDataSet = Math.abs(mOffsetY) / rowHeight;
            final int endRowPositionInTotalDataSet = (Math.abs(mOffsetY) + (mMeasuredHeight - headerHeight)) % rowHeight == 0 ?
                    (Math.abs(mOffsetY) + (mMeasuredHeight - headerHeight)) / rowHeight - 1 : (Math.abs(mOffsetY) + (mMeasuredHeight - headerHeight)) / rowHeight;
            if (mRows.size() == 0 || (!mIsEnoughDataCount && mRows.size() != totalDataCount)) {
                if (mIsEnoughDataCount) {
                    for (int i = mRowStartPositionInTotalDataSet; i <= endRowPositionInTotalDataSet; i++) {
                        if (i < totalDataCount) {
                            Row row;
                            int positionInWindowDataSet = i - mWindowStartPositionInTotalDataSet;
                            if (positionInWindowDataSet < 0 || positionInWindowDataSet >= windowDataCount) {
                                row = mTableAdapter.getNonExistRow(nonExistRowStyle, null);
                            } else {
                                row = mTableAdapter.getRowInternal(positionInWindowDataSet, null);
                            }
                            if (row != null) {
                                row.rowPositionInTotalDataSet = i;
                                mRows.add(row);
                                drawRow(canvas, row, i);
                            }
                        }
                    }
                } else {
                    if (mRows.size() > 0) mRows.clear();
                    for (int i = 0; i < totalDataCount; i++) {
                        Row row = mTableAdapter.getRowInternal(i - mWindowStartPositionInTotalDataSet, null);
                        row.rowPositionInTotalDataSet = i;
                        mRows.add(row);
                        drawRow(canvas, row, i);
                    }
                }
            } else {
                //重新获取所有数据
                if (mNeedRefreshRows) {
                    mNeedRefreshRows = false;
                    if (mRows.size() > totalDataCount) {
                        mRows = mRows.subList(0, totalDataCount);
                    }
                    int size = mRows.size();
                    for (int i = 0; i < size; i++) {
                        Row newRow;
                        Row oldRow = mRows.get(i);
                        int positionInWindowDataSet = oldRow.rowPositionInTotalDataSet - mWindowStartPositionInTotalDataSet;
                        if (positionInWindowDataSet < 0 || positionInWindowDataSet >= windowDataCount) {
                            newRow = mTableAdapter.getNonExistRow(nonExistRowStyle, oldRow);
                        } else {
                            newRow = mTableAdapter.getRowInternal(positionInWindowDataSet, oldRow);
                        }
                        if (newRow != null) {
                            newRow.rowPositionInTotalDataSet = oldRow.rowPositionInTotalDataSet;
                            mRows.remove(oldRow);
                            mRows.add(i, newRow);
                        }
                    }
                }
                if (mRows.isEmpty()) return;
                while (mRows.get(0).rowPositionInTotalDataSet > mRowStartPositionInTotalDataSet) {
                    if (mRows.get(0).rowPositionInTotalDataSet <= endRowPositionInTotalDataSet) {
                        int positionInWindowDataSet = mRows.get(0).rowPositionInTotalDataSet - 1 - mWindowStartPositionInTotalDataSet;
                        if (mRows.get(mRows.size() - 1).rowPositionInTotalDataSet > endRowPositionInTotalDataSet) {
                            Row row;
                            if (positionInWindowDataSet < 0 || positionInWindowDataSet >= windowDataCount) {
                                row = mTableAdapter.getNonExistRow(nonExistRowStyle, mRows.get(mRows.size() - 1));
                            } else {
                                row = mTableAdapter.getRowInternal(positionInWindowDataSet, mRows.get(mRows.size() - 1));
                            }
                            if (row != null) {
                                row.rowPositionInTotalDataSet = mRows.get(0).rowPositionInTotalDataSet - 1;
                                mRows.remove(mRows.size() - 1);
                                mRows.add(0, row);
                            }
                        } else {
                            Row row;
                            if (positionInWindowDataSet < 0 || positionInWindowDataSet >= windowDataCount) {
                                row = mTableAdapter.getNonExistRow(nonExistRowStyle, null);
                            } else {
                                row = mTableAdapter.getRowInternal(positionInWindowDataSet, null);
                            }
                            if (row != null) {
                                row.rowPositionInTotalDataSet = mRows.get(0).rowPositionInTotalDataSet - 1;
                                mRows.add(0, row);
                            }
                        }
                    } else {
                        //Rows全部在屏幕下方
                        for (int i = 0; i < mRows.size(); i++) {
                            mRows.get(i).rowPositionInTotalDataSet = mRowStartPositionInTotalDataSet + i;
                        }
                    }
                }
                while (mIsEnoughDataCount && mRows.get(mRows.size() - 1).rowPositionInTotalDataSet < endRowPositionInTotalDataSet) {
                    if (mRows.get(mRows.size() - 1).rowPositionInTotalDataSet >= mRowStartPositionInTotalDataSet) {
                        int positionInWindowDataSet = mRows.get(mRows.size() - 1).rowPositionInTotalDataSet + 1 - mWindowStartPositionInTotalDataSet;
                        if (mRows.get(0).rowPositionInTotalDataSet < mRowStartPositionInTotalDataSet) {
                            Row row;
                            if (positionInWindowDataSet < 0 || positionInWindowDataSet >= windowDataCount) {
                                row = mTableAdapter.getNonExistRow(nonExistRowStyle, mRows.get(0));
                            } else {
                                row = mTableAdapter.getRowInternal(positionInWindowDataSet, mRows.get(0));
                            }
                            if (row != null) {
                                row.rowPositionInTotalDataSet = mRows.get(mRows.size() - 1).rowPositionInTotalDataSet + 1;
                                mRows.remove(0);
                                mRows.add(row);
                            }
                        } else {
                            Row row;
                            if (positionInWindowDataSet < 0 || positionInWindowDataSet >= windowDataCount) {
                                row = mTableAdapter.getNonExistRow(nonExistRowStyle, null);
                            } else {
                                row = mTableAdapter.getRowInternal(positionInWindowDataSet, null);
                            }
                            if (row != null) {
                                row.rowPositionInTotalDataSet = mRows.get(mRows.size() - 1).rowPositionInTotalDataSet + 1;
                                mRows.add(row);
                            }
                        }
                    } else {
                        //Rows全部在屏幕上方
                        for (int i = 0; i < mRows.size(); i++) {
                            mRows.get(i).rowPositionInTotalDataSet = mRowStartPositionInTotalDataSet + i;
                        }
                    }
                }
            }
            //画Rows
            mLinePaint.setColor(0xFF262626);
            int rowSize = mRows.size();
            if (mIsFirstRowPositionFixed && rowSize != 0) {
                canvas.save();
                canvas.clipRect(-mOffsetX, headerHeight + rowHeight - mOffsetY, mMeasuredWidth - mOffsetX, mMeasuredHeight - mOffsetY);
                for (int i = 1; i < mRows.size(); i++) {
                    if (rowHeight * (mRows.get(0).rowPositionInTotalDataSet + i) >= Math.abs(mOffsetY)) {
                        drawRow(canvas, mRows.get(i), mRows.get(0).rowPositionInTotalDataSet + i);
                    }
                }
                canvas.restore();
                //画第一行
                if (mRows.get(rowSize - 1).rowPositionInTotalDataSet > endRowPositionInTotalDataSet) {
                    mFirstRow = mTableAdapter.getRowInternal(0, mRows.get(rowSize - 1));
                    mFirstRow.rowPositionInTotalDataSet = 0;
                    mRows.remove(rowSize - 1);
                } else {
                    mFirstRow = mTableAdapter.getRowInternal(0, null);
                    mFirstRow.rowPositionInTotalDataSet = 0;
                }
                if (mFirstRow.backgroundColor != 0) {
                    canvas.save();
                    rect.left = -mOffsetX;
                    rect.top = headerHeight - mOffsetY;
                    rect.right = mMeasuredWidth - mOffsetX;
                    rect.bottom = headerHeight + rowHeight - mOffsetY;
                    canvas.clipRect(rect);
                    canvas.drawColor(mFirstRow.backgroundColor);
                    canvas.restore();
                }
                List<Cell> firstRowCells = mFirstRow.getCells();
                int size = Math.min(firstRowCells.size(), headerCells.size());
                if (mIsFirstColumnPositionFixed) {
                    HeaderCell firstHeaderCell = (HeaderCell) headerCells.get(0);
                    Cell firstCell = firstRowCells.get(0);
                    if (mFocusedRowAndColumn != null) {
                        isFocusedCellWithListener = firstRowCells.get(mFocusedRowAndColumn[1]).isSetListener;
                    }
                    for (int i = 0; i < size; i++) {
                        HeaderCell headerCell = (HeaderCell) headerCells.get(i);
                        Cell cell = firstRowCells.get(i);
                        if (mFocusedRowAndColumn != null) {
                            if (isFocusedCellWithListener) {
                                if (0 == mFocusedRowAndColumn[0] && i == mFocusedRowAndColumn[1]) {
                                    cell.setCellFocused(true);
                                } else {
                                    cell.setCellFocused(false);
                                }
                            } else {
                                if (0 == mFocusedRowAndColumn[0] && onTableItemClickListener != null) {
                                    cell.setCellFocused(true);
                                } else {
                                    cell.setCellFocused(false);
                                }
                            }
                        } else {
                            cell.setCellFocused(false);
                        }
                        if (i == 0) {
                            leftPartWidth = DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                        } else {
                            int left = Math.max(leftPartWidth, DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX);
                            rect.left = leftPartWidth;
                            rect.top = headerHeight - mOffsetY;
                            rect.right = leftPartWidth + DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                            rect.bottom = headerHeight + rowHeight - mOffsetY;
                            if (rect.left < mMeasuredWidth - mOffsetX && rect.right > DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX) {//画屏幕内的cell
                                canvas.save();
                                canvas.clipRect(left, rect.top, rect.right, rect.bottom);
                                drawCell(canvas, cell, rect);
                                canvas.restore();
                            }
                            leftPartWidth += DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                        }
                    }
                    // 画第一行第一列
                    rect.left = -mOffsetX;
                    rect.top = headerHeight - mOffsetY;
                    rect.right = DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX;
                    rect.bottom = headerHeight + rowHeight - mOffsetY;
                    drawCell(canvas, firstCell, rect);
                } else {
                    if (mFocusedRowAndColumn != null) {
                        isFocusedCellWithListener = firstRowCells.get(mFocusedRowAndColumn[1]).isSetListener;
                    }
                    for (int i = 0; i < size; i++) {
                        HeaderCell headerCell = (HeaderCell) headerCells.get(i);
                        Cell cell = firstRowCells.get(i);
                        if (mFocusedRowAndColumn != null) {
                            if (isFocusedCellWithListener) {
                                if (0 == mFocusedRowAndColumn[0] && i == mFocusedRowAndColumn[1]) {
                                    cell.setCellFocused(true);
                                } else {
                                    cell.setCellFocused(false);
                                }
                            } else {
                                if (0 == mFocusedRowAndColumn[0] && onTableItemClickListener != null) {
                                    cell.setCellFocused(true);
                                } else {
                                    cell.setCellFocused(false);
                                }
                            }
                        } else {
                            cell.setCellFocused(false);
                        }
                        if (i == 0) {
                            leftPartWidth = DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                            rect.left = 0;
                            rect.top = headerHeight - mOffsetY;
                            rect.right = leftPartWidth;
                            rect.bottom = headerHeight + rowHeight - mOffsetY;
                            if (rect.left < mMeasuredWidth - mOffsetX && rect.right > -mOffsetX) {//画屏幕内的cell
                                drawCell(canvas, cell, rect);
                            }
                        } else {
                            rect.left = leftPartWidth;
                            rect.top = headerHeight - mOffsetY;
                            rect.right = leftPartWidth + DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                            rect.bottom = headerHeight + rowHeight - mOffsetY;
                            if (rect.left < mMeasuredWidth - mOffsetX && rect.right > -mOffsetX) {//画屏幕内的cell
                                drawCell(canvas, cell, rect);
                            }
                            leftPartWidth += DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                        }
                    }
                }
                canvas.drawLine(-mOffsetX, headerHeight + rowHeight - mOffsetY, mMeasuredWidth - mOffsetX, headerHeight + rowHeight - mOffsetY, mLinePaint);//第一行的分割线
            } else {
                for (int i = 0; i < mRows.size(); i++) {
                    drawRow(canvas, mRows.get(i), mRows.get(0).rowPositionInTotalDataSet + i);
                }
            }

            //画headers
            if (mIsFirstColumnPositionFixed) {
                HeaderCell firstHeaderCell = (HeaderCell) headerCells.get(0);
                for (int i = 0; i < headerCells.size(); i++) {
                    HeaderCell headerCell = (HeaderCell) headerCells.get(i);
                    if (i == 0) {
                        leftPartWidth = DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                    } else {
                        rect.left = leftPartWidth;
                        rect.top = -mOffsetY;
                        rect.right = leftPartWidth + DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                        rect.bottom = headerHeight - mOffsetY;
                        if (rect.left < mMeasuredWidth - mOffsetX && rect.right > DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX) {//画屏幕内的cell
                            drawCell(canvas, headerCell, rect);
                        }
                        leftPartWidth += DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                    }
                }
                rect.left = -mOffsetX;
                rect.top = -mOffsetY;
                rect.right = DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX;
                rect.bottom = headerHeight - mOffsetY;
                drawCell(canvas, firstHeaderCell, rect);
            } else {
                for (int i = 0; i < headerCells.size(); i++) {
                    HeaderCell headerCell = (HeaderCell) headerCells.get(i);
                    if (i == 0) {
                        leftPartWidth = DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                        rect.left = 0;
                        rect.top = -mOffsetY;
                        rect.right = leftPartWidth;
                        rect.bottom = -mOffsetY + headerHeight;
                        if (rect.left < mMeasuredWidth - mOffsetX && rect.right > -mOffsetX) {//画屏幕内的cell
                            drawCell(canvas, headerCell, rect);
                        }
                    } else {
                        rect.left = leftPartWidth;
                        rect.top = -mOffsetY;
                        rect.right = leftPartWidth + DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                        rect.bottom = -mOffsetY + headerHeight;
                        if (rect.left < mMeasuredWidth - mOffsetX && rect.right > -mOffsetX) {//画屏幕内的cell
                            drawCell(canvas, headerCell, rect);
                        }
                        leftPartWidth += DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                    }
                }
            }
            canvas.drawLine(-mOffsetX, headerHeight - mOffsetY, mMeasuredWidth - mOffsetX, headerHeight - mOffsetY, mLinePaint);//header下的分割线

            // 画滚动条
            if (!mIsEnoughDataCount) {
                return;
            }
            if (mScrollbarEnable) {
                rect.left = -mOffsetX;
                rect.top = headerHeight + Math.abs(mOffsetY);
                rect.right = rect.left + 10;
                rect.bottom = mMeasuredHeight + Math.abs(mOffsetY);
                mPaint.setAlpha(127);
                canvas.drawRect(rect, mPaint);
                int barHeight = Math.max(18, (mMeasuredHeight - headerHeight) * (mMeasuredHeight - headerHeight) / (totalDataCount * rowHeight));//滚动条长度
                rect.left = -mOffsetX;
                rect.top = headerHeight + Math.abs(mOffsetY) + (int) ((Float.valueOf(df.format((float) ((mMeasuredHeight - headerHeight) - barHeight) / (totalDataCount * rowHeight - (mMeasuredHeight - headerHeight))))) * Math.abs(mOffsetY));
                rect.right = rect.left + 10;
                rect.bottom = rect.top + barHeight;
                mPaint.setColor(0xFF888888);
                canvas.drawRect(rect, mPaint);
            }
            //画底部当前数据位置提示框
            if (isShowPosition) {
                String noteText = endRowPositionInTotalDataSet + 1 + "/" + totalDataCount;
                mPaint.setColor(0xFFFFFFFF);
                canvas.drawText(noteText, mMeasuredWidth / 2 - mPaint.measureText(noteText) / 2 - mOffsetX, mMeasuredHeight - 31 - mOffsetY, mPaint);
                mPaint.setColor(0x4d888888);
                float noteRectMarginBottom = 20;
                float noteRectHeight = 43;
                rectF.left = mMeasuredWidth / 2 - mPaint.measureText(noteText) * 0.75f - mOffsetX;
                rectF.top = mMeasuredHeight - noteRectHeight - mOffsetY - noteRectMarginBottom;
                rectF.right = mMeasuredWidth / 2 + mPaint.measureText(noteText) * 0.75f - mOffsetX;
                rectF.bottom = mMeasuredHeight - mOffsetY - noteRectMarginBottom;
                canvas.drawRoundRect(rectF, 21, 21, mPaint);
            }
        }
    }

    private void drawRow(Canvas canvas, Row row, int rowPositionInTotalDataSet) {
        rect.left = -mOffsetX;
        rect.top = headerHeight + rowHeight * rowPositionInTotalDataSet;
        rect.right = mMeasuredWidth - mOffsetX;
        rect.bottom = headerHeight + rowHeight * (rowPositionInTotalDataSet + 1);
        if (row.backgroundColor != 0) {
            canvas.save();
            canvas.clipRect(rect);
            canvas.drawColor(row.backgroundColor);
            canvas.restore();
        }
        List<Cell> headerCells = mHeaderRow.getCells();
        List<Cell> cells = row.getCells();
        int size = Math.min(cells.size(), headerCells.size());
        if (mIsFirstColumnPositionFixed) {
            HeaderCell firstHeaderCell = (HeaderCell) headerCells.get(0);
            Cell firstCell = cells.get(0);
            if (mFocusedRowAndColumn != null) {
                isFocusedCellWithListener = cells.get(mFocusedRowAndColumn[1]).isSetListener;
            }
            for (int i = 0; i < size; i++) {
                HeaderCell headerCell = (HeaderCell) headerCells.get(i);
                Cell cell = cells.get(i);
                if (mFocusedRowAndColumn != null) {
                    if (isFocusedCellWithListener) {
                        if (rowPositionInTotalDataSet == mFocusedRowAndColumn[0] && i == mFocusedRowAndColumn[1]) {
                            cell.setCellFocused(true);
                        } else {
                            cell.setCellFocused(false);
                            if (mFocusedRowAndColumn[1] != 0) {
                                firstCell.setCellFocused(false);
                            }
                        }
                    } else {
                        if (rowPositionInTotalDataSet == mFocusedRowAndColumn[0] && onTableItemClickListener != null) {
                            cell.setCellFocused(true);
                        } else {
                            cell.setCellFocused(false);
                        }
                    }
                } else {
                    cell.setCellFocused(false);
                }
                if (i == 0) {
                    leftPartWidth = DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                } else {
                    int left = Math.max(leftPartWidth, DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX);
                    rect.left = leftPartWidth;
                    rect.top = headerHeight + rowHeight * rowPositionInTotalDataSet;
                    rect.right = leftPartWidth + DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                    rect.bottom = headerHeight + rowHeight * (rowPositionInTotalDataSet + 1);
                    if (rect.left < mMeasuredWidth - mOffsetX && rect.right > DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX) {//画屏幕内的cell
                        canvas.save();
                        canvas.clipRect(left, rect.top, rect.right, rect.bottom);
                        drawCell(canvas, cell, rect);
                        canvas.restore();
                    }
                    leftPartWidth += DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                }
            }
            //第一列cell
            rect.left = -mOffsetX;
            rect.top = headerHeight + rowHeight * rowPositionInTotalDataSet;
            rect.right = DensityUtil.dip2px(firstHeaderCell.getColumnWidthInDip()) - mOffsetX;
            rect.bottom = headerHeight + rowHeight * (rowPositionInTotalDataSet + 1);
            drawCell(canvas, firstCell, rect);
        } else {
            if (mFocusedRowAndColumn != null) {
                isFocusedCellWithListener = cells.get(mFocusedRowAndColumn[1]).isSetListener;
            }
            for (int i = 0; i < size; i++) {
                HeaderCell headerCell = (HeaderCell) headerCells.get(i);
                Cell cell = cells.get(i);
                if (mFocusedRowAndColumn != null) {
                    if (isFocusedCellWithListener) {
                        if (rowPositionInTotalDataSet == mFocusedRowAndColumn[0] && i == mFocusedRowAndColumn[1]) {
                            cell.setCellFocused(true);
                        } else {
                            cell.setCellFocused(false);
                        }
                    } else {
                        if (rowPositionInTotalDataSet == mFocusedRowAndColumn[0] && onTableItemClickListener != null) {
                            cell.setCellFocused(true);
                        } else {
                            cell.setCellFocused(false);
                        }
                    }
                } else {
                    cell.setCellFocused(false);
                }
                if (i == 0) {
                    leftPartWidth = DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                    rect.left = 0;
                    rect.top = headerHeight + rowHeight * rowPositionInTotalDataSet;
                    rect.right = leftPartWidth;
                    rect.bottom = headerHeight + rowHeight * (rowPositionInTotalDataSet + 1);
                    if (rect.right > -mOffsetX) {//画屏幕内的cell
                        drawCell(canvas, cell, rect);
                    }
                } else {
                    rect.left = leftPartWidth;
                    rect.top = headerHeight + rowHeight * rowPositionInTotalDataSet;
                    rect.right = leftPartWidth + DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                    rect.bottom = headerHeight + rowHeight * (rowPositionInTotalDataSet + 1);
                    if (rect.left < mMeasuredWidth - mOffsetX && rect.right > -mOffsetX) {//画屏幕内的cell
                        drawCell(canvas, cell, rect);
                    }
                    leftPartWidth += DensityUtil.dip2px(headerCell.getColumnWidthInDip());
                }
            }
        }
        if (row.backgroundDrawable != null) {
            rect.left = -mOffsetX;
            rect.top = headerHeight + rowHeight * rowPositionInTotalDataSet;
            rect.right = mMeasuredWidth - mOffsetX;
            rect.bottom = headerHeight + rowHeight * (rowPositionInTotalDataSet + 1);
            canvas.save();
            row.backgroundDrawable.setBounds(rect);
            row.backgroundDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.drawLine(-mOffsetX, headerHeight + rowHeight * (rowPositionInTotalDataSet + 1), mMeasuredWidth - mOffsetX, headerHeight + rowHeight * (rowPositionInTotalDataSet + 1), mLinePaint);//每一行分割线
    }

    private void drawCell(Canvas canvas, Cell cell, Rect rect) {
        canvas.save();
        canvas.clipRect(rect);
        cell.onDraw(canvas, rect);
        canvas.restore();
    }

    private int startX;
    private int startY;
    private float xVelocity;
    private float yVelocity;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTableAdapter == null) {
            return false;
        }
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOverScrollX = 0;
                mOverScrollY = 0;
                startX = (int) event.getX();
                startY = (int) event.getY();
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                if (mValueAnimator != null && mValueAnimator.isRunning()) {
                    mValueAnimator.cancel();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1);
                xVelocity = mVelocityTracker.getXVelocity();
                yVelocity = mVelocityTracker.getYVelocity();
                int currentX = (int) event.getX();
                int currentY = (int) event.getY();
                int dx = currentX - startX;
                int dy = currentY - startY;
                if (startX > mEfficientX && startY > mEfficientY && (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > mTouchSlop) && mPreState == State.IDLE && maxHeadersWidth > mMeasuredWidth) {
                    mState = State.TOLEFTORRIGHT;//记录为左右滑动状态
                    resetFocused();
                }
                if (startY > mEfficientY && (Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > mTouchSlop) && mPreState == State.IDLE) {
                    if (mIsEnoughDataCount) {
                        mState = State.TOUPORDOWN;//记录为上下滑动状态
                        resetFocused();
                    }
                }
                if (mState == State.TOLEFTORRIGHT) {
                    if (mOverScrollX > 0 && xVelocity < 0) {
                        mPreOffsetX = 0;
                        startX = (int) event.getX();
                    }
                    if (mOverScrollX < 0 && xVelocity > 0) {
                        mPreOffsetX = mMeasuredWidth - maxHeadersWidth;
                        startX = (int) event.getX();
                    }
                    dx = currentX - startX;
                    //处理右边内容区域左右滑动事件
                    mOffsetX = mPreOffsetX + dx;
                    int tempX = mOffsetX;
                    //限制mOffsetX范围
                    mOffsetX = fixOffsetX(mOffsetX);
                    mOverScrollX = tempX - mOffsetX;
                    switch (mPreState) {
                        case IDLE:
                            //从默认状态改变为左右滑动状态
                            mPreState = mState;
                            scrollTo(-mOffsetX, -mOffsetY);
                            break;
                        case TOLEFTORRIGHT:
                            //状态未发生变化
                            scrollTo(-mOffsetX, -mOffsetY);
                            break;
                        case TOUPORDOWN:
                            //从上下滑动状态改变为左右滑动状态
                            break;
                    }
                } else if (mState == State.TOUPORDOWN) {
                    isShowPosition = true;
                    if (mOverScrollY > 0 && yVelocity < -0.02) {
                        mPreOffsetY = 0;
                        startY = (int) event.getY();
                    }
                    if (mOverScrollY < 0 && yVelocity > 0.02) {
                        mPreOffsetY = mMeasuredHeight - headerHeight - mTableAdapter.getTotalDataCount() * rowHeight;
                        startY = (int) event.getY();
                    }
                    dy = currentY - startY;
                    //处理下边内容区域上下滑动事件
                    mOffsetY = mPreOffsetY + dy;
                    int tempY = mOffsetY;
                    //限制mOffsetY范围
                    mOffsetY = fixOffsetY(mOffsetY);

                    mOverScrollY = tempY - mOffsetY;
                    switch (mPreState) {
                        case IDLE:
                            mPreState = mState;
                            scrollTo(-mOffsetX, -mOffsetY);
                            break;
                        case TOLEFTORRIGHT:
                            //从左右滑动状态改变为上下滑动状态
                            break;
                        case TOUPORDOWN:
                            //状态未发生变化
                            scrollTo(-mOffsetX, -mOffsetY);
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsSingleTapUp) {
                    switch (mPreState) {
                        case IDLE:
                            break;
                        case TOLEFTORRIGHT:
                            long xDuration = Math.min(1000, (long) Math.abs(xVelocity) * 150);
                            int flingDistance = (int) (xVelocity * xDuration / 4);
                            int endX = mOffsetX + flingDistance;
                            endX = fixOffsetX(endX);
                            mValueAnimator = ValueAnimator.ofInt(mOffsetX, endX);
                            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mOffsetX = (int) animation.getAnimatedValue();
                                    mOffsetX = fixOffsetX(mOffsetX);
                                    mPreOffsetX = mOffsetX;
                                    scrollTo(-mOffsetX, -mOffsetY);
                                }
                            });
                            mValueAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (mHorizontalScrollListener != null) {
                                        mHorizontalScrollListener.onHorizontalScrollStop();
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                            mValueAnimator.setDuration(xDuration);
                            mValueAnimator.setInterpolator(new DecelerateInterpolator(2));//设置动画差值器
                            mValueAnimator.start();
                            break;
                        case TOUPORDOWN:
                            long yDuration = Math.min(1000, (long) Math.abs(yVelocity) * 150);
                            int yFlingDistance = (int) (yVelocity * yDuration / 4);
                            int endY = mOffsetY + yFlingDistance;
                            endY = fixOffsetY(endY);
                            mValueAnimator = ValueAnimator.ofInt(mOffsetY, endY);
                            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    mOffsetY = (int) animation.getAnimatedValue();
                                    mOffsetY = fixOffsetY(mOffsetY);
                                    mPreOffsetY = mOffsetY;
                                    scrollTo(-mOffsetX, -mOffsetY);
                                }
                            });
                            mValueAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    postInvalidate();
                                    endScrolling();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            mValueAnimator.setDuration(yDuration);
                            mValueAnimator.setInterpolator(new DecelerateInterpolator(2));//设置动画差值器
                            mValueAnimator.start();
                            break;
                    }
                }
                resetState();
                break;
            case MotionEvent.ACTION_CANCEL:
                endScrolling();
                resetState();
                mPreOffsetY = mOffsetY;
                scrollTo(-mOffsetX, -mOffsetY);
                super.onTouchEvent(event);
                break;
        }
        return true;
    }

    private void endScrolling() {
        isShowPosition = false;
        int lastPositionInTotalDataSet = ((mMeasuredHeight - headerHeight) + Math.abs(mOffsetY)) % rowHeight == 0 ?
                ((mMeasuredHeight - headerHeight) + Math.abs(mOffsetY)) / rowHeight - 1 : ((mMeasuredHeight - headerHeight) + Math.abs(mOffsetY)) / rowHeight;
        if (mTableListener != null) {
            mTableListener.onScrollEnd(TableView.this, Math.abs(mOffsetY) / rowHeight, lastPositionInTotalDataSet);
        }
    }

    private void resetState() {
        mState = State.IDLE;
        mPreState = State.IDLE;
        mOverScrollX = 0;
        mOverScrollY = 0;
        mIsLongPress = false;
        isFocusedCellWithListener = false;

        long delayTime = mIsSingleTapUp ? 80 : 0;
        mIsShowPress = false;
        mIsSingleTapUp = false;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFocusedRowAndColumn = null;
                postInvalidate();
            }
        }, delayTime);
    }

    /**
     * 点击表头排序需要让列表回到第一个位置时调用这个方法
     */
    public void resetSortState() {
        mPreOffsetY = 0;
        mOffsetY = 0;
        for (int i = 0; i < mRows.size(); i++) {
            mRows.get(i).rowPositionInTotalDataSet = i;
        }
        scrollTo(-mOffsetX, 0);
    }

    /**
     * 初始化状态（设置或者切换adapter）
     */
    private void initState() {
        mPreOffsetX = 0;
        mOffsetX = 0;
        mPreOffsetY = 0;
        mOffsetY = 0;
        mNeedRefreshRows = true;
        isNeedHeadersWidth = true;
        mIsNeedRefreshHeaders = true;
        mRows.clear();
        scrollTo(0, 0);
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
    }

    private void performCellClick(int rowPositionInTotalDataSet) {
        int columnIndex = 0;
        if (mIsFirstColumnPositionFixed) {
            if (startX > fixedLeftPart) {
                columnIndex = getColumnIndex(columnIndex);
            }
        } else {
            columnIndex = getColumnIndex(columnIndex);
        }
        List<Cell> cells = null;
        if (mIsFirstRowPositionFixed) {
            if (mFirstRow != null) {
                cells = mFirstRow.getCells();
            }
        } else {
            int position = rowPositionInTotalDataSet - mRowStartPositionInTotalDataSet;
            if (position >= 0 && position < mRows.size()) {
                Row pressedRow = mRows.get(position);
                cells = pressedRow.getCells();
            }
        }
        if (cells != null) {
            Cell cell = cells.get(columnIndex);
            if (mIsFirstRowPositionFixed && rowPositionInTotalDataSet == 0) {
                boolean isFirstRowCellClick = cell.performClick(0, columnIndex);
                if (!isFirstRowCellClick && onTableItemClickListener != null) {
                    onTableItemClickListener.onClick(0);
                }
            } else {
                boolean isCellClick = cell.performClick(rowPositionInTotalDataSet - mWindowStartPositionInTotalDataSet, columnIndex);
                if (!isCellClick && onTableItemClickListener != null) {
                    onTableItemClickListener.onClick(rowPositionInTotalDataSet - mWindowStartPositionInTotalDataSet);
                }
            }
        }
    }

    private int getColumnIndex(int columnIndex) {
        int x = startX - mOffsetX;
        for (int i = 1; i < mColumnEndWidth.size(); i++) {
            if (x > mColumnEndWidth.get(i - 1) && x < mColumnEndWidth.get(i)) {
                columnIndex = i;
            }
        }
        return columnIndex;
    }

    /**
     * 限制mOffsetX范围
     *
     * @param offsetX x偏移量
     * @return 修正后的x偏移量
     */
    private int fixOffsetX(int offsetX) {
        if (offsetX > 0 || maxHeadersWidth < mMeasuredWidth) {
            offsetX = 0;
            mPreOffsetX = 0;
        } else if (offsetX < mMeasuredWidth - maxHeadersWidth) {
            offsetX = mMeasuredWidth - maxHeadersWidth;
            mPreOffsetX = offsetX;
        }
        return offsetX;
    }

    /**
     * 限制mOffsetY范围
     *
     * @param offsetY y偏移量
     * @return 修正后的y偏移量
     */
    private int fixOffsetY(int offsetY) {
        if (offsetY > 0 || !mIsEnoughDataCount) {
            offsetY = 0;
            mPreOffsetY = 0;
        } else if (offsetY < mMeasuredHeight - headerHeight - rowHeight * mTableAdapter.getTotalDataCount()) {
            offsetY = mMeasuredHeight - headerHeight - rowHeight * mTableAdapter.getTotalDataCount();
            mPreOffsetY = offsetY;
        }
        return offsetY;
    }

    public State getTableViewState() {
        return mState;
    }

    public void setTableAdapter(TableDataAdapter tableAdapter) {
        initState();
        this.mTableAdapter = tableAdapter;
        mTableAdapter.setTableView(this);
        if (mTableListener != null) {
            mTableListener.onTableReady(this);
        }
        notifyDataChanged();
    }

    public TableDataAdapter getTableAdapter() {
        return mTableAdapter;
    }

    public void setTableListener(TableListener tableListener) {
        this.mTableListener = tableListener;
    }

    public void setHorizontalScrollListener(HorizontalScrollListener listener) {
        this.mHorizontalScrollListener = listener;
    }

    public TableListener getTableListener() {
        return mTableListener;
    }

    public void setFirstRowPositionFixed() {
        mIsFirstRowPositionFixed = true;
    }

    public void setFirstColumnPositionFixed() {
        mIsFirstColumnPositionFixed = true;
    }

    public boolean isReachTop() {
        return mOffsetY == 0;
    }

    public boolean isReachBottom() {
        return !mIsEnoughDataCount || mOffsetY == mMeasuredHeight - headerHeight - rowHeight * mTableAdapter.getTotalDataCount();
    }

    public boolean isHorizontalMove() {
        return mState == State.TOLEFTORRIGHT;
    }

    public boolean isScrollbarEnable() {
        return mScrollbarEnable;
    }

    public void setScrollbarEnable(boolean scrollbarEnable) {
        this.mScrollbarEnable = scrollbarEnable;
    }

    public void notifyDataChanged() {
        mNeedRefreshRows = true;
        mIsNeedRefreshHeaders = true;
        postInvalidate();
    }

    public int getRowCountInDisplay() {
        return mRows.size();
    }

    private OnTableItemClickListener onTableItemClickListener;

    public interface OnTableItemClickListener {
        void onClick(int rowPositionInWindowDataSet);
    }

    public void setOnTableItemClickListener(OnTableItemClickListener listener) {
        onTableItemClickListener = listener;
    }

    private OnTableItemLongClickListener onTableItemLongClickListener;

    public interface OnTableItemLongClickListener {
        void onClick(int rowPositionInWindowDataSet);
    }

    public void setOnTableItemLongClickListener(OnTableItemLongClickListener listener) {
        onTableItemLongClickListener = listener;
    }

    /**
     * Check if this view can be scrolled horizontally in a certain direction.
     *
     * @param direction Negative to check scrolling left, positive to check scrolling right.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (!isShown()) {
            return false;
        }
        final int range = maxHeadersWidth - mMeasuredWidth;
        if (range == 0) return false;
        if (direction < 0) {
            return -mOffsetX > 0;
        } else {
            return -mOffsetX < range - 1;
        }
    }
}