package cbp.marketlist.tableview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import cbp.marketlist.utils.DensityUtil;


/**
 * 双行文本单元格
 *
 * @author cbp
 */
public class TwoRowTextCell extends Cell {
    private String textRow1;
    private String textRow2;
    private Style styleRow1;
    private Style styleRow2;
    private Paint paint1;
    private Paint paint2;

    private int paddingLeft = 12;

    public TwoRowTextCell() {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public TwoRowTextCell(String textRow1, String textRow2) {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextRow1(textRow1);
        setTextRow2(textRow2);
    }

    public TwoRowTextCell(String textRow1, String textRow2, Style styleRow1, Style styleRow2) {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextRow1(textRow1);
        setTextRow2(textRow2);
        setStyleRow1(styleRow1);
        setStyleRow2(styleRow2);
    }

    public TwoRowTextCell(String textRow1, String textRow2, Style styleRow1, Style styleRow2, Gravity gravity) {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextRow1(textRow1);
        setTextRow2(textRow2);
        setStyleRow1(styleRow1);
        setStyleRow2(styleRow2);
        setGravity(gravity);
        setMaxCharCountRatio(2.0f);
    }

    public TwoRowTextCell(String textRow1, String textRow2, Style styleRow1, Style styleRow2, Gravity gravity, float maxCharCountRatio) {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextRow1(textRow1);
        setTextRow2(textRow2);
        setStyleRow1(styleRow1);
        setStyleRow2(styleRow2);
        setGravity(gravity);
        setMaxCharCountRatio(maxCharCountRatio);
    }

    public TwoRowTextCell(String textRow1, String textRow2, Style styleRow1, Style styleRow2, float maxCharCountRatio) {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextRow1(textRow1);
        setTextRow2(textRow2);
        setStyleRow1(styleRow1);
        setStyleRow2(styleRow2);
        setMaxCharCountRatio(maxCharCountRatio);
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    private void setTextRow1(String textRow1) {
        if (TextUtils.isEmpty(textRow1)) {
            textRow1 = "--";
        }
        this.textRow1 = textRow1;
    }

    String getTextRow1() {
        return textRow1;
    }

    String getTextRow2() {
        return textRow2;
    }

    private void setTextRow2(String textRow2) {
        if (TextUtils.isEmpty(textRow2)) {
            textRow2 = "--";
        }
        this.textRow2 = textRow2;
    }

    Style getStyleRow1() {
        if (styleRow1 == null) {
            return super.getStyle();
        }
        return styleRow1;
    }

    private void setStyleRow1(Style styleRow1) {
        this.styleRow1 = styleRow1;
    }

    Style getStyleRow2() {
        if (styleRow2 == null) {
            return super.getStyle();
        }
        return styleRow2;
    }

    private void setStyleRow2(Style styleRow2) {
        this.styleRow2 = styleRow2;
    }

    public void onDraw(Canvas canvas, Rect rect) {
        Style styleRow1 = getStyleRow1();
        int textSizeSp1 = styleRow1.getTextSizeSp();
        int textColor1 = styleRow1.getTextColor();
        int backgroundColor1 = styleRow1.getBackgroundColor();
        if (isChanged) {
            if (styleRow1 instanceof HighLightStyle) {
                backgroundColor1 = ((HighLightStyle) styleRow1).getHighLightBackgroundColor();
            }
        }
        paint1.setTextSize(DensityUtil.sp2px(textSizeSp1));
        paint1.setColor(textColor1);
        float ascent1 = paint1.getFontMetrics().ascent;
        float descent1 = paint1.getFontMetrics().descent;

        Style styleRow2 = getStyleRow2();
        int textSizeSp2 = styleRow2.getTextSizeSp();
        int textColor2 = styleRow2.getTextColor();
        paint2.setTextSize(DensityUtil.sp2px(textSizeSp2));
        paint2.setColor(textColor2);
        float ascent2 = paint2.getFontMetrics().ascent;
        float descent2 = paint2.getFontMetrics().descent;

        if (isCellFocused) {
            canvas.drawColor(styleRow1.getClickBackgroundColor());
        } else if (backgroundColor1 != 0) {
            canvas.drawColor(backgroundColor1);
        }
        int rectWidth = rect.right - rect.left - 12;
        String textRow1 = getTextRow1();
        paint1.setTextScaleX(1);
        float measureText1 = paint1.measureText(textRow1);
        float widthRatio1 = rectWidth / measureText1;
        float ratio1 = 1 / maxCharCountRatio;

        if (widthRatio1 > ratio1 && widthRatio1 <= 1) {
            paint1.setTextScaleX(widthRatio1 * 0.98f);
        } else if (widthRatio1 <= ratio1) {
            int countCanBeDisplayed = paint1.breakText(textRow1, true, rectWidth * maxCharCountRatio, new float[1]);
            textRow1 = textRow1.substring(0, (int) (countCanBeDisplayed - maxCharCountRatio)) + "..";
            paint1.setTextScaleX(ratio1);
        }
        int left1 = rect.left;
        int top1 = (int) (rect.top - ascent1);
        int right1 = measureText1 > rectWidth ? rect.left : (int) (rect.right - measureText1);
        int bottom1 = (int) ((rect.top + rect.bottom) / 2 - descent1);

        String textRow2 = getTextRow2();
        paint2.setTextScaleX(1);
        float measureText2 = paint2.measureText(textRow2);
        float widthRatio2 = rectWidth / measureText2;
        float ratio2 = 1 / maxCharCountRatio;

        if (widthRatio2 > ratio2 && widthRatio2 <= 1) {
            paint2.setTextScaleX(widthRatio2 * 0.98f);
        } else if (widthRatio2 <= ratio2) {
            int countCanBeDisplayed = paint2.breakText(textRow2, true, rectWidth * maxCharCountRatio, new float[1]);
            textRow2 = textRow2.substring(0, (int) (countCanBeDisplayed - maxCharCountRatio)) + "..";
            paint2.setTextScaleX(ratio2);
        }
        int left2 = rect.left;
        int top2 = (int) ((rect.top + rect.bottom) / 2 - ascent2);
        int right2 = (int) (rect.right - paint2.measureText(textRow2));
        int bottom2 = (int) (rect.bottom - descent2);

        float row1Height = rect.height() * 0.6f;
        float row1Bottom = rect.top + row1Height;
        float row2Height = rect.height() * 0.4f;
        float text1Baseline = row1Bottom - paint1.getFontMetrics().bottom;
        float text2Baseline = row1Bottom - paint2.getFontMetrics().top;

        switch (getGravity()) {
            case LEFT:
                canvas.drawText(textRow1, left1 + paddingLeft, text1Baseline, paint1);
                canvas.drawText(textRow2, left2 + paddingLeft, text2Baseline, paint2);
                break;
            case LEFT_TOP:
                canvas.drawText(textRow1, left1 + paddingLeft, top1, paint1);
                canvas.drawText(textRow2, left2 + paddingLeft, top2, paint2);
                break;
            case LEFT_BOTTOM:
                canvas.drawText(textRow1, left1 + paddingLeft, bottom1, paint1);
                canvas.drawText(textRow2, left2 + paddingLeft, bottom2, paint2);
                break;
            case CENTER:
                canvas.drawText(textRow1, (left1 + right1) / 2, (top1 + bottom1) / 2, paint1);
                canvas.drawText(textRow2, (left2 + right2) / 2, (top2 + bottom2) / 2, paint2);
                break;
            case CENTER_TOP:
                canvas.drawText(textRow1, (left1 + right1) / 2, top1, paint1);
                canvas.drawText(textRow2, (left2 + right2) / 2, top2, paint2);
                break;
            case CENTER_BOTTOM:
                canvas.drawText(textRow1, (left1 + right1) / 2, bottom1, paint1);
                canvas.drawText(textRow2, (left2 + right2) / 2, bottom2, paint2);
                break;
            case RIGHT:
                canvas.drawText(textRow1, right1, text1Baseline, paint1);
                canvas.drawText(textRow2, right2, text2Baseline, paint2);
                break;
            case RIGHT_TOP:
                canvas.drawText(textRow1, right1, top1, paint1);
                canvas.drawText(textRow2, right2, top2, paint2);
                break;
            case RIGHT_BOTTOM:
                canvas.drawText(textRow1, right1, bottom1, paint1);
                canvas.drawText(textRow2, right2, bottom2, paint2);
                break;
        }
    }
}
