package com.mapbar.android.obd.rearview.framework.model;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class MAnimation {
    public final static Animation push_left_in;
    public final static Animation push_left_part_in;
    public final static Animation push_left_out;
    public final static Animation push_right_in;
    public final static Animation push_right_out;
    public final static Animation push_up_in;
    public final static Animation push_up_in1;
    public final static Animation push_up_out;
    public final static Animation push_up_out1;
    public final static Animation push_down_in;
    public final static Animation push_down_out;
    public final static Animation push_down_out1;
    public final static Animation fade_in;
    public final static Animation fade_in1;
    public final static Animation fade_out;
    public final static Animation fade_in_map;
    public final static Animation fade_in_map1;
    public final static Animation fade_out_map;
    public final static Animation fade_out_map1;
    public final static Animation zoom_in;
    public final static Animation zoom_out;
    public final static AnimationSet hyperspace_in;
    public final static AnimationSet hyperspace_out;
    public final static Animation push_none;
    private final static long DEFAULT_Duration = 400;

	/*
     * private static Animation solid_in; private static Animation solid_out;
	 * 
	 * private static Animation solid_in_right; private static Animation
	 * solid_out_right;
	 */

    static {
        push_left_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        // push_left_in.setInterpolator(new AccelerateInterpolator());
        push_left_in.setDuration(DEFAULT_Duration);
        push_left_out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        // push_left_out.setInterpolator(new AccelerateInterpolator());
        push_left_out.setDuration(DEFAULT_Duration);

        push_left_part_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        push_left_part_in.setDuration(500);

        push_right_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        // push_right_in.setInterpolator(new AccelerateInterpolator());
        push_right_in.setDuration(DEFAULT_Duration);
        push_right_out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        // push_right_out.setInterpolator(new AccelerateInterpolator());
        push_right_out.setDuration(DEFAULT_Duration);

        push_up_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        push_up_in.setDuration(400);
        push_up_in1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.657f, Animation.RELATIVE_TO_SELF, 0);
        push_up_in1.setDuration(400);
        push_up_out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1);
        push_up_out.setDuration(DEFAULT_Duration);
        push_up_out1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.64f);
        push_up_out1.setDuration(800);

        push_down_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        push_down_in.setDuration(DEFAULT_Duration);
        push_down_out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        push_down_out.setDuration(400);
        push_down_out1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.675f);
        // push_down_out1.setFillAfter(true);
        push_down_out1.setDuration(400);

        fade_in1 = new AlphaAnimation(0, 1);
        fade_in1.setDuration(400);
        fade_in = new AlphaAnimation(0, 1);
        fade_in.setDuration(800);
        // fade_in.setStartOffset(600);
        fade_out = new AlphaAnimation(1, 0);
        fade_out.setDuration(400);

        fade_in_map = new AlphaAnimation(0, 1);
        fade_in_map.setDuration(400);
        fade_in_map1 = new AlphaAnimation(0, 1);
        fade_in_map1.setDuration(400);
        fade_out_map = new AlphaAnimation(1, 0);
        fade_out_map.setDuration(400);
        fade_out_map1 = new AlphaAnimation(1, 0);
        fade_out_map1.setDuration(400);

        push_none = new TranslateAnimation(0, 0, 0, 0);
        push_none.setDuration(400);

        zoom_in = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoom_in.setDuration(400);
        zoom_in.setStartOffset(400);
        zoom_out = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoom_out.setDuration(400);

        hyperspace_in = new AnimationSet(true);
        RotateAnimation rai = new RotateAnimation(0, -360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rai.setDuration(400);
        hyperspace_in.addAnimation(rai);
        hyperspace_in.addAnimation(zoom_in);
        hyperspace_in.setStartOffset(400);
        hyperspace_out = new AnimationSet(true);
        RotateAnimation rao = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rao.setDuration(400);
        hyperspace_out.addAnimation(rao);
        hyperspace_out.addAnimation(zoom_out);
    }

    public static void setListener(AnimationListener listener) {
        push_left_out.setAnimationListener(listener);
        push_right_out.setAnimationListener(listener);
    }

	/*
     * public static Animation getSolidInAnimation(float pivotX, float pivotY) {
	 * if(solid_in == null) { float centerX = pivotX / 2.0F; float centerY =
	 * pivotY / 2.0F; solid_in = new Rotate3dAnimation(-90F, 0.0F, centerX,
	 * centerY); solid_in.setDuration(300); solid_in.setFillAfter(true);
	 * solid_in.setStartOffset(300); solid_in.setInterpolator(new
	 * AccelerateInterpolator()); } return solid_in; }
	 * 
	 * public static Animation getSolidOutAnimation(float pivotX, float pivotY)
	 * { if(solid_out == null) { float centerX = pivotX / 2.0F; float centerY =
	 * pivotY / 2.0F; solid_out = new Rotate3dAnimation(0.0F, 90F, centerX,
	 * centerY); solid_out.setDuration(300); // solid_out.setFillAfter(true);
	 * solid_out.setInterpolator(new DecelerateInterpolator()); } return
	 * solid_out; }
	 * 
	 * public static Animation getSolidInRight(float pivotX, float pivotY) {
	 * if(solid_in_right == null) { float centerX = pivotX / 2.0F; float centerY
	 * = pivotY / 2.0F; solid_in_right = new Rotate3dAnimation(90F, 0.0F,
	 * centerX, centerY); solid_in_right.setDuration(300);
	 * solid_in_right.setFillAfter(true); solid_in_right.setStartOffset(300);
	 * solid_in_right.setInterpolator(new AccelerateInterpolator()); } return
	 * solid_in_right; }
	 * 
	 * public static Animation getSolidOutRight(float pivotX, float pivotY) {
	 * if(solid_out_right == null) { float centerX = pivotX / 2.0F; float
	 * centerY = pivotY / 2.0F; solid_out_right = new Rotate3dAnimation(0.0F,
	 * -90F, centerX, centerY); solid_out_right.setDuration(300); //
	 * solid_out_right.setFillAfter(true); solid_out_right.setInterpolator(new
	 * DecelerateInterpolator()); } return solid_out_right; }
	 */
}
