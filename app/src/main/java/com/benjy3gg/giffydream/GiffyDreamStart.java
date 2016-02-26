package com.benjy3gg.giffydream;

/**
 * Created by benjy3gg on 26.02.2016.
 */
import android.app.Activity;
import android.content.Intent;

/**
 * To use, replace "extends Activity" in your activity with "extends DreamNow".
 *
 * From the Google Android Blog Daydream example.
 */
public class GiffyDreamStart extends Activity {
    @Override
    public void onStart() {
        super.onStart();
        final Intent intent = new Intent(Intent.ACTION_MAIN);

        try {
            // Somnabulator is undocumented--may be removed in a future version...
            intent.setClassName("com.android.systemui",
                    "com.android.systemui.Somnambulator");
            startActivity(intent);
            finish();

        } catch (Exception e) { /* Do nothing */ }
    }
}
