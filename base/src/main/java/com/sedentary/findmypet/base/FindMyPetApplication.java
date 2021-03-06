package com.sedentary.findmypet.base;

import android.app.Application;
import android.content.Context;

import com.sedentary.findmypet.base.preferences.Prefs;
import com.sedentary.findmypet.base.utils.PrefUtils;
import com.sedentary.findmypet.base.utils.StorageUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by rodrigo on 24/02/15.
 */
public class FindMyPetApplication extends Application {
    private static OkHttpClient sHttpClient;

    private static Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     *
     * @return
     */
    public static OkHttpClient getHttpClient() {
        if (sHttpClient == null) {
            sHttpClient = new OkHttpClient();

            int cacheSize = 10 * 1024 * 1024;
            try {
                File cacheLocation = new File(PrefUtils.get(FindMyPetApplication.getAppContext(), Prefs.STORAGE_LOCATION, StorageUtils.getIdealCacheDirectory(FindMyPetApplication.getAppContext()).toString()));
                cacheLocation.mkdirs();
                com.squareup.okhttp.Cache cache = new com.squareup.okhttp.Cache(cacheLocation, cacheSize);
                sHttpClient.setCache(cache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sHttpClient;
    }

    /**
     *
     * @return
     */
    public static Context getAppContext() {
        return mInstance;
    }
}
