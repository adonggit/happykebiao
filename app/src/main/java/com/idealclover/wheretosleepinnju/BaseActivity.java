package com.idealclover.wheretosleepinnju;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.idealclover.wheretosleepinnju.app.Constant;
import com.idealclover.wheretosleepinnju.utils.ActivityUtil;
import com.idealclover.wheretosleepinnju.utils.LogUtil;
import com.idealclover.wheretosleepinnju.utils.Preferences;
import com.idealclover.wheretosleepinnju.utils.ToastUtils;


/**
 * Created by mnnyang on 17-10-2.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");

        initTheme();
        ActivityUtil.addActivity(this);
    }

    /**
     * 初始化toolbar功能为返回
     * @param title
     */
    protected void initBackToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public void gotoActivity(Class clzz) {
        Intent intent = new Intent(this, clzz);
        startActivity(intent);
    }

    protected void initTheme() {
        int anInt = Preferences.getInt(getString(R.string.app_preference_theme), 0);
        setTheme(Constant.themeArray[anInt]);
    }

    /**
     * 通知更新主页
     */

    public void notifiUpdateMainPage(int type) {
        Intent intent = new Intent();
        intent.setAction(Constant.INTENT_UPDATE);
        intent.putExtra(Constant.INTENT_UPDATE_TYPE, type);
        sendBroadcast(intent);
    }

    public void toast(String msg) {
        ToastUtils.show(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        ActivityUtil.removeActivity(this);
    }
}
