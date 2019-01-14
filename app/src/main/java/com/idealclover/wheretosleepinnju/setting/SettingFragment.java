package com.idealclover.wheretosleepinnju.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.idealclover.wheretosleepinnju.BaseActivity;
import com.idealclover.wheretosleepinnju.R;
import com.idealclover.wheretosleepinnju.add.AddActivity;
import com.idealclover.wheretosleepinnju.app.Constant;
import com.idealclover.wheretosleepinnju.app.app;
import com.idealclover.wheretosleepinnju.course.CourseActivity;
import com.idealclover.wheretosleepinnju.mg.MgActivity;
import com.idealclover.wheretosleepinnju.impt.ImptActivity;
import com.idealclover.wheretosleepinnju.utils.ActivityUtil;
import com.idealclover.wheretosleepinnju.utils.DialogHelper;
import com.idealclover.wheretosleepinnju.utils.DialogListener;
import com.idealclover.wheretosleepinnju.utils.Preferences;
import com.idealclover.wheretosleepinnju.utils.ScreenUtils;
import com.idealclover.wheretosleepinnju.utils.ToastUtils;

import java.util.List;

import static com.idealclover.wheretosleepinnju.app.Constant.themeColorArray;
import static com.idealclover.wheretosleepinnju.app.Constant.themeNameArray;

public class SettingFragment extends PreferenceFragment implements SettingContract.View {

    private SettingPresenter mPresenter;
    private DialogHelper mDeleteDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.page_setting);

        mPresenter = new SettingPresenter(this);
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String title = (String) preference.getTitle();

        if (title.equals(getString(R.string.user_add))) {
            gotoAddActivity();
            return true;
        } else if (title.equals(getString(R.string.import_nju))) {
            gotoImptActivity();
            return true;
        } else if (title.equals(getString(R.string.kb_manage))) {
            gotoMgActivity();
            return true;
        } else if (title.equals(getString(R.string.del_all))) {
            return true;
        } else if (title.equals(getString(R.string.feedback))) {
            feedback();
            return true;
        } else if (title.equals(getString(R.string.hide_fab))) {
            ((BaseActivity) getActivity()).notifiUpdateMainPage(Constant.INTENT_UPDATE_TYPE_OTHER);
            return true;
        } else if (title.equals(getString(R.string.show_noon_course))) {
            ((BaseActivity) getActivity()).notifiUpdateMainPage(Constant.INTENT_UPDATE_TYPE_COURSE);
            return true;
        } else if (title.equals(getString(R.string.about))) {
//            ((SettingActivity) getActivity()).addAboutFragment();
            return true;
        } else if (title.equals(getString(R.string.theme_preference))) {
            showThemeDialog();
            return true;
        } else if (title.equals(getString(R.string.other_preference))) {
            ToastUtils.show("正在努力开发中...");
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    int theme;

    private void showThemeDialog() {
        ScrollView scrollView = new ScrollView(getActivity());
        RadioGroup radioGroup = new RadioGroup(getActivity());
        scrollView.addView(radioGroup);
        int margin = ScreenUtils.dp2px(16);
        radioGroup.setPadding(margin / 2, margin, margin, margin);

        for (int i = 0; i < themeColorArray.length; i++) {
            AppCompatRadioButton arb = new AppCompatRadioButton(getActivity());

            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            arb.setLayoutParams(params);
            arb.setId(i);
            arb.setTextColor(getResources().getColor(themeColorArray[i]));
            arb.setText(themeNameArray[i]);
            arb.setTextSize(16);
            arb.setPadding(0, margin/2, 0, margin/2);
            radioGroup.addView(arb);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                theme = checkedId;
            }
        });

        DialogHelper dialogHelper = new DialogHelper();
        dialogHelper.showCustomDialog(getActivity(), scrollView, getString(R.string.theme_preference), new DialogListener() {
            @Override
            public void onPositive(DialogInterface dialog, int which) {
                super.onPositive(dialog, which);
                dialog.dismiss();
                String key = getString(R.string.app_preference_theme);
                int oldTheme = Preferences.getInt(key, 0);

                if (theme != oldTheme) {
                    Preferences.putInt(key, theme);
                    ActivityUtil.finishAll();
                    startActivity(new Intent(app.mContext, CourseActivity.class));
                }
            }
        });
    }

    private void feedback() {
        if (!QQIsAvailable(getActivity())) {
            ToastUtils.show(getString(R.string.qq_not_installed));
            return;
        }
        String url1 = "mqqwpa://im/chat?chat_type=wpa&uin=" + getString(R.string.qq_number);
        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));

        i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i1.setAction(Intent.ACTION_VIEW);

        startActivity(i1);
    }

    public static boolean QQIsAvailable(Context context) {
        final PackageManager mPackageManager = context.getPackageManager();
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

    @Override
    public void showNotice(String notice) {
        ToastUtils.show(notice);
    }


    public void gotoImptActivity() {
        ((BaseActivity) getActivity()).gotoActivity(ImptActivity.class);
        getActivity().finish();
    }

    public void gotoMgActivity() {
        ((BaseActivity) getActivity()).gotoActivity(MgActivity.class);
    }

    public void gotoAddActivity() {
        ((BaseActivity) getActivity()).gotoActivity(AddActivity.class);
    }

}