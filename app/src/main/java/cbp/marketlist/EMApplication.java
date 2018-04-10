package cbp.marketlist;

import android.app.Application;

import cbp.marketlist.utils.ContextUtil;


public class EMApplication extends Application {
    private static final String TAG = EMApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        ContextUtil.init(this);
        super.onCreate();
    }
}
