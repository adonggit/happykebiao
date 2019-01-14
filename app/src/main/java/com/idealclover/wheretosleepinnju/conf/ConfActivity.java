package com.idealclover.wheretosleepinnju.conf;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.idealclover.wheretosleepinnju.BaseActivity;
import com.idealclover.wheretosleepinnju.R;
import com.idealclover.wheretosleepinnju.app.Constant;
import com.idealclover.wheretosleepinnju.custom.settting.SettingItemNormal;
import com.idealclover.wheretosleepinnju.utils.FileUtils;
import com.idealclover.wheretosleepinnju.utils.Preferences;
import com.idealclover.wheretosleepinnju.utils.RequestPermission;
import com.idealclover.wheretosleepinnju.utils.ToastUtils;
import com.idealclover.wheretosleepinnju.utils.spec.SelectImageHelper;

/**
 * Created by xxyangyoulin on 2018/3/13.
 */

public class ConfActivity extends BaseActivity implements ConfContract.View, SettingItemNormal.SettingOnClickListener {

    private static final int REQUEST_CODE_IMAGE = 100;

    private ConfPresenter mPresenter;
    private SettingItemNormal mSinBgImage;
    private SelectImageHelper mSelectImageHelper;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf);

        initBackToolbar(getString(R.string.conf));
        initView();

        mPresenter = new ConfPresenter(this);
    }

    private void initView() {
        mSinBgImage = findViewById(R.id.sin_background_iamge);
        mSinBgImage.setSettingOnClickListener(this);

        boolean enabled = Preferences.getBoolean(getString(R.string.app_preference_bg_enabled),
                false);
        mSinBgImage.setChecked(enabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view, boolean checked) {
        switch (view.getId()) {
            case R.id.sin_background_iamge:
                confBgImage();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(View view, boolean checked) {
        switch (view.getId()) {
            case R.id.sin_background_iamge:
                changeBgState(checked);
                break;

            default:
                break;
        }
    }

    private void changeBgState(boolean checked) {
        Preferences.putBoolean(getString(R.string.app_preference_bg_enabled), checked);

        if (checked){
            toast(getString(R.string.bg_enabled));
        }else{
            toast(getString(R.string.bg_disabled));
        }

        notifiUpdateMainPage(Constant.INTENT_UPDATE_TYPE_OTHER);
        return;
    }

    @Override
    public void confBgImage() {
        RequestPermission.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .request(new RequestPermission.Callback() {
                    @Override
                    public void onGranted() {
                        selectImage();
                    }

                    @Override
                    public void onDenied() {
                        ToastUtils.show(getString(R.string.storage_explanation));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void selectImage() {
        if (mSelectImageHelper == null) {
            mSelectImageHelper = new SelectImageHelper(this);
        }
        mSelectImageHelper.start(REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_IMAGE:
                Uri uri;
                if (data != null && (uri = data.getData()) != null) {
                    String sourceFilePath = FileUtils.getFilePathFromContentUri(uri,
                            getContentResolver());

                    if (!TextUtils.isEmpty(sourceFilePath)) {
                        //直接保存图片的路径得了 然后设置到图片上
                        Preferences.putString(getString(R.string.app_preference_bg_iamge_path),
                                sourceFilePath);
                        //
                        notifiUpdateMainPage(Constant.INTENT_UPDATE_TYPE_OTHER);
                        toast(getString(R.string.select_iamge_succeed));
                    } else {
                        toast(getString(R.string.select_image_failed));
                    }
                }

                break;
        }
    }
}
