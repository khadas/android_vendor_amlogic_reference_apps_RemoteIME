/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidlogic.inputmethod.remote;

import java.util.List;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import android.util.Log;
import com.droidlogic.inputmethod.remote.Settings;

import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * Setting activity of Pinyin IME.
 */
public class SettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {

        private static String TAG = "SettingsActivity";

        private CheckBoxPreference mKeySoundPref;
        private CheckBoxPreference mVibratePref;
        private CheckBoxPreference mPredictionPref;

        @Override
        protected void onCreate ( Bundle savedInstanceState ) {
            super.onCreate ( savedInstanceState );
            addPreferencesFromResource ( R.xml.settings );
            PreferenceScreen prefSet = getPreferenceScreen();
            mKeySoundPref = ( CheckBoxPreference ) prefSet
                            .findPreference ( getString ( R.string.setting_sound_key ) );
            if ( mKeySoundPref != null ) {
                mKeySoundPref.setOnPreferenceChangeListener(this);
            }

            mVibratePref = ( CheckBoxPreference ) prefSet
                           .findPreference ( getString ( R.string.setting_vibrate_key ) );
            if ( mVibratePref != null ) {
                mVibratePref.setOnPreferenceChangeListener(this);
            }

            mPredictionPref = ( CheckBoxPreference ) prefSet
                              .findPreference ( getString ( R.string.setting_prediction_key ) );
            if ( mPredictionPref != null ) {
                mPredictionPref.setOnPreferenceChangeListener(this);
            }

            prefSet.setOnPreferenceChangeListener ( this );
            Settings.getInstance (this.getSharedPreferences(Settings.HARED_PREF_DVR_WATCHED_POSITION,
                    Context.MODE_PRIVATE));
            updatePreference ( prefSet, getString ( R.string.setting_advanced_key ) );
            updateWidgets();
        }

        @Override
        protected void onResume() {
            super.onResume();
            updateWidgets();
        }

        @Override
        protected void onDestroy() {
            Settings.releaseInstance();
            super.onDestroy();
        }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d(TAG, "onPreferenceChange, " + (boolean) newValue);
        boolean isChecked = (boolean) newValue;
        if (preference.getKey().equals(getString(R.string.setting_sound_key))) {
            Settings.setKeySound(isChecked);
        } else if (preference.getKey().equals(getString(R.string.setting_vibrate_key))) {
            Settings.setVibrate(isChecked);
        }else if (preference.getKey().equals(getString(R.string.setting_prediction_key))) {
            Settings.setPrediction(isChecked);
        }
        Settings.writeBack();
        return true;
    }

        private void updateWidgets() {
            mKeySoundPref.setChecked ( Settings.getKeySound() );
            mVibratePref.setChecked ( Settings.getVibrate() );
            mPredictionPref.setChecked ( Settings.getPrediction() );
        }

        public void updatePreference ( PreferenceGroup parentPref, String prefKey ) {
            Preference preference = parentPref.findPreference ( prefKey );
            if ( preference == null ) {
                return;
            }
            Intent intent = preference.getIntent();
            if ( intent != null ) {
                PackageManager pm = getPackageManager();
                List<ResolveInfo> list = pm.queryIntentActivities ( intent, 0 );
                int listSize = list.size();
                if ( listSize == 0 ) {
                    parentPref.removePreference ( preference );
                }
            }
        }
}
