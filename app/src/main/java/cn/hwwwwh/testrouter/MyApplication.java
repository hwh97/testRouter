package cn.hwwwwh.testrouter;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import java.util.Map;

import cn.hwwwwh.routerlib.RouterManager;
import cn.hwwwwh.routerlib.bean.Router;
import cn.hwwwwh.routerlib.handler.CustomRouterHandler;

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        RouterManager.getInstance().init(this);
        RouterManager.getInstance().registerRouterHandler("http", new CustomRouterHandler() {
            @Override
            public void onRouterHandler(Router router, Map<String, String> params) {
                Uri uri = Uri.parse(router.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }




}
