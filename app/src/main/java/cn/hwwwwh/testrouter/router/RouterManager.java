package cn.hwwwwh.testrouter.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hwwwwh.testrouter.router.bean.Router;
import cn.hwwwwh.testrouter.router.handler.CustomRouterHandler;

public class RouterManager {

    private static RouterManager mInstance;
    private static Context mContext;
    private Map<String, Router> mRouterMap;
    private Bundle bundle;
    private String routerName;
    private Map<String, CustomRouterHandler> handlerMap;

    public RouterManager() {
    }

    public static RouterManager getInstance() {
        if (mInstance == null) {
            synchronized (RouterManager.class) {
                if (mInstance == null) {
                    mInstance = new RouterManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化router
     * @param context
     */
    public void init(Context context) {
        mRouterMap = new HashMap<>();
        // 防止传进Activity造成内存泄露
        mContext = context.getApplicationContext();
        // 初始化路由列表
        String result = initRouterFile(mContext);
        addRouter(result);
    }

    /**
     * 注册自定义scheme解析
     * @param scheme
     * @param customRouterHandler
     */
    public void registerRouterHandler(String scheme, CustomRouterHandler customRouterHandler) {
        if (handlerMap == null) handlerMap = new HashMap<>();
//        if (scheme.equals("activity")) throw new IllegalArgumentException("scheme error");
        handlerMap.put(scheme, customRouterHandler);
    }

    /**
     * 注销自定义scheme解析
     * @param scheme
     */
    public void unRegisterRouterHandler(String scheme) {
        if (handlerMap != null) handlerMap.remove(scheme);
    }

    /**
     * 通过json添加router
     * @param jsonData
     * @return
     */
    public boolean addRouter(String jsonData) {
        try {
            // 转为对象
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Router>>(){}.getType();
            List<Router> list = gson.fromJson(jsonData, listType);
            // 添加进Map
            for (Router router : list) {
                mRouterMap.put(router.getName(), router);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 添加单个router
     * @param routerName
     * @param url
     * @param path
     * @return router
     */
    public Router addRouter(String routerName, String url, String path) {
        Router router = new Router(routerName, url, path);
        return mRouterMap.put(routerName, router);
    }

    /**
     * 添加路由名参数(必须)
     * @param routerName
     * @return
     */
    public RouterManager withRouterName(String routerName) {
        this.routerName = routerName;
        return getInstance();
    }

    /**
     * 添加bundle用于activity跳转
     * @param bundle
     * @return
     */
    public RouterManager withBundle(Bundle bundle) {
        this.bundle = bundle;
        return getInstance();
    }

    /**
     * 跳转路由
     * @return 调用成功与否
     */
    public boolean goRouter() {
        return goRouter(null, -1);
    }

    /**
     * 跳转路由
     * @param activity
     * @param requestCode
     * @return 调用成功与否
     */
    public boolean goRouter(Activity activity, int requestCode) {
        if (routerName == null || routerName.equals("")) throw new IllegalArgumentException("router name is required");
        Router router = getRouter(routerName);
        if (router != null) {
            URI uri = URI.create(router.getUrl());
            String scheme = uri.getScheme();
            if (scheme.equals("activity") && !handlerMap.containsKey(scheme)) { // 未设置activity处理使用默认
                String className = router.getPath();
                if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
                    if (bundle == null)
                        bundle = new Bundle();
                    // convert query
                    Map<String, String> params = getQueryParams(uri.getQuery());
                    for (String str : params.keySet()) {
                        bundle.putString(str, params.get(str));
                    }
                }
                try {
                    Class activityClass = Class.forName(className);
                    Intent intent = new Intent(mContext, activityClass);
                    if (bundle != null) {
                        intent.putExtras(bundle);
                    }
                    if (activity != null) {
                        activity.startActivityForResult(intent, requestCode);
                    } else {
                        mContext.startActivity(intent);
                    }
                    // reset params
                    resetParams();
                    return true;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (handlerMap != null && handlerMap.containsKey(scheme)) {
                    CustomRouterHandler customRouterHandler = handlerMap.get(scheme);
                    if (customRouterHandler != null) {
                        Map<String, String> params = null;
                        if (uri.getQuery() != null && !uri.getQuery().isEmpty()) {
                            params = getQueryParams(uri.getQuery());
                        }
                        customRouterHandler.onRouterHandler(router, params);
                        // reset params
                        resetParams();
                        return true;
                    }
                }
                Log.d(this.getClass().getName(), "scheme not found name: " + scheme);
            }
        } else {
            Log.d(this.getClass().getName(), "router not found name: " + routerName);
        }
        // reset params
        resetParams();
        return false;
    }

    /**
     * 重置参数
     */
    private void resetParams() {
        this.routerName = null;
        this.bundle = null;
    }

    /**
     * 初始化读取Router json文件
     * @param context
     */
    private String initRouterFile(Context context) {
        try {
            StringBuilder stringBuilder = new StringBuilder();

            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("router.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            String result = stringBuilder.toString();
            Log.d(this.getClass().getName(), "init router success");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据路由名获取路由详情
     * @param name
     * @return 路由对象
     */
    private Router getRouter(String name) {
        return mRouterMap.get(name);
    }

    /**
     * 解析Uri的Query参数
     * @param query
     * @return 参数Map
     */
    private Map<String, String> getQueryParams(String query) {
        Map<String, String> mapRequest = new HashMap<>();

        String[] arrSplit = query.split("[&]");
        for (String strSplit : arrSplit) {
            String [] arrSplitEqual = strSplit.split("[=]");

            if (arrSplitEqual.length > 1) {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }

        return mapRequest;
    }

}
