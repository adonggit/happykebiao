package com.idealclover.wheretosleepinnju.app;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.idealclover.wheretosleepinnju.utils.CheckUpdateUtil;
import com.idealclover.wheretosleepinnju.utils.Preferences;
import com.idealclover.wheretosleepinnju.utils.ScreenUtils;
import com.idealclover.wheretosleepinnju.utils.ToastUtils;
import com.lsh.packagelibrary.CasePackageApp;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 * Created by mnnyang on 17-11-1.
 */

public class app extends CasePackageApp {
    public static Context mContext;
    public static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mHandler = new Handler(Looper.getMainLooper());
        initOkHttp();
        initUtils();
    }

    private void initOkHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .followRedirects(false)  //禁制OkHttp的重定向操作，我们自己处理重定向
                .followSslRedirects(false)
                .cookieJar(new LocalCookieJar())   //为OkHttp设置自动携带Cookie的功能
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(new CookieJarImpl(new PersistentCookieStore(getBaseContext()))) //要在内存Cookie前
                .cookieJar(new CookieJarImpl(new MemoryCookieStore()))//内存Cookie
                .cache(null)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    //CookieJar是用于保存Cookie的
    static class  LocalCookieJar implements CookieJar {
        List<Cookie> cookies;

        @Override
        public List<Cookie> loadForRequest(HttpUrl arg0) {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();
        }

        @Override
        public void saveFromResponse(HttpUrl arg0, List<Cookie> cookies) {
            System.out.println("----------------saveFromResponse");
            for (Cookie cookie : cookies) {
                System.out.println(cookie);
            }
            this.cookies = cookies;
        }

    }

    private void initUtils() {
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        ScreenUtils.init(mContext);
        CheckUpdateUtil.init(mContext);
    }
}
