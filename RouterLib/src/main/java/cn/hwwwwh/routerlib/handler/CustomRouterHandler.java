package cn.hwwwwh.routerlib.handler;

import java.util.Map;

import cn.hwwwwh.routerlib.bean.Router;

public interface CustomRouterHandler {

    void onRouterHandler(Router router, Map<String, String> params);

}
