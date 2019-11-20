/*
 * Copyright 2014 Gerhard Klostermeier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package de.syss.MCTBrute4c.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import de.syss.MCTBrute4c.Common;
import de.syss.MCTBrute4c.R;

/**
 * This view will let the user edit global preferences.
 *
 */
public class Preferences extends BasicActivity {

    /**
     * Enumeration with all preferences. This enumeration implements
     * "toString()" so it can be used to access the shared preferences (e.g.
     * SharedPreferences.getBoolean(Pref.AutoReconnect.toString(), false)).
     */
    public enum Preference {
        AutoReconnect("auto_reconnect"),
        AutoCopyUID("auto_copy_uid"),
        UIDFormat("uid_format"),
        SaveLastUsedKeyFiles("save_last_used_key_files"),
        UseCustomSectorCount("use_custom_sector_count"),
        CustomSectorCount("custom_sector_count"),
        UseInternalStorage("use_internal_storage"),
        UseRetryAuthentication("use_retry_authentication"),
        RetryAuthenticationCount("retry_authentication_count");
        // Add more preferences here (comma separated).

        private final String text;

        private Preference(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private CheckBox mPrefAutoReconnect;
    private CheckBox mPrefAutoCopyUID;
    private CheckBox mPrefSaveLastUsedKeyFiles;
    private CheckBox mUseCustomSectorCount;
    private CheckBox mUseRetryAuthentication;
    private CheckBox mUseInternalStorage;
    private EditText mCustomSectorCount;
    private EditText mRetryAuthenticationCount;
    private RadioGroup mUIDFormatRadioGroup;

    /**
     * Initialize the preferences with the last stored ones.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // Get preferences (init. the member variables).
        mPrefAutoReconnect = (CheckBox) findViewById(R.id.checkBoxPreferencesAutoReconnect);
        mPrefAutoCopyUID = (CheckBox) findViewById(R.id.checkBoxPreferencesCopyUID);
        mPrefSaveLastUsedKeyFiles = (CheckBox) findViewById(R.id.checkBoxPreferencesSaveLastUsedKeyFiles);
        mUseCustomSectorCount = (CheckBox) findViewById(R.id.checkBoxPreferencesUseCustomSectorCount);
        mCustomSectorCount = (EditText) findViewById(R.id.editTextPreferencesCustomSectorCount);
        mUseInternalStorage = (CheckBox) findViewById(R.id.checkBoxPreferencesUseInternalStorage);
        mUseRetryAuthentication = (CheckBox) findViewById(R.id.checkBoxPreferencesUseRetryAuthentication);
        mRetryAuthenticationCount = (EditText) findViewById(R.id.editTextPreferencesRetryAuthenticationCount);

        // Assign the last stored values.
        SharedPreferences pref = Common.getPreferences();
        mPrefAutoReconnect.setChecked(pref.getBoolean(
                Preference.AutoReconnect.toString(), false));
        mPrefAutoCopyUID.setChecked(pref.getBoolean(
                Preference.AutoCopyUID.toString(), false));
        setUIDFormatBySequence(pref.getInt(Preference.UIDFormat.toString(),0));
        mPrefSaveLastUsedKeyFiles.setChecked(pref.getBoolean(
                Preference.SaveLastUsedKeyFiles.toString(), true));
        mUseCustomSectorCount.setChecked(pref.getBoolean(
                Preference.UseCustomSectorCount.toString(), false));
        mCustomSectorCount.setEnabled(mUseCustomSectorCount.isChecked());
        mCustomSectorCount.setText("" + pref.getInt(
                Preference.CustomSectorCount.toString(), 16));
        mUseInternalStorage.setChecked(pref.getBoolean(
                Preference.UseInternalStorage.toString(), false));
        mUseRetryAuthentication.setChecked(pref.getBoolean(
                Preference.UseRetryAuthentication.toString(), false));
        mRetryAuthenticationCount.setEnabled(
                mUseRetryAuthentication.isChecked());
        mRetryAuthenticationCount.setText("" + pref.getInt(
                Preference.RetryAuthenticationCount.toString(), 1));

        // UID Format Options (hide/show)
        mUIDFormatRadioGroup = (RadioGroup) findViewById(R.id.radioGroupUIDFormat);
        toggleUIDFormat(null);
    }

    /**
     * Show information on the "auto reconnect" preference.
     * @param view The View object that triggered the method
     * (in this case the info on auto reconnect button).
     */
    public void onShowAutoReconnectInfo(View view) {
        AlertDialog.Builder ventana1 = new AlertDialog.Builder(this);
        ventana1.setTitle(R.string.dialog_auto_reconnect_title);
        ventana1.setMessage(R.string.dialog_auto_reconnect);
        ventana1.setIcon(android.R.drawable.ic_dialog_info);
        ventana1.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Preferences.this, R.string.dialog_closed, Toast.LENGTH_LONG).show();
            }
        });
        ventana1.show();
    }

    /**
     * Toggle the radio group for the copy UID format options
     * @param view The View object that triggered the method
     * (in this case the info on auto copy UID button).
     */
    public void toggleUIDFormat(View view) {
        for (int i = 0; i < mUIDFormatRadioGroup.getChildCount(); i++) {
            mUIDFormatRadioGroup.getChildAt(i).setEnabled(
                    mPrefAutoCopyUID.isChecked());
        }
    }

    /**
     * Convenience method for converting selected radio item to an int
     * @return the sequence number of the radio button (0=Hex; 1=DecBE; 2=DecLE)
     * Defaults to 0 (Hex) if all else fails!
     */
    private int getUIDFormatSequence() {
        switch(mUIDFormatRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButtonHex:
                return 0;
            case R.id.radioButtonDecBE:
                return 1;
            case R.id.radioButtonDecLE:
                return 2;
        }
        return 0;
    }

    /**
     * Sets the correct radio, reverse of getUIDFormatSequence
     * @param seq the radio button sequence to select (0=Hex; 1=DecBE; 2=DecLE)
     * Defaults to 0 (Hex) if all else fails!
     */
    private void setUIDFormatBySequence(int seq) {
        RadioButton selectRadioButton;
        int rBID;
        switch(seq) {
            case 2:
                rBID = R.id.radioButtonDecLE;
                break;
            case 1:
                rBID = R.id.radioButtonDecBE;
                break;
            default:
                rBID = R.id.radioButtonHex;
        }
        selectRadioButton = (RadioButton) findViewById(rBID);
        selectRadioButton.toggle();
    }

    /**
     * Enable or disable the custom sector count text box according to the
     * checkbox state.
     * @param view The View object that triggered the method
     * (in this case the use custom sector count checkbox).
     */
    public void onUseCustomSectorCountChanged(View view) {
        mCustomSectorCount.setEnabled(mUseCustomSectorCount.isChecked());
    }

    /**
     * Enable or disable the retry authentication count text box according
     * to the checkbox state.
     * @param view The View object that triggered the method
     * (in this case the use retry authentication checkbox).
     */
    public void onUseRetryAuthenticationChanged(View view) {
        mRetryAuthenticationCount.setEnabled(
                mUseRetryAuthentication.isChecked());
    }


    /**
     * Show information on the "use custom sector count" preference.
     * @param view The View object that triggered the method
     * (in this case the info on custom sector count button).
     */
    public void onShowCustomSectorCountInfo(View view) {
        AlertDialog.Builder ventana2 = new AlertDialog.Builder(this);
        ventana2.setTitle(R.string.dialog_custom_sector_count_title);
        ventana2.setMessage(R.string.dialog_custom_sector_count);
        ventana2.setIcon(android.R.drawable.ic_dialog_info);
        ventana2.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Preferences.this, R.string.dialog_closed, Toast.LENGTH_LONG).show();
                    }
                });
        ventana2.show();
    }

    /**
     * Show information on the "use internal storage" preference.
     * @param view The View object that triggered the method
     * (in this case the info on use internal storage button).
     */
    public void onShowUseInternalStorageInfo(View view) {
        AlertDialog.Builder ventana3 = new AlertDialog.Builder(this);
        ventana3.setTitle(R.string.dialog_use_internal_storage_title);
        ventana3.setMessage(R.string.dialog_use_internal_storage);
        ventana3.setIcon(android.R.drawable.ic_dialog_info);
        ventana3.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Preferences.this, R.string.dialog_closed, Toast.LENGTH_LONG).show();
                    }
                });
        ventana3.show();
    }

    /**
     * Show information on the "retry authentication" preference.
     * @param view The View object that triggered the method
     * (in this case the info on use internal storage button).
     */
    public void onShowRetryAuthenticationInfo(View view) {
        AlertDialog.Builder ventana4 = new AlertDialog.Builder(this);
        ventana4.setTitle(R.string.dialog_retry_authentication_title);
        ventana4.setMessage(R.string.dialog_retry_authentication);
        ventana4.setIcon(android.R.drawable.ic_dialog_info);
        ventana4.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Preferences.this, R.string.dialog_closed, Toast.LENGTH_LONG).show();
                    }
                });
        ventana4.show();
    }

    /**
     * Save the preferences (to the application context,
     * {@link Common#getPreferences()}).
     * @param view The View object that triggered the method
     * (in this case the save button).
     */
    public void onSave(View view) {
        // Check if settings are valid.
        int customSectorCount = Integer.parseInt(
                mCustomSectorCount.getText().toString());
        if (customSectorCount > 40 || customSectorCount <= 0) {
            Toast.makeText(this, R.string.info_sector_count_error,
                    Toast.LENGTH_LONG).show();
            return;
        }
        int retryAuthenticationCount = Integer.parseInt(
                mRetryAuthenticationCount.getText().toString());
        if (retryAuthenticationCount > 1000 || retryAuthenticationCount <= 0) {
            Toast.makeText(this,
                    R.string.info_retry_authentication_count_error,
                    Toast.LENGTH_LONG).show();
            return;
        }


        // Save preferences.
        SharedPreferences.Editor edit = Common.getPreferences().edit();
        edit.putBoolean(Preference.AutoReconnect.toString(),
                mPrefAutoReconnect.isChecked());
        edit.putBoolean(Preference.AutoCopyUID.toString(),
                mPrefAutoCopyUID.isChecked());
        edit.putInt(Preference.UIDFormat.toString(),getUIDFormatSequence());
        edit.putBoolean(Preference.SaveLastUsedKeyFiles.toString(),
                mPrefSaveLastUsedKeyFiles.isChecked());
        edit.putBoolean(Preference.UseCustomSectorCount.toString(),
                mUseCustomSectorCount.isChecked());
        edit.putBoolean(Preference.UseInternalStorage.toString(),
                mUseInternalStorage.isChecked());
        edit.putBoolean(Preference.UseRetryAuthentication.toString(),
                mUseRetryAuthentication.isChecked());
        edit.putInt(Preference.CustomSectorCount.toString(),
                customSectorCount);
        edit.putInt(Preference.RetryAuthenticationCount.toString(),
                retryAuthenticationCount);
        edit.apply();

        // Exit the preferences view.
        finish();
    }

    /**
     * Exit the preferences view without saving anything.
     * @param view The View object that triggered the method
     * (in this case the cancel button).
     */
    public void onCancel(View view) {
        finish();
    }
}