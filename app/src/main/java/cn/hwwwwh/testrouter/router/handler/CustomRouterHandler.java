package cn.hwwwwh.testrouter.router.handler;

import java.util.Map;

import cn.hwwwwh.testrouter.router.bean.Router;

public interface CustomRouterHandler {

    void onRouterHandler(Router router, Map<String, String> params);

}
