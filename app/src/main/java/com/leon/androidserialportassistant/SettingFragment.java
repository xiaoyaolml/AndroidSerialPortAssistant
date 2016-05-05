package com.leon.androidserialportassistant;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by Leon on 2016-05-05.
 */
public class SettingFragment extends PreferenceFragment {
    private SerialPortFinder mSerialPortFinder;
    public ListPreference mListDevice;
    public ListPreference mListBaudrate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        mSerialPortFinder = new SerialPortFinder();
        String[] entries = mSerialPortFinder.getAllDevices();
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();

        mListDevice = (ListPreference) findPreference("DEVICE");
        mListBaudrate = (ListPreference) findPreference("BAUDRATE");

        mListDevice.setEntries(entries);
        mListDevice.setEntryValues(entryValues);
        mListDevice.setSummary(mListDevice.getValue());
        mListDevice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });

        mListBaudrate.setSummary(mListBaudrate.getValue());
        mListBaudrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });


        // TODO: 2016-05-05  setting_recv and setting_send

    }

}
