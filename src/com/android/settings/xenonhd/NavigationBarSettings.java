package com.android.settings.nitrogen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.nitrogen.ScreenType;
import com.android.settings.nitrogen.KeyDisabler;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String NAVIGATION_BAR_TINT = "navigation_bar_tint";
    private static final String PREF_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String PREF_NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
    private static final String PREF_NAVIGATION_BAR_WIDTH = "navigation_bar_width";
    private static final String NAVIGATION_BAR_SHOW = "navigation_bar_show";

    public static final int KEY_MASK_HOME = 0x01;
    public static final int KEY_MASK_BACK = 0x02;
    public static final int KEY_MASK_MENU = 0x04;

    ListPreference mNavigationBarHeight;
    ListPreference mNavigationBarHeightLandscape;
    ListPreference mNavigationBarWidth;

    private ColorPickerPreference mNavbarButtonTint;
    private SwitchPreference mNavigationBarShow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nitrogen_settings_navigation);

        // Navigation bar button color
        mNavbarButtonTint = (ColorPickerPreference) findPreference(NAVIGATION_BAR_TINT);
        mNavbarButtonTint.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NAVIGATION_BAR_TINT, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNavbarButtonTint.setSummary(hexColor);
        mNavbarButtonTint.setNewPreviewColor(intColor);

        PreferenceScreen prefSet = getPreferenceScreen();

        // navigation bar show
        mNavigationBarShow = (SwitchPreference) findPreference(NAVIGATION_BAR_SHOW);
        updateDisableNavkeysOption();

        // navigation bar dimensions
        mNavigationBarHeight =
            (ListPreference) findPreference(PREF_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        mNavigationBarHeightLandscape =
            (ListPreference) findPreference(PREF_NAVIGATION_BAR_HEIGHT_LANDSCAPE);

        if (ScreenType.isPhone(getActivity())) {
            prefSet.removePreference(mNavigationBarHeightLandscape);
            mNavigationBarHeightLandscape = null;
        } else {
            mNavigationBarHeightLandscape.setOnPreferenceChangeListener(this);
        }

        mNavigationBarWidth =
            (ListPreference) findPreference(PREF_NAVIGATION_BAR_WIDTH);

        if (!ScreenType.isPhone(getActivity())) {
            prefSet.removePreference(mNavigationBarWidth);
            mNavigationBarWidth = null;
        } else {
            mNavigationBarWidth.setOnPreferenceChangeListener(this);
        }

        updateDimensionValues();

    }

    private void updateDimensionValues() {
        int navigationBarHeight = Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_HEIGHT, -1);
        if (navigationBarHeight == -1) {
            navigationBarHeight = (int) (getResources().getDimension(
                    com.android.internal.R.dimen.navigation_bar_height)
                    / getResources().getDisplayMetrics().density);
        }
        mNavigationBarHeight.setValue(String.valueOf(navigationBarHeight));
        mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntry());

        if (mNavigationBarHeightLandscape != null) {
            int navigationBarHeightLandscape = Settings.System.getInt(getContentResolver(),
                                Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, -1);
            if (navigationBarHeightLandscape == -1) {
                navigationBarHeightLandscape = (int) (getResources().getDimension(
                        com.android.internal.R.dimen.navigation_bar_height_landscape)
                        / getResources().getDisplayMetrics().density);
            }
            mNavigationBarHeightLandscape.setValue(String.valueOf(navigationBarHeightLandscape));
            mNavigationBarHeightLandscape.setSummary(mNavigationBarHeightLandscape.getEntry());
        }

        if (mNavigationBarWidth != null) {
            int navigationBarWidth = Settings.System.getInt(getContentResolver(),
                                Settings.System.NAVIGATION_BAR_WIDTH, -1);
            if (navigationBarWidth == -1) {
                navigationBarWidth = (int) (getResources().getDimension(
                        com.android.internal.R.dimen.navigation_bar_width)
                        / getResources().getDisplayMetrics().density);
            }
            mNavigationBarWidth.setValue(String.valueOf(navigationBarWidth));
            mNavigationBarWidth.setSummary(mNavigationBarWidth.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavigationBarShow) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), NAVIGATION_BAR_SHOW,
                    value ? 1 : 0);
            return true;
        } else if (preference == mNavigationBarWidth) {
            int index = mNavigationBarWidth.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH, Integer.parseInt((String) objValue));
            updateDimensionValues();
            return true;
        } else if (preference == mNavigationBarHeight) {
            int index = mNavigationBarHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, Integer.parseInt((String) objValue));
            updateDimensionValues();
            return true;
        } else if (preference == mNavigationBarHeightLandscape) {
            int index = mNavigationBarHeightLandscape.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, Integer.parseInt((String) objValue));
            updateDimensionValues();
            return true;
        } else if (preference == mNavbarButtonTint) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_TINT, intHex);
            return true;
        }
            return false;
    }

    private static void writeDisableNavkeysOption(Context context, boolean enabled) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final int defaultBrightness = context.getResources().getInteger(
                com.android.internal.R.integer.config_buttonBrightnessSettingDefault);

        final int deviceKeys = context.getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);

        final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;
        final boolean hasMenuKey = (deviceKeys & KEY_MASK_MENU) != 0;
        final boolean hasBackKey = (deviceKeys & KEY_MASK_BACK) != 0;

        final boolean hasHWkeys = (hasHomeKey || hasMenuKey || hasBackKey);
                
        if (hasHWkeys) {
    	    Settings.System.putInt(context.getContentResolver(),
        	    Settings.System.NAVIGATION_BAR_SHOW, enabled ? 1 : 0);
    	    KeyDisabler.setActive(enabled);
        } else {
    	    Settings.System.putInt(context.getContentResolver(),
        	    Settings.System.NAVIGATION_BAR_SHOW, 1);
        }        

        if (enabled) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, 0);
        } else {
            int oldBright = prefs.getInt(ButtonBacklightBrightness.KEY_BUTTON_BACKLIGHT,
                    defaultBrightness);
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, oldBright);
        }
    }

    private void updateDisableNavkeysOption() {
        boolean enabled = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NAVIGATION_BAR_SHOW, 0) != 0;

        mNavigationBarShow.setChecked(enabled);
    }

    public static void restoreKeyDisabler(Context context) {
        if (!KeyDisabler.isSupported()) {
            return;
        }

        writeDisableNavkeysOption(context, Settings.System.getInt(context.getContentResolver(),
                Settings.System.NAVIGATION_BAR_SHOW, 0) != 0);
    }

  @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mNavigationBarShow) {
            writeDisableNavkeysOption(getActivity(), mNavigationBarShow.isChecked());
            updateDisableNavkeysOption();
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.NAVBAR_SETTINGS;
    }
}
