package com.example.rally;

import android.app.Application;

import com.example.utils.CacheUtils;
import com.example.utils.DatabaseUtils;
import com.parse.Parse;


public class App extends Application {

    public static CacheUtils cacheUtils;
    @Override
    public void onCreate() {
        super.onCreate();
        cacheUtils = new CacheUtils(App.this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}
