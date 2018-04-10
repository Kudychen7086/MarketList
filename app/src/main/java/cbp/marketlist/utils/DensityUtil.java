package cbp.marketlist.utils;

import android.content.Context;

/**
 * 设备屏幕工具类
 *
 * @author cbp
 */
public class DensityUtil {

    /**
     * dp -> px
     *
     * @param dpValue dp数值
     * @return dp to  px
     */
    public static int dip2px(float dpValue) {
        final float scale = ContextUtil.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp -> px
     *
     * @param spValue dp数值
     * @return sp to  px
     */
    public static int sp2px(float spValue) {
        final float fontScale = ContextUtil.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px -> sp
     *
     * @param pxValue px数值
     * @return px to  sp
     */
    public static float px2sp(float pxValue) {
        return px2sp(ContextUtil.getContext(), pxValue);
    }

    public static float px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale;
    }

    /**
     * px -> dp
     *
     * @param pxValue px的数值
     * @return px to dp
     */
    public static int px2dip(float pxValue) {
        final float scale = ContextUtil.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}