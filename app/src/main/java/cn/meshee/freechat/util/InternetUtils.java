package cn.meshee.freechat.util;

import http.HttpRequest;
import static http.HttpRequest.CHARSET_UTF8;
import static http.HttpRequest.CONTENT_TYPE_JSON;

public class InternetUtils {

    public static final String TAG = "InternetUtils";

    public static HttpRequest buildGetRequest(String url) {
        return HttpRequest.get(url).useDefaultProxy().acceptJson().connectTimeout(2000).readTimeout(2000);
    }

    private static HttpRequest buildPostRequest(String url, String sendBody) {
        return HttpRequest.post(url).useDefaultProxy().acceptJson().connectTimeout(2000).readTimeout(2000).contentType(CONTENT_TYPE_JSON, CHARSET_UTF8).send(sendBody);
    }
}
