package jp.bunkaich.sukashimotion;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Display;
import android.view.WindowManager;

/** Creates display-bound overlay contexts without losing the app-specific locale. */
final class WindowContexts {
    private WindowContexts() {}

    static Context overlay(Context owner, Display display) {
        Context windowContext=owner.createDisplayContext(display)
                .createWindowContext(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,null);
        Configuration configuration=new Configuration(windowContext.getResources().getConfiguration());
        configuration.setLocales(owner.getResources().getConfiguration().getLocales());
        return windowContext.createConfigurationContext(configuration);
    }
}
