
package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class DeveloperPreference extends Preference {

    private static final String TAG = "DeveloperPreference";

    private String nameDev;
    private String username;
    private String title;
    private static final String ANDROIDNS = "http://schemas.android.com/apk/res/android"; 
    private static final String SETTINGS = "http://schemas.android.com/apk/res/com.android.settings"; 

    public DeveloperPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
	setValuesFromXml(context,attrs);
    }
    
    private void setValuesFromXml(Context context,AttributeSet attrs) { 
         nameDev = context.getString(attrs.getAttributeResourceValue(SETTINGS, "nameDev", 0)); 
         username = context.getString(attrs.getAttributeResourceValue(SETTINGS, "twitterName", 0)); 
	 title = getAttributeStringValue(attrs, ANDROIDNS, "key", "");
         Log.i(TAG,"initialization: "+nameDev+","+username+","+title);
     } 

     private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue) { 
         String value = attrs.getAttributeValue(namespace, name); 
         if(value == null) 
             value = defaultValue; 
          
         return value; 
     }

//    @Override
//    protected View onCreateView(ViewGroup parent) {
//        super.onCreateView(parent);
//	FrameLayout mItemView = new FrameLayout(getContext());
//        LayoutInflater li = (LayoutInflater)getContext()
//		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	View layout = li.inflate(R.layout.dev_card,mItemView, true);
//
//	View layout = View.inflate(getContext(), R.layout.dev_card, null);
//        Log.i(TAG,"onCreateView: "+nameDev+","+twitterName+","+title);
//        TextView devName = (TextView) findViewById(R.id.name);
//        ImageView photoView = (ImageView) findViewById(R.id.photo);
//        Log.i(TAG,"photoView is "+photoView);

//        return layout;
//    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
	TextView devName = (TextView) view.findViewById(R.id.name);
	devName.setText(nameDev);
	ImageView photoView = (ImageView) view.findViewById(R.id.photo);
	int id = getContext().getResources().getIdentifier("com.android.settings:drawable/"+title, null, null);
	photoView.setImageResource(id);

        Log.i(TAG,"onBindView: "+nameDev+","+username+","+title);


        if (username != null) {
            final OnPreferenceClickListener openUser = new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Uri userURL = Uri.parse("http://plus.google.com/+" + username);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, userURL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getContext().startActivity(intent);
                    return true;
                }
            };

            // changed to clicking the preference to open twitter
            // it was a hit or miss to click the twitter bird
            // twitterButton.setOnClickListener(openTwitter);
        this.setOnPreferenceClickListener(openUser);

        }
    }
}
