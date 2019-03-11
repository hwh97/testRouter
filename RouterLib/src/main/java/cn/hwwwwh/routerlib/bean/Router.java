package cn.hwwwwh.routerlib.bean;

public class Router {
    /**
     * name : test
     * url : activity://Test1Activity/
     */

    private String name;
    private String url;
    private String path;

    public Router() {
    }

    public Router(String name, String url, String path) {
        this.name = name;
        this.url = url;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
