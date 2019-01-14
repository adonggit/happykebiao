package com.idealclover.wheretosleepinnju.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.idealclover.wheretosleepinnju.R;
import com.idealclover.wheretosleepinnju.app.app;
import com.idealclover.wheretosleepinnju.data.bean.Course;
import com.idealclover.wheretosleepinnju.data.bean.Version;
import com.idealclover.wheretosleepinnju.data.db.CourseDbDao;
import com.idealclover.wheretosleepinnju.http.HttpCallback;
import com.idealclover.wheretosleepinnju.http.HttpUtils;
import com.idealclover.wheretosleepinnju.utils.ActivityUtil;
import com.idealclover.wheretosleepinnju.utils.DialogHelper;
import com.idealclover.wheretosleepinnju.utils.LogUtil;
import com.idealclover.wheretosleepinnju.utils.Preferences;
import com.idealclover.wheretosleepinnju.utils.ToastUtils;
import com.idealclover.wheretosleepinnju.utils.VersionUpdate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mnnyang on 17-10-19.
 */

public class SettingPresenter implements SettingContract.Presenter {

    private SettingContract.View mView;

    public SettingPresenter(SettingContract.View view) {
        this.mView = view;
    }

    @Override
    public void start() {
        //nothing to do
    }

    @Override
    public void feedback() {
        if (!QQIsAvailable()) {
            mView.showNotice(app.mContext.getString(R.string.qq_not_installed));
            return;
        }

        String url1 = "mqqwpa://im/chat?chat_type=wpa&uin=" + app.mContext.getString(R.string.qq_number);
        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));

        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i1.setAction(Intent.ACTION_VIEW);

        app.mContext.startActivity(i1);
    }

    private boolean QQIsAvailable() {
        final PackageManager mPackageManager = app.mContext.getPackageManager();
        List<PackageInfo> pinfo = mPackageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
                if (pn.equals("com.tencent.tim")){
                    return true;
                }
            }
        }
        return false;
    }
}
