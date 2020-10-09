package doitgames.soundrecorder;

import android.content.Context;
import android.preference.PreferenceManager;

public class MySharedPreferences {
    public static void setPrefHighQuality(Context context, boolean isHighQuality) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean("pref_high_quality", isHighQuality)
                .apply();
    }

    public static boolean getPrefHighQuality(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("pref_high_quality", false);
    }
}
