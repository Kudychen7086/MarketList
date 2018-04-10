package cbp.marketlist.tableview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import cbp.marketlist.utils.DensityUtil;

/**
 * 单行文本单元格
 *
 * @author cbp
 */
public class SingleTextCell extends Cell {
    private String text;
    private Paint paint;

    public SingleTextCell() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public SingleTextCell(String text) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setText(text);
    }

    public SingleTextCell(String text, Style style) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setText(text);
        setStyle(style);
        setMaxCharCountRatio(2.0f);
    }

    public SingleTextCell(String text, Style style, int paddingLeft) {
        this(text, style);
        setPaddingLeft(paddingLeft);
    }

    public SingleTextCell(String text, Style style, Gravity gravity) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setText(text);
        setStyle(style);
        setGravity(gravity);
    }

    public SingleTextCell(String text, Style style, Gravity gravity, float maxCharCountRatio) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setText(text);
        setStyle(style);
        setGravity(gravity);
        setMaxCharCountRatio(maxCharCountRatio);
    }

    public void onDraw(Canvas canvas, Rect rect) {
        Style style = getStyle();
        int textSizeSp = style.getTextSizeSp();
        int textColor = style.getTextColor();
        int backgroundColor = style.getBackgroundColor();
        if (isChanged) {
            if (style instanceof HighLightStyle) {
                backgroundColor = ((HighLightStyle) style).getHighLightBackgroundColor();
            }
        }
        paint.setTextSize(DensityUtil.sp2px(textSizeSp));
        paint.setColor(textColor);
        float ascent = paint.getFontMetrics().ascent;
        float descent = paint.getFontMetrics().descent;

        if (isCellFocused) {
            canvas.drawColor(style.getClickBackgroundColor());
        } else if (backgroundColor != 0) {
            canvas.drawColor(backgroundColor);
        }
        paint.setTextScaleX(1);
        String text = getText();
        float measureText = paint.measureText(text);
        int rectWidth = rect.right - (rect.left + getPaddingLeft());
        float widthRatio = rectWidth / measureText;
        float ratio = 1 / maxCharCountRatio;
        if (widthRatio > ratio && widthRatio <= 1) {
            paint.setTextScaleX(widthRatio * 0.98f);
        } else if (widthRatio <= ratio) {
            int countCanBeDisplayed = paint.breakText(text, true, rectWidth * maxCharCountRatio, new float[1]);
            text = text.substring(0, (int) (countCanBeDisplayed - maxCharCountRatio)) + "..";
            paint.setTextScaleX(ratio);
        }
        int left = rect.left + getPaddingLeft();
        int top = (int) (rect.top - ascent);
        int right = measureText > rectWidth ? left : (int) (rect.right - measureText);
        int bottom = (int) (rect.bottom - descent);

        switch (getGravity()) {
            case LEFT:
                canvas.drawText(text, left, (top + bottom) / 2, paint);
                break;
            case LEFT_TOP:
                canvas.drawText(text, left, top, paint);
                break;
            case LEFT_BOTTOM:
                canvas.drawText(text, left, bottom, paint);
                break;
            case CENTER:
                canvas.drawText(text, (left + right) / 2, (top + bottom) / 2, paint);
                break;
            case CENTER_TOP:
                canvas.drawText(text, (left + right) / 2, top, paint);
                break;
            case CENTER_BOTTOM:
                canvas.drawText(text, (left + right) / 2, bottom, paint);
                break;
            case RIGHT:
                canvas.drawText(text, right, (top + bottom) / 2, paint);
                break;
            case RIGHT_TOP:
                canvas.drawText(text, right, top, paint);
                break;
            case RIGHT_BOTTOM:
                canvas.drawText(text, right, bottom, paint);
                break;
        }
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            text = "--";
        }
        this.text = text;
    }
}