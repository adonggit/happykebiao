package com.idealclover.wheretosleepinnju.impt;

import android.graphics.Bitmap;

import com.idealclover.wheretosleepinnju.R;
import com.idealclover.wheretosleepinnju.app.app;
import com.idealclover.wheretosleepinnju.data.bean.Course;
import com.idealclover.wheretosleepinnju.data.bean.CourseTime;
import com.idealclover.wheretosleepinnju.data.db.CourseDbDao;
import com.idealclover.wheretosleepinnju.http.HttpCallback;
import com.idealclover.wheretosleepinnju.http.HttpUtils;
import com.idealclover.wheretosleepinnju.utils.ToastUtils;
import com.idealclover.wheretosleepinnju.utils.spec.ParseCourse;
import com.idealclover.wheretosleepinnju.utils.LogUtil;
import com.idealclover.wheretosleepinnju.utils.Preferences;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mnnyang on 17-10-23.
 * Changed by idealclover on 18-07-07
 */

public class ImptPresenter implements ImptContract.Presenter {

    private ImptContract.View mImptView;
    private ImptContract.Model mModel;
    private String mSchoolUrl;

    private String xh;
    private String mNormalCourseHtml;
    private String mSelectYear;
    private String mSelectTerm;

    public ImptPresenter(ImptContract.View imptView,String schoolUrl) {
        mImptView = imptView;
        mSchoolUrl = schoolUrl;
        mModel = new ImptModel();
    }

    @Override
    public void start() {
        getCaptcha();
    }


    @Override
    public void importCustomCourses(final String year, final String term) {
        LogUtil.d(this, "importCustomCourses");
        LogUtil.d(this, "sy" + mSelectYear + "st" + mSelectTerm + "y" + year + "t" + term);
        if (year.equals(mSelectYear) && term.equals(mSelectTerm)) {
            importDefaultCourses(year, term);
            return;
        }

        mImptView.showImpting();
        HttpUtils.newInstance().toImpt(mSchoolUrl,xh, new HttpCallback<String>() {
            @Override
            public void onSuccess(String s) {
                parseCoursesHtmlToDb(s, year + "-" + term);
            }

            @Override
            public void onFail(String errMsg) {
                mImptView.hideImpting();
                mImptView.showErrToast(errMsg, true);
            }
        });
    }

    @Override
    public void importDefaultCourses(final String year, final String term) {
        LogUtil.d(this, "importCustomCourses");
        mImptView.showImpting();
        parseCoursesHtmlToDb(mNormalCourseHtml, year + "-" + term);
    }

    @Override
    public void loadCourseTimeAndTerm(final String xh, String pwd, String captcha) {
        if (!verify(xh, pwd, captcha)) return;
        mImptView.showImpting();
        HttpUtils.newInstance().login(mSchoolUrl,xh, pwd, captcha, null, null,
                new HttpCallback<String>() {

                    @Override
                    public void onSuccess(String s) {
                        ImptPresenter.this.xh = xh;
                        mNormalCourseHtml = s;
                        mImptView.hideImpting();
                        parseTimeTermHtmlToShow(s);
                    }

                    @Override
                    public void onFail(String errMsg) {
                        mImptView.hideImpting();
                        mImptView.showErrToast(errMsg, true);
                    }
                });

    }

    private void parseTimeTermHtmlToShow(String html) {
        CourseTime ct = ParseCourse.parseTime(html);

        if (ct == null || ct.years.size() == 0) {
            mImptView.showErrToast("导入学期失败", true);
            return;
        }
        mSelectYear = ct.selectYear;
        mSelectTerm = ct.selectTerm;
        mImptView.showCourseTimeDialog(ct);
    }

    private void parseCoursesHtmlToDb(final String html, final String courseTimeTerm) {
        try {
            Observable.create(new Observable.OnSubscribe<String>() {

                @Override
                public void call(Subscriber<? super String> subscriber) {
                    final ArrayList<Course> courses = ParseCourse.parse(html);

                    //删除旧数据
                    CourseDbDao.newInstance().removeByCsName(courseTimeTerm);

                    //添加新数据
                    for (Course c : courses) {
                        c.setCsName(courseTimeTerm);
                        CourseDbDao.newInstance().addCourse(c);
                    }

                    subscriber.onNext("导入成功");
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {

                        @Override
                        public void onCompleted() {
                            LogUtil.d(this, "完成");
                        }

                        @Override
                        public void onError(Throwable e) {
                            mImptView.hideImpting();
                            mImptView.showErrToast("插入数据库失败", true);
                        }

                        @Override
                        public void onNext(String s) {

                            LogUtil.i(this,"导入成功:" + courseTimeTerm);

                            Preferences.putInt(app.mContext.getString(
                                    R.string.app_preference_current_cs_name_id),
                                    CourseDbDao.newInstance().getCsNameId(courseTimeTerm));

                            mImptView.hideImpting();
                            mImptView.showSucceed();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mImptView.hideImpting();
            mImptView.showErrToast("导入错误", true);
        }
    }


    private boolean verify(String xh, String pwd, String captcha) {
        if (xh.isEmpty()) {
            mImptView.showErrToast("请填写学号", false);
            return false;
        }

        if (pwd.isEmpty()) {
            mImptView.showErrToast("请填写密码", false);
            return false;
        }

        if (captcha.isEmpty()) {
            mImptView.showErrToast("请填写验证码", false);
            return false;
        }
        return true;
    }


    private boolean captchaIsLoading = false;

    @Override
    public void getCaptcha() {
        //防止重复点击加载验证码按钮导致多次执行
        if (captchaIsLoading){
            return;
        }

        captchaIsLoading = true;
        mImptView.captchaIsLoading(true);

        HttpUtils.newInstance().loadCaptcha(app.mContext.getCacheDir(),mSchoolUrl,
                new HttpCallback<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        mImptView.getCaptchaIV().setImageBitmap(bitmap);
                        captchaIsLoading = false;
                        mImptView.captchaIsLoading(false);
                    }

                    @Override
                    public void onFail(String errMsg) {
                        ToastUtils.show(errMsg);
                        mImptView.getCaptchaIV().setImageResource(R.drawable.ic_svg_refresh);
                        captchaIsLoading = false;
                        mImptView.captchaIsLoading(false);
                    }
                });
    }
}
