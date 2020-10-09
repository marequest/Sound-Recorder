package doitgames.soundrecorder.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import doitgames.soundrecorder.MySharedPreferences;
import doitgames.soundrecorder.R;
import doitgames.soundrecorder.activites.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference_screen);

        SwitchPreference switchHighQuality = (SwitchPreference) findPreference("switch_high_quality");
        switchHighQuality.setChecked(MySharedPreferences.getPrefHighQuality(getActivity().getApplicationContext()));
        switchHighQuality.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                MySharedPreferences.setPrefHighQuality(getActivity().getApplicationContext(), (boolean) o);
                return true;
            }
        });

        Preference aboutPref = findPreference("pref_about");
        aboutPref.setSummary("version: 1.0.0");
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.show(((SettingsActivity)getActivity()).getSupportFragmentManager().beginTransaction(), "dialog_about");
                return true;
            }
        });
    }
}
