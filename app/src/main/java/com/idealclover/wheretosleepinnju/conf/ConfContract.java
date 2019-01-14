package com.idealclover.wheretosleepinnju.conf;

import com.idealclover.wheretosleepinnju.BasePresenter;
import com.idealclover.wheretosleepinnju.BaseView;

/**
 * Created by mnnyang on 17-11-3.
 */

public interface ConfContract {
    interface Presenter extends BasePresenter {
    }

    interface View extends BaseView<Presenter> {
        void confBgImage();
    }
}
