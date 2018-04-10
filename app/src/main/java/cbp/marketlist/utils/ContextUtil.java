package cbp.marketlist.utils;

import android.content.Context;

/**
 * 提供全局context
 *
 * @author cbp
 */
public class ContextUtil {
    private static Context context;

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
