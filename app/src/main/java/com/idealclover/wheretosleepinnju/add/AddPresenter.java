package com.idealclover.wheretosleepinnju.add;

import android.text.TextUtils;

import com.idealclover.wheretosleepinnju.data.bean.Course;
import com.idealclover.wheretosleepinnju.data.db.CourseDbDao;
import com.idealclover.wheretosleepinnju.utils.LogUtil;

/**
 * Created by mnnyang on 17-11-3.
 */

public class AddPresenter implements AddContract.Presenter {
    AddContract.View mView;

    public AddPresenter(AddContract.View view) {
        mView = view;
    }

    @Override
    public void start() {
        //do nothing
    }

    @Override
    public void addCourse(Course course) {
        LogUtil.d(this, course.toString());

        if (TextUtils.isEmpty(course.getName())) {
            mView.showAddFail("请填写课程名称");
            return;
        }

        if (0 == course.getWeek()) {
            mView.showAddFail("请选择上课时间");
            return;
        }

        if (0 == course.getStartWeek()) {
            mView.showAddFail("请选择课程开始周");
            return;
        }

        CourseDbDao dao = CourseDbDao.newInstance();
        Course course1 = dao.addCourse(course);

        if (course1 != null) {
            mView.showAddFail("和课程 【" + course1.getName() + "-星期" + course1.getWeek()
                    + "第" + course1.getNodes().get(0) + "节】 " + "时间冲突");
            return;
        }
        mView.onAddSucceed(course);
    }

    @Override
    public void removeCourse(int courseId) {
        CourseDbDao.newInstance().removeCourse(courseId);
        mView.onDelSucceed();
    }

    @Override
    public void updateCourse(Course course) {
        LogUtil.d(this, course.toString());

        if (TextUtils.isEmpty(course.getName())) {
            mView.showAddFail("请填写课程名称");
            return;
        }

        if (0 == course.getWeek()) {
            mView.showAddFail("请选择上课时间");
            return;
        }

        if (0 == course.getStartWeek()) {
            mView.showAddFail("请选择课程开始周");
            return;
        }

        CourseDbDao dao = CourseDbDao.newInstance();
        Course course1 = dao.updateCourse(course);

        if (course1 != null) {
            mView.showAddFail("和课程 【" + course1.getName() + "-星期" + course1.getWeek()
                    + "第" + course1.getNodes().get(0) + "节】 " + "时间冲突");
            return;
        }
        mView.onUpdateSucceed(course);
    }
}
