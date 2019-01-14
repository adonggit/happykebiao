package com.idealclover.wheretosleepinnju.custom.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import com.idealclover.wheretosleepinnju.R;


/**
 * shape选择器生成工具
 * Created by mnnyang on 17-10-22.
 */

public class Utils {
    public static GradientDrawable
    getDrawable(Context context, int rgb,
                float radius, int stroke, int strokeColor) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(rgb);
        gradientDrawable.setCornerRadius(dip2px(context, radius));
        gradientDrawable.setStroke(dip2px(context, stroke), strokeColor);
        return gradientDrawable;
    }

    public static StateListDrawable
    getPressedSelector(Context context, int color, int pressedColor, float radius) {
        GradientDrawable normalD = getDrawable(context, color, radius, 0, 0);
        GradientDrawable pressedD = getDrawable(context, pressedColor, radius, 0, 0);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressedD);
        drawable.addState(new int[]{}, normalD);
        return drawable;
    }

    public static int[] getColors(Context context) {
        Resources resources = context.getResources();

        return new int[]{
                getColor(resources, R.color.color_g),
                getColor(resources, R.color.color_g_pressed),
                getColor(resources, R.color.color_er),
                getColor(resources, R.color.color_er_pressed),
                getColor(resources, R.color.color_san),
                getColor(resources, R.color.color_san_pressed),
                getColor(resources, R.color.color_si),
                getColor(resources, R.color.color_si_pressed),
                getColor(resources, R.color.color_wu),
                getColor(resources, R.color.color_wu_pressed),
                getColor(resources, R.color.color_liu),
                getColor(resources, R.color.color_liu_pressed),
                getColor(resources, R.color.color_qi),
                getColor(resources, R.color.color_qi_pressed),
                getColor(resources, R.color.color_ba),
                getColor(resources, R.color.color_ba_pressed),
                getColor(resources, R.color.color_jiu),
                getColor(resources, R.color.color_jiu_pressed),
                getColor(resources, R.color.color_yisan),
                getColor(resources, R.color.color_yisan_pressed),
                getColor(resources, R.color.color_yis),
                getColor(resources, R.color.color_yis_pressed),
                getColor(resources, R.color.color_yiwu),
                getColor(resources, R.color.color_yiwu_pressed),
                getColor(resources, R.color.color_yiliu),
                getColor(resources, R.color.color_yiliu_pressed),
                getColor(resources, R.color.color_wuw),
                getColor(resources, R.color.color_wuw_pressed),
        };
    }

    public static int getColor(Resources resources, int colorId) {
        return resources.getColor(colorId);
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) (0.5f + dpValue * context.getResources().getDisplayMetrics().density);
    }
}


