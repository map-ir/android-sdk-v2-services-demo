package ir.map.sdkdemo_service;

import android.app.Application;

import ir.map.sdk_map.MapSDK;
import ir.map.sdk_services.ServiceSDK;

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MapSDK.init(this);
        ServiceSDK.init(this);
    }
}
