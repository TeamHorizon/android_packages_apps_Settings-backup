package com.android.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class About extends SettingsPreferenceFragment {


    public static final String TAG = "About";

    Preference mSiteUrl;
    Preference mSourceUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_about);
        addPreferencesFromResource(R.xml.prefs_about);
        mSiteUrl = findPreference("xenonhd_website");
        mSourceUrl = findPreference("xenonhd_source");
        
//        PreferenceGroup devsGroup = (PreferenceGroup) findPreference("devs");
//        int mSize = devsGroup.getPreferenceCount();
//	Log.i(TAG,"There are a total of "+mSize+" preferences.");
/*        ArrayList<Preference> devs = new ArrayList<Preference>();
        devs.clear();
        for (int i = 0; i < mSize; i++) {
            devs.add(devsGroup.getPreference(i));
            Log.i(TAG,"Added preference "+i+" of "+mSize);
        }
        devsGroup.removeAll();
        devsGroup.setOrderingAsAdded(false);
        for(int i = 0; i < mSize; i++) {
            Preference p = devs.get(i);
            p.setOrder(i);

            devsGroup.addPreference(p);
        }
*/
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mSiteUrl) {
            launchUrl("http://xenonhd.com");
        } else if (preference == mSourceUrl) {
            launchUrl("http://github.com/TeamHorizon");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void launchUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent website = new Intent(Intent.ACTION_VIEW, uriUrl);
        getActivity().startActivity(website);
    }
}


