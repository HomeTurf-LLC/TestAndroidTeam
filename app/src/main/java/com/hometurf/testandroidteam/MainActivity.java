package com.hometurf.testandroidteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.hometurf.android.HomeTurfWebViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchHomeTurf(View view) {
        // Start application and set contentView
        Resources applicationContextResources = getApplicationContext().getResources();
        String useNativeAuth0 = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_use_auth0);
        if (useNativeAuth0.equals("true")) {
            String auth0Audience = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_audience);
            String auth0ClientId = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_client_id);
            String auth0Domain = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_domain);
            String scheme = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_scheme);
            HomeTurfWebViewActivity.setAuth0Service(new TeamHomeTurfAuth0Service(auth0Audience, auth0ClientId, auth0Domain, scheme));
        }
        Intent i = new Intent(MainActivity.this, HomeTurfWebViewActivity.class);
        MainActivity.this.startActivity(i);
    }
}